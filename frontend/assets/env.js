// Configuração de ambiente do frontend (injeção em runtime, sem build step).
//
// Desenvolvimento local — dois feeds distintos na home:
//   Radar TRCon      → GET /api/public/highlights  (curadoria IA/tecnologia; se vazio, cai no JSON estático)
//   Novidades TRCon  → GET /api/public/news        (artigos publicados pelo Sirius Marketing)
//
// Produção: substitua pelos domínios reais no deploy (Coolify/Pages).

window.TRCON_LEADS_API_URL = 'http://localhost:8081/api/v1/site/leads';
window.TRCON_HIGHLIGHTS_API_URL = 'http://localhost:8081/api/public/highlights';
window.TRCON_NEWS_API_URL = 'http://localhost:8081/api/public/news';
