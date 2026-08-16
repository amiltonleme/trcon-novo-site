# Status de implementação — Site TRCON

> Atualizado em **16/08/2026** — `site/backend` **0.8.0** + `site/frontend` **0.8.0** (SEO hub editorial: higiene de indexação + HTML SSR `/novidades/{slug}`).  
> Gaps e segurança: [`15-GAPS-PRODUCAO-SEGURANCA.md`](15-GAPS-PRODUCAO-SEGURANCA.md).  
> Feito / fazendo / a fazer: [`16-PASSO-A-PASSO.md`](16-PASSO-A-PASSO.md).

## Visão geral

| Camada | Stack | Prod (ago/2026) |
|--------|-------|-----------------|
| Frontend | HTML/CSS/JS (ES modules), Vitest — **0.8.0** | Coolify; nginx proxy `/novidades/` → API |
| Backend | Spring Boot 3, Java 21, Flyway V1–**V8**, versão **0.8.0** | **OK** — `api-site.trcongroup.com.br` (Coolify + Neon `trcon_site`) |
| Pipeline conteúdo | Python + GitHub Actions 2×/dia | **OK** — `update-content.yml` |
| Integração marketing | API interna `X-API-Key` | **Código OK** — smoke/redeploy conforme ambiente |
| Notificação lead | Resend (`LeadEmailNotifier`) | **Código OK** — configurar `TRCON_SITE_MAIL_*` no Coolify |

---

## Backend — módulos

| Módulo | API pública | API interna | Migration | Testes |
|--------|-------------|-------------|-----------|--------|
| `lead` | `POST /api/v1/site/leads` | — | V1 | IT + unit; e-mail Resend (falha não quebra 201) |
| `highlights` | `GET /api/public/highlights` | `POST /api/internal/highlights` | V2, V5 | IT + unit (filtra editorial) |
| `news` | `GET /api/public/news`, **`GET /api/public/news/{slug}`**, **`GET /novidades/{slug}` (HTML SSR)** | `POST /api/internal/news` (+ **`coverImageUrl`**) | V3, V4, V7, **V8** | IT + unit |
| `economytips` | `GET /api/public/economy-tips` | `POST /api/internal/economy-tips` | V6 | IT |
| `feeds` | **`GET /sitemap.xml`**, **`GET /feed/news.xml`** | — | — | IT |
| `internal` (filtro) | — | `InternalApiKeyFilter` | — | IT |
| mail | — | Resend via `trcon.site.mail.*` | — | unit + mock HTTP |

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
| **V8** | `cover_image_url` em `news_items` |

---

## Frontend — capacidades

| Item | Status | Arquivo / nota |
|------|--------|----------------|
| Config centralizada de APIs | ✅ | `config.js`, `env.js` (dev → `:8081`) |
| Formulário leads → backend | ✅ | `lead-form.js` — página **`#page-contato`** |
| Páginas de produto (Hub / Agendamento / Marketing) | ✅ | `#page-hub`, `#page-agendamento`, `#page-marketing` — só conteúdo |
| Contato contextual (`data-product`) | ✅ | hub / agendamento / marketing / servicos / default |
| Radar: API + fallback pipeline JSON | ✅ | `fetchRadarHighlights` — exclui editorial legado |
| Novidades: API + fallback JSON | ✅ | feed separado de highlights |
| **Layout Radar + Novidades: cards-grid** | ✅ | `buildCardItemHtml` compartilhado |
| Educação Financeira merge API + RSS | ✅ | `loadEconomyTips` |
| **Seções editoriais só com conteúdo** | ✅ | `#block-news` / `#block-radar` / `#block-economy-tips` ocultos se vazios; sem texto operacional no HTML inicial |
| Página artigo `/novidades/{slug}` | ✅ | **SSR backend** (meta/OG/JSON-LD/corpo) + fallback CSR `novidades.html` |
| Meta SEO + Open Graph + JSON-LD | ✅ | HTML inicial via `ArticlePageController`; CSR também injeta JSON-LD |
| `robots.txt` | ✅ | `frontend/robots.txt` + sitemap institucional |
| Sitemap / RSS | ✅ | backend `sitemap.xml`, `feed/news.xml` |
| Dockerfile frontend + nginx | ✅ | proxy `/novidades/` → `SITE_API_UPSTREAM` (fallback CSR) |

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

## Feito / fazendo / a fazer (resumo)

| Estado | Itens |
|--------|-------|
| **Feito** | Fases 0–7; S8.1–S8.6; **S8.5b** JSON-LD + HTML SSR; higiene SEO home (`robots.txt`, seções vazias); Desenho A; Radar ≠ Novidades; economy tips V6; produtos + contato; Resend; JaCoCo ≥ 80% |
| **Fazendo** | Redeploy prod frontend/backend **0.8.0** + `SITE_API_UPSTREAM`; DNS `@`/`www` → Hetzner; smoke end-to-end; Coolify `TRCON_SITE_MAIL_*` |
| **A fazer** | Rate limit CF leads/interno; LGPD export/exclusão; staging; S8.7 SEO no form marketing; F9 consolidação legado |
| **Melhorias** | Painel desativar dica economy; CRM; **Desenho B (R2)**; E2E cross-stack |

---

## Pendências operacionais (alta)

| Item | Prioridade |
|------|------------|
| Redeploy backend + frontend **0.8.0** (SSR `/novidades/` + proxy) | Alta |
| Coolify frontend: env **`SITE_API_UPSTREAM`** (ex. `http://trcon-site-backend:8080` ou URL interna da API) | Alta |
| `TRCON_SITE_MAIL_*` + `TRCON_SITE_LEAD_NOTIFY_TO` no Coolify | Alta |
| DNS `@`/`www` → Hetzner | Alta |
| Smoke: View Source em `/novidades/{slug}` com meta/JSON-LD; home sem “Carregando…” | Alta |
| S8.7 SEO no form marketing | Baixa |

---

## Correções recentes (16/08/2026)

1. **Versão 0.8.0** — backend + frontend alinhados.
2. **SEO higiene (Fase A)** — remoção de mensagens operacionais do HTML inicial; seções editoriais ocultas sem itens; `robots.txt`.
3. **SEO SSR (Fase B / S8.5b)** — `GET /novidades/{slug}` HTML com meta, OG, JSON-LD `NewsArticle` e corpo; nginx/`dev_server` proxy para a API.

### 30/07/2026

1. Desenho A — capa URL + `og:image` + vídeo embed.
2. Docs sync com Flyway V8.

### 29/07/2026

1. UX produtos + e-mail lead Resend; JaCoCo ≥ 80%; CI `mvnw`.

### 27/07/2026

1. Sprint 8 — artigo por slug, body, RSS, sitemap.
2. Radar ≠ Novidades; layout cards; `env.js` dev `:8081`.
