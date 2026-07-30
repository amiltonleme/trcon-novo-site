// Consumo de conteúdo público (highlights / news) com degradação previsível:
// tenta a API do backend quando configurada e, em qualquer falha, cai para o
// JSON estático publicado. O site nunca quebra por indisponibilidade do backend
// (doc/07-MIGRACAO-PARALELA.md — fallback por capacidade).
//
// Funções puras de render (buildHighlightsHtml / buildNewsHtml) ficam isoladas
// de DOM/rede para serem testáveis com Vitest.

import { escapeHtml, safeUrl, localizeSiteHref } from './sanitize.js';
import { isInternalArticleHref, resolveNewsHref } from './article.js';

// Extrai a lista de itens do envelope canônico (ou do array puro).
export function extractItems(payload) {
  if (Array.isArray(payload)) return payload;
  if (payload && Array.isArray(payload.items)) return payload.items;
  return [];
}

// Busca com fallback: API (se houver URL e itens) -> JSON estático. Retorna
// { items, source }. `source` é 'api' ou 'json' (útil para debug/telemetria).
//
// Radar (highlights) e Novidades (news) usam endpoints distintos. Se a API
// responder 200 com lista vazia, cai no JSON — evita esvaziar o Radar quando
// só Novidades está sendo alimentada pelo Sirius Marketing.
export async function fetchWithFallback(apiUrl, jsonUrl, deps = {}) {
  const fetchImpl = deps.fetch || (typeof fetch !== 'undefined' ? fetch : null);
  if (!fetchImpl) throw new Error('fetch indisponível neste ambiente.');

  if (apiUrl) {
    try {
      const res = await fetchImpl(apiUrl, { headers: { Accept: 'application/json' } });
      if (res.ok) {
        const payload = await res.json();
        const items = extractItems(payload);
        if (items.length > 0) {
          return {
            items,
            source: 'api',
            disclaimer: payload.disclaimer || '',
          };
        }
      }
    } catch (error) {
      // silencioso: cai para o JSON estático abaixo
    }
  }

  const res = await fetchImpl(jsonUrl, { cache: 'no-store' });
  if (!res.ok) throw new Error('Fallback indisponível: ' + jsonUrl);
  const payload = await res.json();
  return {
    items: extractItems(payload),
    source: 'json',
    disclaimer: payload.disclaimer || '',
  };
}

function normalizeTitleKey(item) {
  return String(item?.title || '')
    .trim()
    .toLowerCase();
}

// Educação Financeira: prioriza itens da API (marketing) e completa com JSON (RSS/catálogo).
export async function loadEconomyTips(apiUrl, jsonUrl, maxItems = 4, deps = {}) {
  const fetchImpl = deps.fetch || (typeof fetch !== 'undefined' ? fetch : null);
  if (!fetchImpl) throw new Error('fetch indisponível neste ambiente.');

  let apiItems = [];
  let apiDisclaimer = '';
  if (apiUrl) {
    try {
      const res = await fetchImpl(apiUrl, { headers: { Accept: 'application/json' } });
      if (res.ok) {
        const payload = await res.json();
        apiItems = extractItems(payload);
        apiDisclaimer = payload.disclaimer || '';
      }
    } catch (error) {
      // silencioso — usa JSON abaixo
    }
  }

  let jsonItems = [];
  let jsonDisclaimer = '';
  try {
    const res = await fetchImpl(jsonUrl, { cache: 'no-store' });
    if (res.ok) {
      const payload = await res.json();
      jsonItems = extractItems(payload);
      jsonDisclaimer = payload.disclaimer || '';
    }
  } catch (error) {
    if (apiItems.length === 0) throw error;
  }

  const seen = new Set();
  const merged = [];
  for (const item of [...apiItems, ...jsonItems]) {
    const key = normalizeTitleKey(item);
    if (!key || seen.has(key)) continue;
    seen.add(key);
    merged.push(item);
    if (merged.length >= maxItems) break;
  }

  if (merged.length > 0 && merged[0]) {
    merged[0] = { ...merged[0], featured: true };
  }

  const source =
    apiItems.length > 0 && jsonItems.length > 0
      ? 'api+json'
      : apiItems.length > 0
        ? 'api'
        : 'json';

  return {
    items: merged,
    source,
    disclaimer: apiDisclaimer || jsonDisclaimer || '',
  };
}

/** Destaque enviado pelo Sirius Marketing (legado: ia também para o Radar). */
export function isEditorialHighlight(item) {
  if (!item) return false;
  const externalId = item.externalId || item.external_id || '';
  if (typeof externalId === 'string' && externalId.endsWith('-radar')) return true;
  return (item.link || item.url || '').includes('/novidades/');
}

/**
 * Remove do Radar itens editoriais; sinais do pipeline permanecem.
 */
export function filterRadarDuplicates(highlights) {
  if (!highlights?.length) return highlights || [];
  return highlights.filter((item) => !isEditorialHighlight(item));
}

/**
 * Radar: API (pipeline) → se só houver artigos editoriais, JSON estático do pipeline.
 */
export async function fetchRadarHighlights(apiUrl, jsonUrl, deps = {}) {
  const primary = await fetchWithFallback(apiUrl, jsonUrl, deps);
  const filtered = filterRadarDuplicates(primary.items);
  if (filtered.length > 0) {
    return { ...primary, items: filtered };
  }
  if (primary.source === 'api') {
    const fallback = await fetchWithFallback('', jsonUrl, deps);
    return {
      ...fallback,
      items: filterRadarDuplicates(fallback.items),
      source: 'json',
    };
  }
  return { ...primary, items: filtered };
}

const SIGNAL_LABEL = { up: '▲', down: '▼', flat: '•' };

function buildCardItemHtml(item, { preferExternalLinks = false } = {}) {
  const href = localizeSiteHref(
    preferExternalLinks
      ? safeUrl(item.link || item.url)
      : (() => {
          const resolved = resolveNewsHref(item);
          return resolved && isInternalArticleHref(resolved) ? resolved : safeUrl(resolved);
        })(),
  );
  const tag = escapeHtml(item.source || item.category || 'TRCon');
  const titulo = escapeHtml(item.title);
  const signal = SIGNAL_LABEL[item.signal] || '';
  const internal = Boolean(href && isInternalArticleHref(href));
  const linkHtml = href
    ? internal
      ? `<a class="content-link" href="${escapeHtml(href)}">${titulo} →</a>`
      : `<a class="content-link" href="${escapeHtml(href)}" target="_blank" rel="noopener noreferrer">${titulo} →</a>`
    : '';
  return `
      <div class="card">
        <span class="card-tag">${tag}</span>
        <h3>${signal ? signal + ' ' : ''}${titulo}</h3>
        <p>${escapeHtml(item.summary || '')}</p>
        ${linkHtml}
      </div>`;
}

// HTML do radar — grid de cards.
export function buildHighlightsHtml(items) {
  if (!items || !items.length) {
    return '<div class="card"><p class="loading-row">Sem destaques no momento.</p></div>';
  }
  return items.map((item) => buildCardItemHtml(item, { preferExternalLinks: true })).join('');
}

// HTML das novidades — mesmo grid de cards do radar.
export function buildNewsHtml(items) {
  if (!items || !items.length) {
    return '<div class="card"><p class="loading-row">Sem novidades no momento.</p></div>';
  }
  return items.map((item) => buildCardItemHtml(item)).join('');
}
