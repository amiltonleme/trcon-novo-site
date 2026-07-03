// Configuração de ambiente do frontend (injeção em runtime, sem build step).
//
// Este arquivo é carregado ANTES do app.js (script clássico, não-módulo) e define
// as URLs da API por ambiente. Em desenvolvimento local, deixe tudo comentado:
// o site funciona 100% com o JSON estático publicado pelo pipeline.
//
// Em PRODUÇÃO, edite este arquivo (ou substitua-o no deploy) apontando para o
// backend real. O config.js lê estas variáveis com fallback seguro.
//
// window.TRCON_LEADS_API_URL      = 'https://api.trcongroup.com.br/api/v1/site/leads';
// window.TRCON_HIGHLIGHTS_API_URL = 'https://api.trcongroup.com.br/api/public/highlights';
// window.TRCON_NEWS_API_URL       = 'https://api.trcongroup.com.br/api/public/news';
