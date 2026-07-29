# Passo a passo — feito, em andamento e a fazer

> Atualizado em **27/07/2026**.  
> Status detalhado: [`14-STATUS-IMPLEMENTACAO.md`](14-STATUS-IMPLEMENTACAO.md).  
> Gaps: [`15-GAPS-PRODUCAO-SEGURANCA.md`](15-GAPS-PRODUCAO-SEGURANCA.md).

---

## ✅ Feito

### Fases 0–7 (plano original — [`09-PLANO-EXECUCAO-IA.md`](09-PLANO-EXECUCAO-IA.md))

| Fase | Entrega |
|------|---------|
| 0 | Documentação canônica, skills, estrutura monorepo |
| 1 | Backend mínimo: lead, highlights, news, Flyway V1–V3, testes ≥ 80% |
| 2 | Docker Compose, Dockerfile backend, CI JaCoCo |
| 3 | Frontend migrado para `site/frontend`, módulos ES, Vitest |
| 4 | Página Serviços, CTAs `data-lead-type`, copy institucional |
| 5 | Leads → `POST /api/v1/site/leads`, fallback offline |
| 6 | Pipeline Python: radar IA/tecnologia, mercado, economy RSS, `update-content.yml` |
| 7 | Home Radar + Novidades com `fetchWithFallback` API→JSON; layout **cards-grid** |

### Fase 8 — Infra produção (parcial)

| Item | Status |
|------|--------|
| Hetzner CX23 + Coolify | ✅ |
| Backend prod `api-site.trcongroup.com.br` | ✅ |
| Neon `trcon_site` | ✅ |
| Frontend prod no Coolify | 🟡 artefato OK; DNS pendente |

### Integração Sirius Marketing (S3 site)

| # | Tarefa | Status |
|---|--------|--------|
| S3.1 | `POST /api/internal/news` + API key | ✅ |
| S3.2 | `POST /api/internal/highlights` + V5 | ✅ |
| S3.3 | Idempotência `external_id` news/highlights | ✅ V4/V5 |
| S3.4 | Frontend: Radar ≠ Novidades; fallback; **cards-grid** | ✅ |
| S3.5 | `application-dev.yml`, porta **8081** | ✅ |
| S3.6 | Profiles prod + CORS + Hikari idle | ✅ |

### Educação Financeira (S3.10)

| # | Tarefa | Status |
|---|--------|--------|
| S3.10.1 | Pipeline `update_economy_tips.py` + catálogo 18 dicas | ✅ |
| S3.10.2 | Flyway **V6** + APIs interna/pública economy tips | ✅ |
| S3.10.3 | Home: `loadEconomyTips` merge API + JSON + disclaimer | ✅ |
| S3.10.4 | Marketing publica `LANDING_PAGE`/`NEWSLETTER` | ✅ (repo marketing) |

**Fluxo:**

```text
RSS 2×/dia → economy-tips.json ──┐
                                  ├── Home (até 4 cards; marketing tem prioridade)
Marketing → POST /api/internal/economy-tips ──┘
```

---

## 🔄 Em andamento / parcial

| Item | Hoje | Próximo passo |
|------|------|---------------|
| Backend prod Flyway V6 | Código no repo | **Redeploy** `site-trcon-backend` no Coolify |
| Frontend prod economy tips | `env.js` com URL prod | Redeploy + smoke home |
| DNS `@`/`www` | Pages ou legado | **A** → Hetzner (Coolify `site-frontend`) |
| Smoke marketing ↔ site | Código OK nos dois repos | Aprovar landing → `GET /api/public/economy-tips` |
| Descontinuar `fluxo-caixa-app/site-trcon` | Novo repo estável | Decisão explícita pós DNS Hetzner |

---

## 📋 A fazer

### Operacional (prioridade alta)

| # | Tarefa | Onde |
|---|--------|------|
| O1 | Coolify → redeploy `site-trcon-backend` (**V6 + V7**) | Coolify |
| O2 | Smoke `GET https://api-site.trcongroup.com.br/api/public/economy-tips` | Browser/curl |
| O3 | Redeploy `site-frontend` com `TRCON_ECONOMY_TIPS_API_URL` | Coolify |
| O4 | Cloudflare: `@` e `www` → IP Hetzner | Cloudflare DNS |
| O5 | Smoke leads + news + highlights + economy na home | Manual |

### Sprint 8 — Páginas de artigo e SEO

> Escopo compartilhado com Sirius Marketing — [`08_passo_a_passo.md`](../../sirius-marketing/projeto/docs/cursor/08_passo_a_passo.md) S8.

| # | Tarefa | Status |
|---|--------|--------|
| S8.2 | Migration V7: `slug`, `body`, meta | ✅ |
| S8.3 | `GET /api/public/news/{slug}` | ✅ |
| S8.1 | Página `/novidades/{slug}` | ✅ |
| S8.4 | Marketing envia slug + body | ✅ |
| S8.5 | Meta SEO + Open Graph | ✅ parcial |
| S8.6 | `sitemap.xml` + `feed/news.xml` | ✅ |
| S8.7 | SEO no form marketing | ❌ pendente |

### Correção Radar ≠ Novidades (27/07)

| # | Tarefa | Status |
|---|--------|--------|
| R1 | Marketing: artigos só em `POST /internal/news` | ✅ |
| R2 | Site: `HighlightServiceImpl` filtra editorial | ✅ |
| R3 | Frontend: `fetchRadarHighlights` + fallback pipeline | ✅ |

**Redeploy prod** necessário para V7 + frontend S8.

### Operacional (prioridade alta)

| # | Tarefa |
|---|--------|
| SEC1 | Rate limit Cloudflare em `POST /api/v1/site/leads` |
| SEC2 | Rate limit CF em `/api/internal/*` |
| SEC3 | Processo LGPD: export/exclusão leads |
| SEC4 | E-mail ao receber lead (Resend) | ✅ código; configurar `TRCON_SITE_MAIL_*` no Coolify |
| SEC5 | Ambiente staging (Neon branch + subdomínio) |

### Fase 9 — Consolidação ([`09-PLANO-EXECUCAO-IA.md`](09-PLANO-EXECUCAO-IA.md))

| # | Tarefa |
|---|--------|
| F9.1 | Observação 2–4 semanas pós-DNS Hetzner |
| F9.2 | Congelar `fluxo-caixa-app/site-trcon` como histórico |
| F9.3 | Remover `infra/render.yaml` / `fly.toml` quando Render off |

---

## Ambiente local (stack com marketing)

```powershell
# Postgres site
cd C:\projetos-al\trcongroup\site\infra
docker compose up -d postgres

# Backend site (:8081)
cd C:\projetos-al\trcongroup\site\backend
mvn spring-boot:run

# Frontend site (:4173)
cd C:\projetos-al\trcongroup\site\frontend
npm run dev

# (Opcional) Marketing :8080 — aprovar artigo e ver Novidades
```

Chave dev compartilhada: `TRCON_SITE_INTERNAL_API_KEY=dev-internal-key-change-me`.

**Smoke local:**

1. `http://localhost:8081/api/public/news`
2. `http://localhost:8081/api/public/highlights`
3. `http://localhost:8081/api/public/economy-tips`
4. `http://127.0.0.1:4173` — Radar (pipeline), Novidades (editorial), cards-grid
5. Aprovar artigo no marketing → `/novidades/{slug}` abre texto completo

---

## Histórico

| Data | Evento |
|------|--------|
| **27/07/2026** | S8 + Radar≠Novidades + layout cards; docs 14–18 |
| **26/07/2026** | Economy tips V6 + merge frontend + pipeline RSS |
| **23/07/2026** | API interna highlights/news; backend prod Coolify |
| **jul/2026** | Fases 0–7 concluídas; migração de `fluxo-caixa-app/site-trcon` |
