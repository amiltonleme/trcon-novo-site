// Helpers puros de sanitização/formatação usados na composição de conteúdo.
// São funções puras (sem DOM) justamente para serem testáveis com Vitest sem
// precisar de navegador (doc/03-FRONTEND-STACK-CANONICA.md, regra 4).

export function changeClass(direction) {
  if (direction === 'up') return 'chg up';
  if (direction === 'down') return 'chg dn';
  return 'chg';
}

export function escapeHtml(value) {
  return String(value ?? '').replace(
    /[&<>"']/g,
    (char) =>
      ({
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#39;',
      })[char],
  );
}

export function safeClass(value, fallback) {
  const text = String(value || '');
  return /^[a-z0-9_-]+$/i.test(text) ? text : fallback;
}

export function safeUrl(value, base) {
  try {
    const reference =
      base || (typeof window !== 'undefined' ? window.location.href : 'http://localhost/');
    const url = new URL(String(value || ''), reference);
    return ['http:', 'https:', 'mailto:'].includes(url.protocol) ? url.href : '';
  } catch (error) {
    return '';
  }
}

/** Em localhost, reescreve links absolutos do site TRCON para caminho relativo. */
export function localizeSiteHref(href, deps = {}) {
  const raw = String(href || '').trim();
  if (!raw) return '';

  const host =
    deps.hostname ||
    (typeof window !== 'undefined' && window.location ? window.location.hostname : '');
  const isProdHost = host === 'trcongroup.com.br' || host === 'www.trcongroup.com.br';
  if (isProdHost) {
    return raw;
  }

  try {
    const base =
      deps.base ||
      (typeof window !== 'undefined' && window.location
        ? window.location.href
        : 'http://127.0.0.1:4173/');
    const url = new URL(raw, base);
    const siteHosts = new Set(['trcongroup.com.br', 'www.trcongroup.com.br']);
    if (!siteHosts.has(url.hostname)) {
      return raw.startsWith('/') ? raw : url.href;
    }
    const path = `${url.pathname}${url.search}${url.hash}`;
    return path === '' ? '/' : path;
  } catch (error) {
    return raw;
  }
}

/** URL HTTPS absoluta para imagem de capa (sem relative/mailto). */
export function safeHttpsImageUrl(value) {
  try {
    const raw = String(value || '').trim();
    if (!raw) return '';
    const converted = convertUnsplashPageToImage(raw);
    const candidate = converted || raw;
    const url = new URL(candidate);
    if (url.protocol !== 'https:') return '';
    return url.href;
  } catch (error) {
    return '';
  }
}

/** Página Unsplash → URL de download usável em &lt;img&gt;. */
export function convertUnsplashPageToImage(value) {
  const raw = String(value || '').trim();
  if (!raw) return '';
  if (raw.includes('images.unsplash.com') || raw.includes('plus.unsplash.com') || raw.includes('/download')) {
    return '';
  }
  const match = raw.match(
    /unsplash\.com\/(?:[a-z]{2}(?:-[a-z]+)?\/)?(?:photos|fotografias)\/([^?#]+)/i,
  );
  if (!match) return '';
  const slug = match[1].replace(/\/$/, '');
  const photoId = slug.includes('-') ? slug.slice(slug.lastIndexOf('-') + 1) : slug;
  if (!/^[A-Za-z0-9_-]{7,15}$/.test(photoId)) return '';
  return `https://unsplash.com/photos/${photoId}/download?force=true&w=1600`;
}

/**
 * Converte URL YouTube/Vimeo em src de iframe seguro, ou null.
 * Aceita watch, youtu.be, shorts, embed e Vimeo.
 */
export function videoEmbedSrc(value) {
  const raw = String(value || '').trim();
  if (!raw) return null;
  try {
    const url = new URL(raw);
    if (url.protocol !== 'https:' && url.protocol !== 'http:') return null;
    const host = url.hostname.replace(/^www\./, '').toLowerCase();

    if (host === 'youtu.be') {
      const id = url.pathname.split('/').filter(Boolean)[0];
      return id ? `https://www.youtube.com/embed/${encodeURIComponent(id)}` : null;
    }
    if (host === 'youtube.com' || host === 'm.youtube.com' || host === 'youtube-nocookie.com') {
      if (url.pathname.startsWith('/embed/')) {
        const id = url.pathname.split('/')[2];
        return id ? `https://www.youtube.com/embed/${encodeURIComponent(id)}` : null;
      }
      if (url.pathname.startsWith('/shorts/')) {
        const id = url.pathname.split('/')[2];
        return id ? `https://www.youtube.com/embed/${encodeURIComponent(id)}` : null;
      }
      const id = url.searchParams.get('v');
      return id ? `https://www.youtube.com/embed/${encodeURIComponent(id)}` : null;
    }
    if (host === 'vimeo.com') {
      const id = url.pathname.split('/').filter(Boolean)[0];
      return id && /^\d+$/.test(id) ? `https://player.vimeo.com/video/${id}` : null;
    }
    if (host === 'player.vimeo.com' && url.pathname.startsWith('/video/')) {
      const id = url.pathname.split('/')[2];
      return id && /^\d+$/.test(id) ? `https://player.vimeo.com/video/${id}` : null;
    }
    return null;
  } catch (error) {
    return null;
  }
}

export function safePercent(value) {
  const number = Number(value);
  if (!Number.isFinite(number)) return 0;
  return Math.max(0, Math.min(100, number));
}

export function safeCssColor(value, fallback = 'var(--text3)') {
  const text = String(value || '');
  if (/^#[0-9a-f]{3,8}$/i.test(text)) return text;
  if (/^var\(--[a-z0-9-]+\)$/i.test(text)) return text;
  return fallback;
}

export function safeGradient(value) {
  const text = String(value || '');
  return /^linear-gradient\([#,\-\w\s%.()]+\)$/i.test(text)
    ? text
    : 'linear-gradient(135deg,#1a2535,#0d1219)';
}
