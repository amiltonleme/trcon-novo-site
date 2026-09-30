# Passo a passo — feito, em andamento e a fazer

> Atualizado em **30/09/2026** — Etapas 0 a 8 do plano de reposicionamento concluídas no código.
> Status detalhado: [`14-STATUS-IMPLEMENTACAO.md`](14-STATUS-IMPLEMENTACAO.md).  
> Gaps: [`15-GAPS-PRODUCAO-SEGURANCA.md`](15-GAPS-PRODUCAO-SEGURANCA.md).
> Mídia: [`19-DESENHO-MIDIA.md`](19-DESENHO-MIDIA.md).

---

## ✅ Feito

### Fases 0–7 (plano original — [`09-PLANO-EXECUCAO-IA.md`](canonical/09-PLANO-EXECUCAO-IA.md))

| Fase | Entrega |
|------|---------|
| 0 | Documentação canônica, skills, estrutura monorepo |
| 1 | Backend mínimo: lead, highlights, news, Flyway V1–V3, testes ≥ 80% |
| 2 | Docker Compose, Dockerfile backend, CI JaCoCo |
| 3 | Frontend migrado para `site/frontend`, módulos ES, Vitest |
| 4 | Página Serviços, CTAs `data-lead-type`, copy institucional |
| 5 | Leads → `POST /api/v1/site/leads`, fallback offline |
| 6 | Pipeline Python: radares de IA/tecnologia e `home-highlights.json`, via `update-content.yml` |
| 7 | Registro histórico: Home Radar + Novidades usavam API→JSON; após a Etapa 2 do plano 22, ambos estão em Conteúdo e Novidades usa somente a API institucional |

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
| S3.4 | Frontend: Radar ≠ Novidades; Radar com fallback e Novidades somente pela API institucional; **cards-grid** | ✅ atualizado na Etapa 2 |
| S3.5 | `application-dev.yml`, porta **8081** | ✅ |
| S3.6 | Profiles prod + CORS + Hikari idle | ✅ |

### Educação Financeira (histórico encerrado)

O fluxo entregue originalmente na S3.10 foi removido nas Etapas 6 e 7 do
[plano 22](22-PLANO-REPOSICIONAMENTO-LIMPEZA.md). A Home, o pipeline, o Sirius
Marketing e o backend não produzem nem consomem mais esse conteúdo. As migrations
V6 e V9 permanecem imutáveis; a V11 será executada somente após o backup previsto
na Etapa 9.

### Sprint 8 — Páginas de artigo e SEO

| # | Tarefa | Status |
|---|--------|--------|
| S8.2 | Migration V7: `slug`, `body`, meta | ✅ |
| S8.3 | `GET /api/public/news/{slug}` | ✅ |
| S8.1 | Página `/novidades/{slug}` | ✅ |
| S8.4 | Marketing envia slug + body | ✅ |
| S8.5 | Meta SEO + Open Graph | ✅ (`og:image` via capa A) |
| **S8.5b** | JSON-LD `NewsArticle` + HTML SSR no first paint | ✅ **16/08/2026** (`GET /novidades/{slug}`) |
| S8.6 | `sitemap.xml` + `feed/news.xml` | ✅ |
| S8.7 | SEO no form marketing | ✅ 17/08/2026 (marketing 0.24.0) |

### SEO higiene / hub editorial (16/08/2026)

| # | Tarefa | Status |
|---|--------|--------|
| H1 | Remover textos operacionais do HTML inicial da home | ✅ |
| H2 | Ocultar Radar e Novidades sem itens | ✅ |
| H3 | `robots.txt` + sitemap no site institucional | ✅ |
| H4 | Nginx/`dev_server` proxy `/novidades/` → backend (`SITE_API_UPSTREAM`) | ✅ |
| H5 | Marketing: `noindex` + `X-Robots-Tag` (app autenticado) | ✅ (repo marketing) |

### Desenho A — mídia URL/embed (30/07)

| # | Tarefa | Status |
|---|--------|--------|
| A1 | Flyway **V8** `news_items.cover_image_url` | ✅ |
| A2 | API interna/pública + hero + `og:image` | ✅ |
| A3 | YouTube/Vimeo URL → iframe seguro no body | ✅ |

### Correção Radar ≠ Novidades (27/07)

| # | Tarefa | Status |
|---|--------|--------|
| R1 | Marketing: artigos só em `POST /internal/news` | ✅ |
| R2 | Site: `HighlightServiceImpl` filtra editorial | ✅ |
| R3 | Frontend: `fetchRadarHighlights` + fallback pipeline | ✅ |

### UX produtos + contato (29/07)

| # | Tarefa | Status |
|---|--------|--------|
| P1 | Páginas `#page-hub`, `#page-agendamento`, `#page-marketing` (só conteúdo) | ✅ |
| P2 | Formulário único `#page-contato` + contexto `data-product` | ✅ |
| P3 | CTAs “especialista” / “Saiba mais” → contato ou detalhe | ✅ |
| P4 | Identidade no lead via `origem` + prefixo na `mensagem` | ✅ |

### Notificação lead + qualidade (29/07)

| # | Tarefa | Status |
|---|--------|--------|
| SEC4 | E-mail ao receber lead (Resend) | ✅ código; Coolify pendente |
| Q1 | JaCoCo ≥ 80% line + branch | ✅ |
| Q2 | `mvnw` +x no CI | ✅ |

---

## 🔄 Em andamento / parcial

| Item | Hoje | Próximo passo |
|------|------|---------------|
| Backend prod com revisão atual e Flyway até V11 | Código no repo | Backup de `economy_tips`, redeploy Coolify e smoke conforme Etapa 9 |
| Frontend prod reposicionado | Código OK | Redeploy + `SITE_API_UPSTREAM` + smoke funcional conforme Etapa 9 |
| Mail lead prod | Código OK | `TRCON_SITE_MAIL_*` + smoke submit |
| DNS `@`/`www` | Pages ou legado | **A** → Hetzner (Coolify `site-frontend`) |
| Smoke marketing ↔ site | Código OK nos dois repos | Aprovar artigo → View Source `/novidades/{slug}` (meta + JSON-LD) |
| Descontinuar `fluxo-caixa-app/site-trcon` | Novo repo estável | Decisão explícita pós DNS Hetzner |

---

## 📋 A fazer

### Operacional (prioridade alta)

| # | Tarefa | Onde |
|---|--------|------|
| O1 | Fazer backup validado da tabela histórica e redeploy do `site-trcon-backend` com a V11 | Coolify/PostgreSQL |
| O2 | Smoke `GET .../news/{slug}`, **`GET /novidades/{slug}`** (HTML), sitemap/feed; View Source | Browser/curl |
| O3 | Redeploy da revisão atual de `site-frontend` + env **`SITE_API_UPSTREAM`** | Coolify |
| O4 | Cloudflare: `@` e `www` → IP Hetzner | Cloudflare DNS |
| O5 | Smoke leads + e-mail Resend + home | Manual |
| O6 | Configurar `TRCON_SITE_MAIL_*` / `TRCON_SITE_LEAD_NOTIFY_TO` | Coolify |

### Segurança / compliance

| # | Tarefa |
|---|--------|
| SEC1 | Rate limit Cloudflare em `POST /api/v1/site/leads` |
| SEC2 | Rate limit CF em `/api/internal/*` |
| SEC3 | Processo LGPD: export/exclusão leads |
| SEC5 | Ambiente staging (Neon branch + subdomínio) |

### Melhorias (baixa / média)

| # | Tarefa | Status |
|---|--------|--------|
| M1 | JSON-LD `NewsArticle` na página de artigo | ✅ 16/08/2026 |
| M2 | S8.7 — campos SEO no form marketing | ✅ 17/08/2026 |
| M3 | Ampliar feeds RSS / curadoria dos radares | ❌ |

### Fase 9 — Consolidação ([`09-PLANO-EXECUCAO-IA.md`](canonical/09-PLANO-EXECUCAO-IA.md))

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

Mail local (opcional): `TRCON_SITE_MAIL_ENABLED=true` + key Resend; default local `enabled=false`.

**Smoke local:**

1. `http://localhost:8081/api/public/news`
2. `http://localhost:8081/api/public/highlights`
3. `http://localhost:8081/novidades/{slug}` — HTML com meta/JSON-LD (View Source)
4. `http://127.0.0.1:4173` — Radar/Novidades só se houver itens; proxy `/novidades/` → `:8081`
5. Enviar pergunta ao assistente e conferir a resposta e os `sourceIds`
6. Aprovar artigo no marketing → View Source em `/novidades/{slug}`
7. Enviar lead em Contato → 201 + (se mail ON) e-mail ao destinatário

---

## Histórico

| Data | Evento |
|------|--------|
| **16/08/2026** | **0.8.0** — SEO higiene (home/`robots.txt`) + SSR `/novidades/{slug}` + JSON-LD; proxy `SITE_API_UPSTREAM` |
| **29/07/2026** | Docs sync 0.4.0; produtos + contato; mail lead; cobertura; CI mvnw |
| **27/07/2026** | S8 + Radar≠Novidades + layout cards; docs 14–18 |
| **26/07/2026** | Histórico: fluxo de Educação Financeira criado na V6; retirado depois pelas Etapas 6 e 7 do plano 22 |
| **23/07/2026** | API interna highlights/news; backend prod Coolify |
| **jul/2026** | Fases 0–7 concluídas; migração de `fluxo-caixa-app/site-trcon` |
