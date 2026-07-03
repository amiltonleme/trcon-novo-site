// Consumo de conteúdo público (highlights / news) com degradação previsível:
// tenta a API do backend quando configurada e, em qualquer falha, cai para o
// JSON estático publicado. O site nunca quebra por indisponibilidade do backend
// (doc/07-MIGRACAO-PARALELA.md — fallback por capacidade).
//
// Funções puras de render (buildHighlightsHtml / buildNewsHtml) ficam isoladas
// de DOM/rede para serem testáveis com Vitest.

import { escapeHtml, safeUrl } from './sanitize.js';

// Extrai a lista de itens do envelope canônico (ou do array puro).
export function extractItems(payload) {
  if (Array.isArray(payload)) return payload;
  if (payload && Array.isArray(payload.items)) return payload.items;
  return [];
}

// Busca com fallback: API (se houver URL) -> JSON estático. Retorna
// { items, source }. `source` é 'api' ou 'json' (útil para debug/telemetria).
export async function fetchWithFallback(apiUrl, jsonUrl, deps = {}) {
  const fetchImpl = deps.fetch || (typeof fetch !== 'undefined' ? fetch : null);
  if (!fetchImpl) throw new Error('fetch indisponível neste ambiente.');

  if (apiUrl) {
    try {
      const res = await fetchImpl(apiUrl, { headers: { Accept: 'application/json' } });
      if (res.ok) {
        return { items: extractItems(await res.json()), source: 'api' };
      }
    } catch (error) {
      // silencioso: cai para o JSON estático abaixo
    }
  }

  const res = await fetchImpl(jsonUrl, { cache: 'no-store' });
  if (!res.ok) throw new Error('Fallback indisponível: ' + jsonUrl);
  return { items: extractItems(await res.json()), source: 'json' };
}

const SIGNAL_LABEL = { up: '▲', down: '▼', flat: '•' };

// HTML de um card de highlight/radar. Puro (usa apenas sanitize).
export function buildHighlightsHtml(items) {
  if (!items || !items.length) {
    return '<div class="card"><p class="loading-row">Sem destaques no momento.</p></div>';
  }
  return items
    .map((item) => {
      const href = safeUrl(item.link || item.url);
      const titulo = escapeHtml(item.title);
      const tituloHtml = href
        ? `<a class="content-link" href="${escapeHtml(href)}" target="_blank" rel="noopener noreferrer">${titulo} →</a>`
        : titulo;
      const signal = SIGNAL_LABEL[item.signal] || '';
      return `
      <div class="card">
        <span class="card-tag">${escapeHtml(item.category || 'TRCon')}</span>
        <h3>${signal ? signal + ' ' : ''}${escapeHtml(item.title)}</h3>
        <p>${escapeHtml(item.summary || '')}</p>
        ${href ? tituloHtml : ''}
      </div>`;
    })
    .join('');
}

// HTML da lista de novidades. Puro.
export function buildNewsHtml(items) {
  if (!items || !items.length) {
    return '<div class="pillar"><div><p class="loading-row">Sem novidades no momento.</p></div></div>';
  }
  return items
    .map((item) => {
      const href = safeUrl(item.url || item.link);
      const titulo = escapeHtml(item.title);
      const tituloHtml = href
        ? `<a class="content-link" href="${escapeHtml(href)}" target="_blank" rel="noopener noreferrer">${titulo} →</a>`
        : titulo;
      return `
      <div class="pillar">
        <span class="pillar-icon">📡</span>
        <div>
          <h4>${tituloHtml}</h4>
          <p>${escapeHtml(item.summary || '')}${item.source ? ` <em>— ${escapeHtml(item.source)}</em>` : ''}</p>
        </div>
      </div>`;
    })
    .join('');
}
