// Configuração de ambiente do frontend (injeção em runtime, sem build step).
//
// Escolhe URLs pelo hostname — evita deploy com localhost ativo.
//   Radar TRCon      → GET /api/public/highlights
//   Novidades TRCon  → GET /api/public/news
//   Educação Financeira → GET /api/public/economy-tips

(function (scope) {
  var host = (scope.location && scope.location.hostname) || '';
  var isProd =
    host === 'trcongroup.com.br' ||
    host === 'www.trcongroup.com.br';

  if (isProd) {
    scope.TRCON_LEADS_API_URL = 'https://api-site.trcongroup.com.br/api/v1/site/leads';
    scope.TRCON_HIGHLIGHTS_API_URL = 'https://api-site.trcongroup.com.br/api/public/highlights';
    scope.TRCON_NEWS_API_URL = 'https://api-site.trcongroup.com.br/api/public/news';
    scope.TRCON_ECONOMY_TIPS_API_URL = 'https://api-site.trcongroup.com.br/api/public/economy-tips';
    scope.TRCON_SITE_BASE_URL = 'https://trcongroup.com.br';
    return;
  }

  // local (marketing + site backend em localhost)
  scope.TRCON_LEADS_API_URL = 'http://localhost:8081/api/v1/site/leads';
  scope.TRCON_HIGHLIGHTS_API_URL = 'http://localhost:8081/api/public/highlights';
  scope.TRCON_NEWS_API_URL = 'http://localhost:8081/api/public/news';
  scope.TRCON_ECONOMY_TIPS_API_URL = 'http://localhost:8081/api/public/economy-tips';
  scope.TRCON_SITE_BASE_URL = 'http://127.0.0.1:4173';
})(window);
