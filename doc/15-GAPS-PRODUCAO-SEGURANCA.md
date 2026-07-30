# Gaps produção e segurança — Site TRCON

> Atualizado em **30/07/2026** — cruzado com código em `site/backend` **0.4.0** (+ V8) e `site/frontend` (`main`).

Legenda: ✅ implementado · 🟡 parcial · ❌ pendente

---

## Produto / UX

| Gap | Status | O que existe | O que falta |
|-----|--------|--------------|-------------|
| Página de artigo legível | ✅ | `slug`, `body`, `/novidades/{slug}`, capa, SEO parcial | JSON-LD `NewsArticle` |
| SEO (meta, OG, sitemap, RSS) | 🟡 | Meta + OG + **`og:image`** (capa); sitemap; RSS | JSON-LD; S8.7 no marketing |
| Páginas de produto + contato | ✅ | `#page-hub`, `#page-agendamento`, `#page-marketing`, `#page-contato` | Copy/cases contínuos |
| Painel admin desativar dica economy | ❌ | Campo `active` em `economy_tips` | Endpoint interno ou SQL manual |
| Backoffice editorial no site | ❌ | Conteúdo via marketing + pipeline JSON | Fora do escopo MVP |
| Imagens / object storage | 🟡 | **Desenho A:** URL externa + embed vídeo | R2 = Desenho B |

---

## Integrações

| Gap | Status | O que existe | O que falta |
|-----|--------|--------------|-------------|
| Sirius Marketing → news | ✅ | `InternalNewsController`, idempotência `external_id` | Smoke prod se redeploy pendente |
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
| Deploy frontend Coolify | 🟡 | `frontend/Dockerfile` | DNS `@`/`www` → Hetzner |
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

## Priorização (29/07/2026)

| Prioridade | Item |
|------------|------|
| **Alta** | Redeploy prod (V6/V7 + frontend) + smoke marketing/artigos |
| **Alta** | Coolify mail lead (`TRCON_SITE_MAIL_*`) + smoke formulário |
| **Alta** | DNS site frontend → Hetzner |
| **Média** | Rate limit CF leads + alertas |
| **Média** | Melhorias SEO (JSON-LD); S8.7 marketing |
| **Baixa** | LGPD export, staging, Desenho B (R2), CRM |

---

## Resumo

O **backend e o frontend estão funcionalmente prontos** para leads (com notificação), Radar, Novidades (artigo completo), Educação Financeira e páginas de produto/contato. Gaps restantes:

1. **Operacional** — redeploy, DNS frontend, vars Resend, smoke.
2. **SEO residual** — JSON-LD; campos SEO no marketing (S8.7).
3. **Segurança escala** — rate limit borda, LGPD leads, alertas.
4. **Adiados** — backoffice, CRM, object storage (Desenho B).
