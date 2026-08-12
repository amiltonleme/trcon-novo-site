// Página de artigo /novidades/{slug} — fetch, meta SEO e render do corpo.

import { escapeHtml, safeHttpsImageUrl, videoEmbedSrc } from './sanitize.js';
import { resolveApiConfig } from './config.js';

export function parseArticleSlug(pathname = '') {
  const match = String(pathname).match(/\/novidades\/([^/?#]+)/);
  return match ? decodeURIComponent(match[1]) : null;
}

export function isInternalArticleHref(href) {
  if (!href) return false;
  if (href.startsWith('/novidades/')) return true;
  try {
    const url = new URL(href);
    return url.pathname.startsWith('/novidades/');
  } catch {
    return false;
  }
}

export function resolveNewsHref(item) {
  if (item?.slug) {
    return `/novidades/${encodeURIComponent(item.slug)}`;
  }
  const href = item?.url || item?.link;
  if (isInternalArticleHref(href)) {
    try {
      const url = new URL(href, 'https://trcongroup.com.br');
      return url.pathname;
    } catch {
      return href;
    }
  }
  return href || '';
}

function renderParagraph(paragraph) {
  const trimmed = String(paragraph || '').trim();
  if (!trimmed) return '';

  const singleLine = trimmed.includes('\n') ? null : trimmed;
  const embedSrc = singleLine ? videoEmbedSrc(singleLine) : null;
  if (embedSrc) {
    return `<div class="article-video"><iframe src="${escapeHtml(embedSrc)}" title="Vídeo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen loading="lazy" referrerpolicy="strict-origin-when-cross-origin"></iframe></div>`;
  }

  const lines = trimmed
    .split('\n')
    .map((line) => {
      const embed = videoEmbedSrc(line.trim());
      if (embed) {
        return `</p><div class="article-video"><iframe src="${escapeHtml(embed)}" title="Vídeo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen loading="lazy" referrerpolicy="strict-origin-when-cross-origin"></iframe></div><p>`;
      }
      return renderInlineMarkup(line.trim());
    })
    .filter(Boolean)
    .join('<br />');

  return `<p>${lines}</p>`;
}

function renderList(block) {
  const items = String(block || '')
    .split('\n')
    .map((line) => line.trim())
    .filter((line) => /^[-*]\s+/.test(line))
    .map((line) => line.replace(/^[-*]\s+/, '').trim())
    .filter(Boolean);
  if (!items.length) return '';
  return `<ul>${items.map((item) => `<li>${renderInlineMarkup(item)}</li>`).join('')}</ul>`;
}

function isListBlock(block) {
  const lines = String(block || '')
    .split('\n')
    .map((line) => line.trim())
    .filter(Boolean);
  return lines.length > 0 && lines.every((line) => /^[-*]\s+/.test(line));
}

function renderInlineMarkup(value) {
  const escaped = escapeHtml(value);
  return escaped.replace(/\*\*([^*\n][\s\S]*?[^*\n])\*\*/g, '<strong>$1</strong>');
}

export function renderArticleBody(body) {
  if (!body || !String(body).trim()) {
    return '<p class="article-empty">Conteúdo indisponível.</p>';
  }
  return String(body)
    .trim()
    .split(/\n\s*\n/)
    .map((block) => (isListBlock(block) ? renderList(block) : renderParagraph(block)))
    .join('');
}

export function formatArticleDate(value) {
  if (!value) return '';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return '';
  return date.toLocaleDateString('pt-BR', {
    day: '2-digit',
    month: 'long',
    year: 'numeric',
  });
}

export function buildArticleUrl(slug, siteBase = 'https://trcongroup.com.br') {
  const base = String(siteBase).replace(/\/+$/, '');
  return `${base}/novidades/${encodeURIComponent(slug)}`;
}

export function applyArticleMeta(article, deps = {}) {
  const doc = deps.document || (typeof document !== 'undefined' ? document : null);
  if (!doc || !article) return;

  const title = article.metaTitle || article.title || 'TRCon Novidades';
  const description = article.metaDescription || article.summary || '';
  const canonical = buildArticleUrl(article.slug, deps.siteBase);
  const cover = safeHttpsImageUrl(article.coverImageUrl);

  doc.title = `${title} — TRCon Group`;

  setMeta(doc, 'name', 'description', description);
  setMeta(doc, 'property', 'og:title', title);
  setMeta(doc, 'property', 'og:description', description);
  setMeta(doc, 'property', 'og:url', canonical);
  setMeta(doc, 'property', 'og:type', 'article');
  if (cover) {
    setMeta(doc, 'property', 'og:image', cover);
  }

  let canonicalLink = doc.querySelector('link[rel="canonical"]');
  if (!canonicalLink) {
    canonicalLink = doc.createElement('link');
    canonicalLink.setAttribute('rel', 'canonical');
    doc.head.appendChild(canonicalLink);
  }
  canonicalLink.setAttribute('href', canonical);
}

function setMeta(doc, attr, key, content) {
  if (!content) return;
  let tag = doc.querySelector(`meta[${attr}="${key}"]`);
  if (!tag) {
    tag = doc.createElement('meta');
    tag.setAttribute(attr, key);
    doc.head.appendChild(tag);
  }
  tag.setAttribute('content', content);
}

export async function fetchArticleBySlug(slug, deps = {}) {
  const fetchImpl = deps.fetch || (typeof fetch !== 'undefined' ? fetch : null);
  if (!fetchImpl) throw new Error('fetch indisponível neste ambiente.');

  const config = resolveApiConfig(deps);
  const base = (config.newsApiUrl || '').replace(/\/+$/, '');
  const url = `${base}/${encodeURIComponent(slug)}`;
  const res = await fetchImpl(url, { headers: { Accept: 'application/json' } });
  if (!res.ok) {
    throw new Error(`Artigo não encontrado (${res.status})`);
  }
  return res.json();
}

export function renderArticlePage(article, root, deps = {}) {
  if (!root || !article) return;

  applyArticleMeta(article, deps);

  const dateLabel = formatArticleDate(article.publishedAt);
  const category = escapeHtml(article.category || 'TRCon');
  const source = article.source ? `<span>${escapeHtml(article.source)}</span>` : '';
  const brand = article.brandSlug ? `<span>${escapeHtml(article.brandSlug)}</span>` : '';
  const cover = safeHttpsImageUrl(article.coverImageUrl);
  const coverHtml = cover
    ? `<figure class="article-cover"><img src="${escapeHtml(cover)}" alt="" loading="eager" decoding="async" /></figure>`
    : '';

  root.innerHTML = `
    <nav class="article-breadcrumb" aria-label="Breadcrumb">
      <a href="/">Home</a>
      <span aria-hidden="true">→</span>
      <span>Novidades</span>
    </nav>
    ${coverHtml}
    <header class="article-header">
      <span class="article-tag">${category}</span>
      <h1>${escapeHtml(article.title || '')}</h1>
      <p class="article-summary">${escapeHtml(article.summary || '')}</p>
      <div class="article-meta">
        ${dateLabel ? `<span>${escapeHtml(dateLabel)}</span>` : ''}
        ${source}
        ${brand}
      </div>
    </header>
    <article class="article-body">${renderArticleBody(article.body)}</article>
  `;
}
