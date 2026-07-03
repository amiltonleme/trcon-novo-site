# Plano Concreto de Execução — TRCon Site

## Premissa central

Todo o desenvolvimento (backend, frontend, infra, testes) é executado por IA
(Claude Code), orquestrado em sessões, com **checkpoints humanos** de decisão e
aprovação entre fases — não de escrita de código. Por isso:

- prazo é medido em **sessões de execução + tempo de revisão humana**, não em
  dias-pessoa de desenvolvedor
- custo é medido em **infraestrutura e uso de API de IA**, não em folha de
  pagamento/hora técnica

Uma "sessão de execução" = uma rodada de Claude Code focada em um entregável
fechado (ex.: "módulo lead completo com testes"), tipicamente concluída em
minutos a poucas horas de processamento, mas o calendário real depende de quando
o humano revisa e aprova o checkpoint para liberar a próxima sessão.

## Pastas envolvidas

```text
trcongroup/
  site/
    frontend/   <- recebe o conteúdo de fluxo-caixa-app/site-trcon (Fase 2)
    backend/    <- novo projeto Spring Boot MVC (Fase 1)
    infra/      <- docker-compose, pipelines de CI/CD (Fase 1 e 6)
    doc/        <- esta documentação (concluída nesta rodada)
    .claude/
      skills/   <- trcon-backend-scaffold, trcon-coverage-gate (concluído nesta rodada)
      agents/   <- trcon-backend-architect, trcon-qa-reviewer (concluído nesta rodada)
```

## Fases, entregas, esforço e checkpoint

| Fase | Entrega | Esforço de execução IA | Checkpoint humano | Depende de |
|---|---|---|---|---|
| 0 | ✅ **Concluída.** Specs (incluindo stack de frontend e backend), skills, agents, estrutura de pastas (**este documento e seus pares**) | 1 sessão | Aprovar specs e nomenclatura de domínio (`Lead`, `LeadType`) | — |
| 1 | ✅ **Concluída.** Backend mínimo: projeto Spring Boot, módulos `lead`/`highlights`/`news`, Flyway, Actuator, testes com cobertura 93,7% linha / 84,4% branch (gate ≥ 80% atingido) | 3–5 sessões | Rodar local, validar contrato HTTP, aprovar cobertura | Fase 0 |
| 2 | ✅ **Concluída.** `infra`: `docker-compose.yml` (backend+Postgres) validado ponta a ponta + `Dockerfile` multi-stage + pipeline de CI (`backend-ci.yml`) com gate de cobertura | 1–2 sessões | Validar pipeline verde, aprovar critérios de gate | Fase 1 |
| 3 | ✅ **Concluída.** Cópia do frontend atual para `site/frontend` conforme [03-FRONTEND-STACK-CANONICA.md](./03-FRONTEND-STACK-CANONICA.md) — reorganizado em `assets/modules/` (`config.js`, `sanitize.js`), `app.js` como ES module, 19 testes Vitest + ESLint/Prettier. Paridade preservada; pasta antiga intacta | 1–2 sessões | Validar paridade visual e funcional | Fase 0 |
| 4 | ✅ **Concluída (aguardando revisão de copy).** Página Serviços (`page-servicos`) com as 4 linhas de negócio detalhadas + modelos de engajamento de staffing, blocos das 4 linhas na home, item de menu Serviços (nav/mobile/footer), CTAs com `data-lead-type` prontos para a Fase 5. Identidade visual preservada (validado em navegador) | 2–3 sessões | Revisar copy e aprovar textos institucionais | Fase 3 |
| 5 | ✅ **Concluída.** Integração frontend → backend: módulo `lead-form.js` (`buildLeadPayload`/`submitLead`/`mensagemDeErro`), formulário com `tipoInteresse`, CTAs pré-selecionando o tipo via `data-lead-type`, config apontando para `/api/v1/site/leads`. Validado no navegador: 201 (persistido), 409 duplicado e fallback de backend offline. 32 testes Vitest | 1–2 sessões | Testar formulário ponta a ponta em ambiente de teste | Fases 1, 2, 4 |
| 6 | ✅ **Concluída.** Pipeline SOLID (`core`/`providers`/`builders` + scripts finos) gerando `ai-radar.json`, `tech-radar.json`, `home-highlights.json`, `news-log.json` (shape = contratos do backend). 16 testes unittest, fallback ao último artefato validado, workflow `update-content.yml` 2x/dia | 2–3 sessões | Aprovar curadoria/fontes usadas | Fase 3 |
| 7 | ✅ **Concluída.** Home com seções Radar TRCon (highlights) e Novidades (news), módulo `content.js` (`fetchWithFallback` API→JSON) + renderizadores puros. Validado no navegador: consome da API quando disponível (`source: api`) e cai para JSON estático quando o backend está fora (`source: json`). 45 testes Vitest | 1–2 sessões | Validar comportamento degradado (backend fora do ar) | Fases 1, 5, 6 |
| 8 | ⏳ **Artefatos prontos (aguardando provisionamento humano).** `render.yaml` (Blueprint), `fly.toml`, `backend-cd.yml`, `env.js` (injeção de URLs), `application.yml` prod (PORT/DB por partes) e runbook [12-DEPLOY.md](./12-DEPLOY.md). Falta: criar contas cloud, domínio e segredos — ver "limite honesto" no runbook | 1 sessão de execução + tempo de provisionamento externo (contas, DNS) | Aprovar custo de infra e liberar produção | Fases 1–7 |
| 9 | Consolidação: observação, ajuste fino, decisão de descontinuar `fluxo-caixa-app/site-trcon` | contínuo | Decisão explícita de corte | Fase 8 estável |

## Estimativa de calendário

Assumindo que o humano revisa cada checkpoint em até 1–2 dias úteis após a
sessão de execução (não em tempo real):

| Marco | Estimativa de calendário |
|---|---|
| Fundação (Fases 0–2): backend mínimo + infra local rodando | **1 a 2 semanas** |
| Frontend migrado + institucional (Fases 3–4) | **+1 semana** |
| Integração completa e home dinâmica (Fases 5–7) | **+1 a 2 semanas** |
| Deploy em produção (Fase 8) | **+2 a 5 dias** (depende de provisionamento de conta cloud/domínio, fora do controle da IA) |
| **Total até produção estável** | **4 a 6 semanas de calendário**, assumindo revisão humana ágil (não bloqueada por dias sem resposta) |

Esse prazo é dominado pelo tempo de decisão/revisão humana entre fases, não pelo
tempo de execução da IA — cada sessão individual de código é rápida; o gargalo
real é aprovação de escopo, copy institucional e provisionamento de contas
externas (domínio, cloud, e-mail transacional).

## Custos estimados

Sem custo de mão de obra (execução por IA). Custos reais são de **infraestrutura
e ferramentas**:

| Item | Estimativa mensal (BRL) | Observação |
|---|---|---|
| Domínio (.com.br ou .com) | ~R$ 3–5/mês (cobrança anual ~R$ 40–60) | já pode existir |
| Hospedagem do backend (container pequeno: Fly.io, Render, Railway ou VPS básica) | R$ 30–120/mês | plano de entrada é suficiente para o volume inicial (site institucional, baixo tráfego) |
| PostgreSQL gerenciado (ou incluso no mesmo provedor) | R$ 0–60/mês | planos free/hobby cobrem a fase inicial; revisar se volume de leads crescer |
| CI/CD (GitHub Actions) | R$ 0 | dentro do free tier para repositório deste porte |
| Hospedagem do frontend estático (GitHub Pages, Vercel, Netlify ou o provedor atual) | R$ 0 | mantém o modelo atual de baixo custo |
| Monitoramento/observabilidade básica (Actuator + logs do provedor) | R$ 0 | sem ferramenta paga na fase inicial |
| Uso de IA em lote para curadoria de conteúdo (Radar IA/Tecnologia, opcional) | R$ 0–100/mês | só se optar por Nível 2 de IA em lote (ver `ARQUITETURA-CANONICA` herdado); começar em Nível 1 (sem custo) |
| Uso de Claude Code para execução do desenvolvimento | custo já coberto pelo plano/assinatura de uso da ferramenta em vigor | não é custo adicional de infraestrutura do produto |

**Estimativa total de operação mensal na fase inicial: R$ 35 a R$ 200/mês**,
podendo começar próximo de R$ 35–60/mês usando tiers gratuitos/hobby de
hospedagem e banco, subindo conforme tráfego e volume de leads justificarem
planos pagos maiores.

Custo de setup único (não recorrente): domínio (se ainda não houver), eventual
taxa de configuração de conta cloud — tipicamente R$ 0–100.

## Critérios de pronto do plano

- backend mínimo rodando localmente com cobertura ≥ 80%
- frontend migrado para `trcongroup/site/frontend` com paridade funcional
- conteúdo institucional das 4 linhas de negócio publicado
- formulário de lead gravando no backend com fallback funcional
- pipeline de CI com gate de cobertura ativo
- deploy em produção com domínio próprio e custo mensal dentro da faixa estimada

## Regra de decisão durante a execução

Se surgir dúvida entre avançar mais rápido pulando revisão humana ou manter o
checkpoint: **manter o checkpoint**. O gargalo aceitável é tempo de aprovação
humana, não retrabalho por decisão tomada sem validação de negócio (nome de
domínio, copy institucional, custo de infra).
