// Configuração de ambiente do frontend (injeção em runtime, sem build step).
//
// Desenvolvimento local — dois feeds distintos na home:
//   Radar TRCon      → GET /api/public/highlights  (curadoria IA/tecnologia; se vazio, cai no JSON estático)
//   Novidades TRCon  → GET /api/public/news        (artigos publicados pelo Sirius Marketing)
//
// Produção: comente o bloco "local" e descomente o bloco "produção" antes do deploy.

// --- local (marketing + site backend em localhost) ---
window.TRCON_LEADS_API_URL = 'http://localhost:8081/api/v1/site/leads';
window.TRCON_HIGHLIGHTS_API_URL = 'http://localhost:8081/api/public/highlights';
window.TRCON_NEWS_API_URL = 'http://localhost:8081/api/public/news';
window.TRCON_ECONOMY_TIPS_API_URL = 'http://localhost:8081/api/public/economy-tips';
window.TRCON_SITE_BASE_URL = 'http://127.0.0.1:4173';

/*
// --- produção (Coolify) ---
window.TRCON_LEADS_API_URL = 'https://api-site.trcongroup.com.br/api/v1/site/leads';
window.TRCON_HIGHLIGHTS_API_URL = 'https://api-site.trcongroup.com.br/api/public/highlights';
window.TRCON_NEWS_API_URL = 'https://api-site.trcongroup.com.br/api/public/news';
window.TRCON_ECONOMY_TIPS_API_URL = 'https://api-site.trcongroup.com.br/api/public/economy-tips';
window.TRCON_SITE_BASE_URL = 'https://trcongroup.com.br';
*/
