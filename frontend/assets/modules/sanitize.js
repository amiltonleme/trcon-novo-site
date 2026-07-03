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
