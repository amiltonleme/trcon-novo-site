// Configuração de endpoints da API por ambiente.
// Regra canônica (doc/03-FRONTEND-STACK-CANONICA.md): nenhuma URL de API fica
// hardcoded espalhada pelo código — tudo passa por aqui, permitindo o rollout
// por configuração descrito em doc/07-MIGRACAO-PARALELA.md.
//
// Cada URL é lida de uma variável global (window.TRCON_*_API_URL) que pode ser
// injetada por ambiente antes do carregamento do app; se ausente, cai no
// default local de desenvolvimento.

const globalScope = typeof window !== 'undefined' ? window : {};

export function resolveApiConfig(overrides = {}) {
  const scope = { ...globalScope, ...overrides };
  return {
    // Endpoint de leads do backend (POST /api/v1/site/leads).
    // TRCON_WAITLIST_API_URL é aceito como alias legado para compatibilidade.
    leadsApiUrl:
      scope.TRCON_LEADS_API_URL ||
      scope.TRCON_WAITLIST_API_URL ||
      'http://localhost:8081/api/v1/site/leads',
    highlightsApiUrl: scope.TRCON_HIGHLIGHTS_API_URL || 'http://localhost:8081/api/public/highlights',
    newsApiUrl: scope.TRCON_NEWS_API_URL || 'http://localhost:8081/api/public/news',
  };
}

export const apiConfig = resolveApiConfig();
