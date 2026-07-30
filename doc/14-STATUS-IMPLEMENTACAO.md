# Status de implementação — Site TRCON

> Atualizado em **30/07/2026** — cruzado com `site/backend` **0.4.0** (+ Flyway **V8** capa), `site/frontend`, `site/infra` (branch `main`).  
> Gaps e segurança: [`15-GAPS-PRODUCAO-SEGURANCA.md`](15-GAPS-PRODUCAO-SEGURANCA.md).  
> Feito / fazendo / a fazer: [`16-PASSO-A-PASSO.md`](16-PASSO-A-PASSO.md).

## Visão geral

| Camada | Stack | Prod (jul/2026) |
|--------|-------|-----------------|
| Frontend | HTML/CSS/JS (ES modules), Vitest | Coolify em andamento; hoje Cloudflare Pages / legado |
| Backend | Spring Boot 3, Java 21, Flyway V1–**V8**, versão **0.4.0** | **OK** — `api-site.trcongroup.com.br` (Coolify + Neon `trcon_site`); **redeploy V8** pendente |
| Pipeline conteúdo | Python + GitHub Actions 2×/dia | **OK** — `update-content.yml` |
| Integração marketing | API interna `X-API-Key` | **Código OK** — redeploy V7/V8 + smoke S8 se ainda pendente em prod |
| Notificação lead | Resend (`LeadEmailNotifier`) | **Código OK** — configurar `TRCON_SITE_MAIL_*` no Coolify |

---

## Backend — módulos

| Módulo | API pública | API interna | Migration | Testes |
|--------|-------------|-------------|-----------|--------|
| `lead` | `POST /api/v1/site/leads` | — | V1 | IT + unit; e-mail Resend (falha não quebra 201) |
| `highlights` | `GET /api/public/highlights` | `POST /api/internal/highlights` | V2, V5 | IT + unit (filtra editorial) |
| `news` | `GET /api/public/news`, **`GET /api/public/news/{slug}`** | `POST /api/internal/news` (+ **`coverImageUrl`**) | V3, V4, V7, **V8** | IT + unit |
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
| Página artigo `/novidades/{slug}` | ✅ | `novidades.html`, `article.js` — **hero capa** + embed vídeo |
| Meta SEO + Open Graph na página | ✅ parcial | **`og:image` via capa** ✅; JSON-LD pendente |
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

## Feito / fazendo / a fazer (resumo)

| Estado | Itens |
|--------|-------|
| **Feito** | Fases 0–7; S8.1–S8.6 (artigo, slug/body, RSS/sitemap); **Desenho A** (capa URL + `og:image` + vídeo embed); Radar ≠ Novidades; economy tips V6; páginas produto + `#page-contato`; e-mail lead Resend; JaCoCo ≥ 80% branch/line |
| **Fazendo** | Redeploy prod (V6–**V8** + frontend); DNS `@`/`www` → Hetzner; smoke end-to-end; Coolify `TRCON_SITE_MAIL_*` |
| **A fazer** | Rate limit CF leads/interno; LGPD export/exclusão; staging; S8.7 SEO no form marketing; F9 consolidação legado |
| **Melhorias** | JSON-LD `NewsArticle`; painel desativar dica economy; CRM; **Desenho B (R2)**; E2E cross-stack |

---

## Pendências operacionais (alta)

| Item | Prioridade |
|------|------------|
| Redeploy backend prod Flyway **V6 + V7 + V8** (capa) | Alta |
| Redeploy frontend prod (S8 + cards + páginas produto + env.js) | Alta |
| `TRCON_SITE_MAIL_*` + `TRCON_SITE_LEAD_NOTIFY_TO` no Coolify | Alta |
| DNS `@`/`www` → Hetzner | Alta |
| Smoke: lead → e-mail; approve → Novidades + `/novidades/{slug}`; Radar sem duplicata | Alta |
| S8.7 SEO no form marketing | Baixa |

---

## Correções recentes (29/07/2026)

1. **Versão 0.4.0** — backend site alinhado ao marketing.
2. **UX produtos** — detalhe Hub / Agendamento / Marketing sem formulário embutido; formulário único em Contato com contexto.
3. **E-mail de lead** — `LeadEmailNotifier` + Resend; falha de e-mail não impede `201`.
4. **Cobertura** — JaCoCo gate ≥ 80% (line + branch); extratos `LeadNotificationMessageBuilder`, `NewsFeedXmlSupport`.
5. **CI** — `mvnw` executável no workflow backend.

### 27/07/2026

1. **Sprint 8** — artigo por slug, body, RSS, sitemap, integração marketing.
2. **Radar ≠ Novidades** — artigos marketing só em `news_items`; highlights filtrados.
3. **Layout unificado** — ambas seções em grid de cards na home.
4. **`env.js` dev** — APIs locais `:8081`.

---

## Correções anteriores (jul/2026)

1. Profiles Spring — `dev` default; prod via Coolify.
2. API interna — news, highlights, economy-tips com idempotência `external_id`.
3. Educação Financeira — V6 + merge frontend + pipeline RSS.
4. Neon idle — Hikari `min-idle=0`.
