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

## Estado atual

- frontend estático publicado a partir de `fluxo-caixa-app/site-trcon` (`index.html`, `style.css`, `assets/app.js`, `data/*.json`)
- pipeline de conteúdo via GitHub Actions (`update-site-data.yml`, a cada 6h)
- nenhum backend próprio
- nenhum monorepo `trcongroup/site` populado com código ainda (só estrutura de pastas e documentação)

## Estado alvo

- `trcongroup/site/frontend` como novo lar do frontend público (idêntico ao atual + evoluções do backlog institucional)
- `trcongroup/site/backend` como API própria (leads, highlights, news)
- `trcongroup/site/infra` com docker-compose local e pipeline de CI/CD
- convivência controlada entre JSON público (fallback) e API própria

## Fases de migração

### Fase 0 — Fundação documental (concluída nesta rodada)
- estrutura de pastas `trcongroup/site/{frontend,backend,infra,doc}`
- documentação canônica completa
- skills e agents do Claude Code

Risco: nenhum. Impacto no site publicado: nenhum.

### Fase 1 — Backend isolado sem impacto público
- criar projeto Spring Boot em `trcongroup/site/backend`
- subir PostgreSQL local via `trcongroup/site/infra/docker-compose.yml`
- módulo `lead` completo com testes (≥ 80% cobertura)
- nenhuma integração com o frontend público ainda

Risco: baixo. Impacto: nenhum (backend não está no ar publicamente).

### Fase 2 — Cópia/migração física do frontend
- copiar o conteúdo de `fluxo-caixa-app/site-trcon` (html/css/assets/data/scripts) para `trcongroup/site/frontend`
- `fluxo-caixa-app/site-trcon` permanece intacto e publicado até a nova pasta estar validada (build local ok, paridade visual confirmada)
- pipeline de CI/CD do novo local criado em paralelo, sem desligar o pipeline antigo

Estratégia de reversão: manter o repositório/pasta antiga publicando normalmente
até o corte oficial (Fase 5).

### Fase 3 — Formulário do site aponta para o backend novo
- formulário de contato/lead do frontend passa a chamar `POST /api/v1/site/leads`
- variável de configuração (`TRCON_LEADS_API_URL`) controla o endpoint — se vazio/indisponível, cai para o comportamento atual (mailto ou formulário estático)
- endpoints públicos de highlights/news disponíveis para teste, mas frontend continua consumindo os JSON estáticos por padrão

Impacto: limitado a uma funcionalidade (formulário), com fallback.

### Fase 4 — Home consumindo highlights/news via API (opcional, por configuração)
- flag decide se a home lê de `data/*.json` ou da API pública
- fallback automático para JSON se a API não responder

### Fase 5 — Consolidação e corte oficial
- `trcongroup/site` passa a ser a fonte publicada oficial
- DNS/publicação apontam para o novo local
- `fluxo-caixa-app/site-trcon` é congelado como histórico (não apagado nesta fase — remoção é decisão explícita e posterior, fora deste plano)

## Fallback por capacidade

| Capacidade | Se backend falhar |
|---|---|
| Leads | exibir erro claro no formulário, sem quebrar a página |
| Highlights | usar JSON local ou último payload válido |
| News | usar JSON local ou reduzir bloco a estado conservador |

## Rollout por configuração

- `TRCON_LEADS_API_URL`
- `TRCON_HIGHLIGHTS_API_URL`
- `TRCON_NEWS_API_URL`

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
