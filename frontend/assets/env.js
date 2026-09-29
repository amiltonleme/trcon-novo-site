// Configuração de ambiente do frontend (injeção em runtime, sem build step).
//
// Escolhe URLs pelo hostname — evita deploy com localhost ativo.
//   Radar TRCONGROUP      → GET /api/public/highlights
//   Novidades TRCONGROUP  → GET /api/public/news
//   Assistente IA         → POST /api/v1/site/chat

(function (scope) {
  var host = (scope.location && scope.location.hostname) || '';
  var origin = (scope.location && scope.location.origin) || '';
  var isProd = host === 'trcongroup.com.br' || host === 'www.trcongroup.com.br';
  var isDev = host === 'site-dev.trcongroup.com.br';

  if (isProd) {
    scope.TRCON_LEADS_API_URL = 'https://api-site.trcongroup.com.br/api/v1/site/leads';
    scope.TRCON_HIGHLIGHTS_API_URL = 'https://api-site.trcongroup.com.br/api/public/highlights';
    scope.TRCON_NEWS_API_URL = 'https://api-site.trcongroup.com.br/api/public/news';
    scope.TRCON_CHAT_API_URL = 'https://api-site.trcongroup.com.br/api/v1/site/chat';
    scope.TRCON_SITE_BASE_URL = 'https://trcongroup.com.br';
    return;
  }

  if (isDev) {
    scope.TRCON_LEADS_API_URL = 'https://api-site-dev.trcongroup.com.br/api/v1/site/leads';
    scope.TRCON_HIGHLIGHTS_API_URL = 'https://api-site-dev.trcongroup.com.br/api/public/highlights';
    scope.TRCON_NEWS_API_URL = 'https://api-site-dev.trcongroup.com.br/api/public/news';
    scope.TRCON_CHAT_API_URL = 'https://api-site-dev.trcongroup.com.br/api/v1/site/chat';
    scope.TRCON_SITE_BASE_URL = 'https://site-dev.trcongroup.com.br';
    return;
  }

  // Preview do Coolify e desenvolvimento local usam sempre o proxy /api da
  // própria origem. O frontend não contém fallback direto para porta de backend.
  scope.TRCON_LEADS_API_URL = origin + '/api/v1/site/leads';
  scope.TRCON_HIGHLIGHTS_API_URL = origin + '/api/public/highlights';
  scope.TRCON_NEWS_API_URL = origin + '/api/public/news';
  scope.TRCON_CHAT_API_URL = origin + '/api/v1/site/chat';
  scope.TRCON_SITE_BASE_URL = origin;
})(window);
