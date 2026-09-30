// Consumo de conteúdo público com degradação por capacidade: Radar pode usar o
// JSON estático; Novidades usa somente a API institucional e fica oculta quando
// indisponível. O site nunca quebra por falha do backend.
//
// Funções puras de render (buildHighlightsHtml / buildNewsHtml) ficam isoladas
// de DOM/rede para serem testáveis com Vitest.

import { escapeHtml, safeUrl, localizeSiteHref } from './sanitize.js?v=6872001';
import { isInternalArticleHref, resolveNewsHref } from './article.js?v=6872001';

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

// Novidades institucionais não podem usar o feed externo dos radares como
// fallback. Se a API estiver vazia ou indisponível, a seção deve ficar oculta.
export async function fetchInstitutionalNews(apiUrl, deps = {}) {
  const fetchImpl = deps.fetch || (typeof fetch !== 'undefined' ? fetch : null);
  if (!fetchImpl) throw new Error('fetch indisponível neste ambiente.');
  if (!apiUrl) return [];

  const response = await fetchImpl(apiUrl, { headers: { Accept: 'application/json' } });
  if (!response.ok) throw new Error(`Novidades indisponíveis: HTTP ${response.status}`);
  return extractItems(await response.json());
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
  const tag = escapeHtml(item.source || item.category || 'TRCONGROUP');
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

// HTML do radar — grid de cards. Lista vazia → string vazia (a seção some na home).
export function buildHighlightsHtml(items) {
  if (!items || !items.length) {
    return '';
  }
  return items.map((item) => buildCardItemHtml(item, { preferExternalLinks: true })).join('');
}

// HTML das novidades — mesmo grid de cards do radar. Lista vazia → string vazia.
export function buildNewsHtml(items) {
  if (!items || !items.length) {
    return '';
  }
  return items.map((item) => buildCardItemHtml(item)).join('');
}
