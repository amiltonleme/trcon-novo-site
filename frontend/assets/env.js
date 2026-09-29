// Configuração de ambiente do frontend (injeção em runtime, sem build step).
//
// Escolhe URLs pelo hostname — evita deploy com localhost ativo.
//   Radar TRCONGROUP      → GET /api/public/highlights
//   Novidades TRCONGROUP  → GET /api/public/news
//   Assistente IA         → POST /api/v1/site/chat

(function (scope) {
  var host = (scope.location && scope.location.hostname) || '';
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
    scope.TRCON_SITE_BASE_URL = 'site-dev.trcongroup.com.br';
    return;
  }

  // local (marketing + site backend em localhost)
  scope.TRCON_LEADS_API_URL = 'http://localhost:8081/api/v1/site/leads';
  scope.TRCON_HIGHLIGHTS_API_URL = 'http://localhost:8081/api/public/highlights';
  scope.TRCON_NEWS_API_URL = 'http://localhost:8081/api/public/news';
  scope.TRCON_CHAT_API_URL = 'http://localhost:8081/api/v1/site/chat';
  scope.TRCON_SITE_BASE_URL = 'http://127.0.0.1:4173';
})(window);
