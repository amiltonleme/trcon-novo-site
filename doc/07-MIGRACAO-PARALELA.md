# Plano de Migração Paralela — TRCon Site

## Objetivo

Definir como (a) o frontend hoje em `C:\projetos-al\fluxo-caixa-app\site-trcon` migra
para `trcongroup/site/frontend`, e (b) o backend novo (`trcongroup/site/backend`) entra
em produção, sem tirar o site do ar e sem misturar responsabilidades.

## Princípio central

O site atual (em `fluxo-caixa-app/site-trcon`) continua sendo a superfície estável até
que `trcongroup/site` seja validado. Nenhuma remoção acontece antes da validação.

## Regras obrigatórias

1. Não remover o frontend/publicação atual antes da validação do novo local.
2. Não acoplar o frontend a recursos novos (backend, nova pasta) sem fallback.
3. Toda substituição deve ser reversível com baixo impacto.
4. A nova arquitetura entra por incremento, nunca por reescrita total de uma vez.

## Estado atual (jul/2026)

- frontend em **`trcongroup/site/frontend`** — paridade com antigo `fluxo-caixa-app/site-trcon` + evoluções (Serviços, API, economy tips)
- backend Spring Boot em **`trcongroup/site/backend`** — leads, highlights, news, economy tips, APIs internas marketing
- **Produção:** backend no Coolify (`api-site.trcongroup.com.br`); frontend migrando DNS para Hetzner
- pipeline de conteúdo via GitHub Actions (`update-content.yml`, 2×/dia UTC)
- integração Sirius Marketing ativa no código (smoke prod pós-redeploy V6)

## Estado alvo (quase alcançado)

- `trcongroup/site/frontend` como **única** fonte publicada (`@`/`www` → Hetzner)
- `fluxo-caixa-app/site-trcon` congelado como histórico ([`16-PASSO-A-PASSO.md`](./16-PASSO-A-PASSO.md) F9)
- convivência JSON (fallback) + API própria — **implementada** (`fetchWithFallback`, `loadEconomyTips`)

## Fases de migração

### Fase 0 — Fundação documental ✅

### Fase 1 — Backend isolado ✅

### Fase 2 — Cópia/migração física do frontend ✅

### Fase 3 — Formulário → backend ✅

### Fase 4 — Home consumindo highlights/news via API ✅

`TRCON_HIGHLIGHTS_API_URL`, `TRCON_NEWS_API_URL`, `TRCON_ECONOMY_TIPS_API_URL` em `env.js`.

### Fase 5 — Consolidação e corte oficial 🟡

DNS `@`/`www` → Hetzner pendente; `fluxo-caixa-app/site-trcon` ainda não descontinuado formalmente.

## Fallback por capacidade

| Capacidade | Se backend falhar |
|---|---|
| Leads | exibir erro claro no formulário, sem quebrar a página |
| Highlights | usar JSON local (`home-highlights.json`) se API vazia ou offline |
| News | usar JSON local (`news-log.json`) se API vazia ou offline |
| Economy tips | merge API + `economy-tips.json`; JSON se API offline |

## Rollout por configuração

- `TRCON_LEADS_API_URL`
- `TRCON_HIGHLIGHTS_API_URL`
- `TRCON_NEWS_API_URL`
- `TRCON_ECONOMY_TIPS_API_URL`

Ausência de valor = usa o comportamento estático atual. Nenhuma URL fica hardcoded
no frontend.

## Critérios de promoção de uma capacidade nova

- passou em testes locais e de integração (≥ 80% cobertura)
- tem comportamento degradado conhecido e testado
- não introduz indisponibilidade no site público
- possui rollback claro (variável de ambiente revertida)

## Critérios de remoção do fluxo antigo (`fluxo-caixa-app/site-trcon`)

- `trcongroup/site/frontend` estável em produção por um período de observação
- backend novo estável (sem incidentes) no mesmo período
- fallback comprovadamente desnecessário
- decisão explícita do responsável pelo projeto — remoção não é automática

## Decisão final

Migração paralela, por capacidade, com fallback explícito e rollback simples.
Nenhuma etapa remove o que está no ar antes de a etapa seguinte estar validada.
