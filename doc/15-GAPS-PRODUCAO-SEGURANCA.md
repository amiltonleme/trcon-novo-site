# Gaps produção e segurança — Site TRCON

> Atualizado em **16/08/2026** — cruzado com `site/backend` **0.8.0** e `site/frontend` **0.8.0**.

Legenda: ✅ implementado · 🟡 parcial · ❌ pendente

---

## Produto / UX

| Gap | Status | O que existe | O que falta |
|-----|--------|--------------|-------------|
| Página de artigo legível | ✅ | `slug`, `body`, `/novidades/{slug}` (SSR + CSR fallback), capa, meta/OG/JSON-LD | — |
| SEO (meta, OG, sitemap, RSS, JSON-LD) | ✅ site | Meta + OG + `og:image` + **JSON-LD**; sitemap; RSS; HTML inicial SSR; `robots.txt`; home sem texto operacional | S8.7 no marketing (form SEO) |
| Páginas de produto + contato | ✅ | `#page-hub`, `#page-agendamento`, `#page-marketing`, `#page-contato` | Copy/cases contínuos |
| Painel admin desativar dica economy | ❌ | Campo `active` em `economy_tips` | Endpoint interno ou SQL manual |
| Backoffice editorial no site | ❌ | Conteúdo via marketing + pipeline JSON | Fora do escopo MVP |
| Imagens / object storage | 🟡 | **Desenho A:** URL externa + embed vídeo | R2 = Desenho B |

---

## Integrações

| Gap | Status | O que existe | O que falta |
|-----|--------|--------------|-------------|
| Sirius Marketing → news | ✅ | `InternalNewsController`, idempotência `external_id` | Smoke prod pós-redeploy 0.8.0 |
| Marketing → highlights (Radar) | ✅ API existe | **Artigos marketing não usam** (27/07) | Pipeline / manual |
| Marketing → economy tips | ✅ | V6 + `InternalEconomyTipController` | Smoke pós-redeploy |
| Pipeline RSS economy tips | ✅ | CI 2×/dia, merge na home | Ampliar feeds / curadoria |
| Notificação de novo lead | ✅ | Lead + e-mail Resend (`LeadEmailNotifier`) | Configurar `TRCON_SITE_MAIL_*` no Coolify |
| CRM externo | ❌ | — | Export ou integração futura |

---

## Segurança

| Item | Status | Implementação | Pendência |
|------|--------|---------------|-----------|
| API interna protegida | ✅ | `InternalApiKeyFilter` — header `X-API-Key` em `/api/internal/*` | Rotacionar chave; nunca commitar |
| CORS | ✅ | `WebConfig` + `TRCON_CORS_ALLOWED_ORIGINS` | Prod: só domínios site |
| Segredos no frontend | ✅ | Sem API key no JS; só URLs públicas em `env.js` | — |
| Indexação apps internos | ✅ | Marketing: `noindex` + `X-Robots-Tag`; site: `robots.txt` institucional | Confirmar em prod após redeploy marketing |
| Exposição de estados editoriais | ✅ | Home sem placeholders “Carregando…/Atualizando…”; seções vazias ocultas | — |
| Rate limit leads | ❌ | Validação + duplicidade e-mail/origem | Cloudflare WAF em `POST .../leads` (recomendado 10 req/min/IP) |
| Rate limit API interna | ❌ | Só API key | CF WAF em `/api/internal/*` |
| Auth usuário final | ❌ | Decisão: não no MVP site | Área privada futura |
| LGPD leads | 🟡 | `consentimentoLgpd` obrigatório; persistência | Exportação/exclusão/retention policy |
| TLS | ✅ | Cloudflare Full (strict) + Let's Encrypt origem | — |
| Actuator exposto | 🟡 | `/actuator/health`; details off prod | Não expor métricas sensíveis publicamente |
| Logs sem PII | 🟡 | Logs Spring padrão | Revisar se e-mail/telefone vazam em debug |
| E-mail lead (headers) | ✅ | Escape HTML + strip CR/LF; Reply-To = lead | Domínio remetente verificado no Resend |

### Checklist segurança mínima prod

- [x] `TRCON_SITE_INTERNAL_API_KEY` forte (32+ bytes hex) no Coolify
- [x] CORS restrito aos domínios do site
- [ ] `TRCON_SITE_MAIL_API_KEY` + from verificado no Resend
- [ ] Rate limit Cloudflare em leads e auth interno
- [ ] Backup Neon + teste restore documentado
- [ ] Rotina rotação API key site ↔ marketing

Referência ecossistema: [`sirius-marketing/projeto/docs/cursor/10_estrategia_infra_ecossistema.md`](../../sirius-marketing/projeto/docs/cursor/10_estrategia_infra_ecossistema.md) §4.4.

---

## Operações

| Gap | Status | O que existe | O que falta |
|-----|--------|--------------|-------------|
| Deploy backend Coolify | ✅ | `backend/Dockerfile`, healthcheck 90s | Manter Flyway alinhado (V8) |
| Deploy frontend Coolify | 🟡 | `frontend/Dockerfile` + proxy `/novidades/` (`SITE_API_UPSTREAM`) | DNS `@`/`www` → Hetzner; env `SITE_API_UPSTREAM` no Coolify |
| Ambiente staging | ❌ | dev local + prod | Neon branch + subdomínio |
| Monitoramento / alertas | 🟡 | Actuator + logs Coolify | Uptime + 5xx + health Neon |
| Runbooks | ✅ | [`12-DEPLOY.md`](12-DEPLOY.md), [`13-AMBIENTE-LOCAL-TESTES.md`](13-AMBIENTE-LOCAL-TESTES.md) | Atualizar pós-cada release |
| Neon compute idle | 🟡 | Hikari min-idle=0; health DB off | Stop API fora do horário (opcional) |
| Backup PostgreSQL | 🟡 | Neon gerenciado | PITR pago; teste restore trimestral |
| Descontinuar Render/Fly | ❌ | Artefatos legado em `infra/` | Após frontend no Hetzner |

---

## Qualidade

| Gap | Status | O que existe | O que falta |
|-----|--------|--------------|-------------|
| Testes backend ≥ 80% | ✅ | JaCoCo gate CI (~80%+ branch / ~95%+ line); Testcontainers | Manter ao adicionar módulos |
| Testes frontend Vitest | ✅ | config, content, lead-form, sanitize | E2E browser |
| Testes pipeline Python | ✅ | `scripts/tests/test_pipeline.py` | — |
| E2E marketing → site → home | ❌ | Testes separados por repo | Playwright cross-stack |
| Pen test público | ❌ | — | Antes de escala de leads/SEO |

---

## Priorização (16/08/2026)

| Prioridade | Item |
|------------|------|
| **Alta** | Redeploy prod **0.8.0** (backend SSR + frontend proxy) + smoke View Source `/novidades/{slug}` |
| **Alta** | Coolify frontend: `SITE_API_UPSTREAM` + DNS `@`/`www` → Hetzner |
| **Alta** | Coolify mail lead (`TRCON_SITE_MAIL_*`) + smoke formulário |
| **Média** | Rate limit CF leads + alertas |
| **Média** | S8.7 SEO no form marketing |
| **Baixa** | LGPD export, staging, Desenho B (R2), CRM |

---

## Resumo

O **backend e o frontend (0.8.0)** cobrem leads (com notificação), Radar, Novidades (artigo SSR + SEO), Educação Financeira e páginas de produto/contato. Gaps restantes:

1. **Operacional** — redeploy 0.8.0, `SITE_API_UPSTREAM`, DNS frontend, vars Resend, smoke.
2. **SEO residual** — campos SEO no marketing (S8.7).
3. **Segurança escala** — rate limit borda, LGPD leads, alertas.
4. **Adiados** — backoffice, CRM, object storage (Desenho B).
