# TRCon Site — Frontend

Frontend público do site TRCon (HTML/CSS/JS vanilla, sem framework de SPA).
Stack e regras completas em `../doc/03-FRONTEND-STACK-CANONICA.md`.

## Status

- **Fase 3 (migração do frontend):** concluída. Conteúdo migrado de
  `C:\projetos-al\fluxo-caixa-app\site-trcon` e reorganizado em `assets/modules/`.
  A pasta antiga permanece intacta e publicada até o corte oficial
  (ver `../doc/07-MIGRACAO-PARALELA.md`, Fase 5).
- **Fase 4 (conteúdo institucional):** concluída (copy aguardando revisão) —
  página Serviços (`page-servicos`) com as 4 linhas de negócio + modelos de
  engajamento de staffing, e blocos das 4 linhas na home. CTAs já carregam
  `data-lead-type` (PRODUTO / DESENVOLVIMENTO_SOB_DEMANDA / CUSTOMIZACAO /
  ALOCACAO_MAO_DE_OBRA) para a integração da Fase 5.
- **Fase 5 (integração com backend):** concluída. O formulário envia para
  `POST /api/v1/site/leads` via `assets/modules/lead-form.js`, com o campo
  `tipoInteresse`, pré-seleção pelos CTAs (`data-lead-type`) e degradação
  previsível se o backend estiver fora do ar. Validado no navegador (201, 409
  duplicado, fallback offline).

## Estrutura

```text
frontend/
  index.html            # página única (SPA por show/hide de seções)
  style.css
  assets/
    app.js              # orquestrador (ES module)
    trcon-logo.png
    modules/
      config.js         # URLs de API por ambiente (window.TRCON_*_API_URL) + fallback
      sanitize.js       # helpers puros de sanitização/formatação (testados)
      lead-form.js      # montagem/envio de lead (POST /api/v1/site/leads) + fallback
      content.js        # radar/novidades: fetchWithFallback (API→JSON) + render puro
  data/                 # JSON público consumido pela home (Camada 3)
  scripts/              # geração offline de conteúdo (pipeline SOLID — ver scripts/README.md)
  tests/modules/        # testes Vitest dos módulos com lógica pura
```

## Consumo de conteúdo (Radar / Novidades)

As seções **Radar TRCon** (highlights) e **Novidades** (news) usam
`assets/modules/content.js`: tentam a API pública do backend quando
`window.TRCON_HIGHLIGHTS_API_URL` / `TRCON_NEWS_API_URL` estão configuradas e,
em qualquer falha, caem para o JSON estático (`data/home-highlights.json`,
`data/news-log.json`). O mesmo shape (camelCase) serve os dois casos, então a
troca é transparente. Sem as variáveis, o site funciona 100% com o JSON
publicado pelo pipeline.

## Desenvolvimento

```bash
npm install       # instala vitest, eslint, prettier
npm run dev       # serve o site em http://127.0.0.1:4173
npm test          # roda os testes Vitest
npm run lint      # ESLint
npm run format    # Prettier
```

## Notas de migração

- O `app.js` é carregado como `<script type="module">`. As funções puras de
  sanitização/formatação foram extraídas para `assets/modules/sanitize.js`
  (testáveis sem navegador); a configuração de endpoints foi extraída para
  `assets/modules/config.js`. O comportamento visual/funcional é idêntico ao do
  site atual (paridade preservada).
- `config.js` já expõe `leadsApiUrl` lendo `window.TRCON_LEADS_API_URL` com
  fallback para o endpoint legado de waitlist — a troca efetiva do formulário
  para o backend novo acontece na Fase 5.
- `index_old.html` e `trcon-hero.html` (resíduos de desenvolvimento) **não**
  foram migrados — não faziam parte do site publicado.

Diretrizes de conteúdo e identidade visual: `../doc/01-POSICIONAMENTO-INSTITUCIONAL.md`
e `../doc/08-REDESIGN-DIRETRIZES.md`.
