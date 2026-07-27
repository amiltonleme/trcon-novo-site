# Status de implementação — Site TRCON

> Atualizado em **27/07/2026** — cruzado com `site/backend`, `site/frontend`, `site/infra`.  
> Gaps e segurança: [`15-GAPS-PRODUCAO-SEGURANCA.md`](15-GAPS-PRODUCAO-SEGURANCA.md).  
> Feito / fazendo / a fazer: [`16-PASSO-A-PASSO.md`](16-PASSO-A-PASSO.md).

## Visão geral

| Camada | Stack | Prod (jul/2026) |
|--------|-------|-----------------|
| Frontend | HTML/CSS/JS (ES modules), Vitest | Coolify em andamento; hoje Cloudflare Pages / legado |
| Backend | Spring Boot 3, Java 21, Flyway V1–**V7** | **OK** — `api-site.trcongroup.com.br` (Coolify + Neon `trcon_site`) |
| Pipeline conteúdo | Python + GitHub Actions 2×/dia | **OK** — `update-content.yml` |
| Integração marketing | API interna `X-API-Key` | **Código OK** — redeploy V7 + smoke S8 pendente |

---

## Backend — módulos

| Módulo | API pública | API interna | Migration | Testes |
|--------|-------------|-------------|-----------|--------|
| `lead` | `POST /api/v1/site/leads` | — | V1 | IT + unit |
| `highlights` | `GET /api/public/highlights` | `POST /api/internal/highlights` | V2, V5 | IT + unit |
| `news` | `GET /api/public/news`, **`GET /api/public/news/{slug}`** | `POST /api/internal/news` | V3, V4, **V7** | IT + unit |
| `economytips` | `GET /api/public/economy-tips` | `POST /api/internal/economy-tips` | V6 | IT |
| `feeds` | **`GET /sitemap.xml`**, **`GET /feed/news.xml`** | — | — | IT |
| `internal` (filtro) | — | `InternalApiKeyFilter` | — | IT |

### Flyway (Neon `trcon_site`)

| Versão | Conteúdo |
|--------|----------|
| V1 | `leads` |
| V2 | `daily_highlights` |
| V3 | `news_items` |
| V4 | `brand_slug`, `external_id` em news |
| V5 | `external_id` em highlights |
| V6 | `economy_tips` |
| **V7** | `slug`, `body`, `meta_title`, `meta_description` em `news_items` |

---

## Frontend — capacidades

| Item | Status | Arquivo / nota |
|------|--------|----------------|
| Config centralizada de APIs | ✅ | `config.js`, `env.js` (dev → `:8081`) |
| Formulário leads → backend | ✅ | `lead-form.js` |
| Radar: API + fallback pipeline JSON | ✅ | `fetchRadarHighlights` — exclui editorial legado |
| Novidades: API + fallback JSON | ✅ | feed separado de highlights |
| **Layout Radar + Novidades: cards-grid** | ✅ | `buildCardItemHtml` compartilhado |
| Educação Financeira merge API + RSS | ✅ | `loadEconomyTips` |
| Página artigo `/novidades/{slug}` | ✅ | `novidades.html`, `article.js` |
| Meta SEO + Open Graph na página | ✅ parcial | sem JSON-LD / `og:image` |
| Sitemap / RSS | ✅ | backend `sitemap.xml`, `feed/news.xml` |
| Dockerfile frontend + nginx rotas `/novidades/*` | ✅ | |

---

## Integração Sirius Marketing

| Fluxo | Endpoint site | Tipo marketing |
|-------|---------------|----------------|
| **Novidades** | `POST /api/internal/news` | `ARTICLE` |
| Educação Financeira | `POST /api/internal/economy-tips` | `LANDING_PAGE`, `NEWSLETTER` |
| ~~Radar via marketing~~ | — | **Removido 27/07** — Radar = pipeline |

**Highlights API** filtra itens editoriais (`/novidades/` ou `external_id` `-radar`) em `HighlightServiceImpl`.

Manual: [`18-MANUAL-MARKETING-EDITORIAL.md`](18-MANUAL-MARKETING-EDITORIAL.md).

---

## Pendências operacionais (alta)

| Item | Prioridade |
|------|------------|
| Redeploy backend prod Flyway **V7** | Alta |
| Redeploy frontend prod (S8 + cards + env.js) | Alta |
| DNS `@`/`www` → Hetzner | Alta |
| Smoke end-to-end: approve → Novidades + página slug; Radar sem duplicata | Alta |
| S8.7 SEO no form marketing | Baixa |

---

## Correções recentes (27/07/2026)

1. **Sprint 8** — artigo por slug, body, RSS, sitemap, integração marketing.
2. **Radar ≠ Novidades** — artigos marketing só em `news_items`; highlights filtrados.
3. **Layout unificado** — ambas seções em grid de cards na home.
4. **`fetchRadarHighlights`** — fallback JSON quando API só tem legado editorial.
5. **`env.js` dev** — APIs locais `:8081` (evita publicar local e ler prod na home).

---

## Correções anteriores (jul/2026)

1. Profiles Spring — `dev` default; prod via Coolify.
2. API interna — news, highlights, economy-tips com idempotência `external_id`.
3. Educação Financeira — V6 + merge frontend + pipeline RSS.
4. Neon idle — Hikari `min-idle=0`.
