# Documentação Canônica — TRCon Group / Site

Monorepo do site institucional da **TRCon — Tecnologia, Inteligência e Resultados**.

Estrutura física:

```text
trcongroup/
  plataforma-agendamento-inteligente/   (outro produto — não relacionado, não mexer)
  site/
    backend/    -> API própria (Java/Spring Boot)
    frontend/   -> site público (recebe o conteúdo hoje em fluxo-caixa-app/site-trcon)
    infra/      -> IaC, docker-compose, pipelines, ambientes
    doc/        -> esta pasta (fonte de verdade documental)
    .claude/    -> skills e agents do Claude Code para este projeto
```

> **Estado 16/08/2026:** frontend + backend site **0.8.0** (S8 + Desenho A + SEO SSR/JSON-LD + higiene de indexação).
> A pasta `fluxo-caixa-app/site-trcon` permanece como histórico até decisão de corte ([`16-PASSO-A-PASSO.md`](./16-PASSO-A-PASSO.md) Fase 9).
> Documentação operacional atual: [`14-STATUS-IMPLEMENTACAO.md`](./14-STATUS-IMPLEMENTACAO.md), [`15-GAPS-PRODUCAO-SEGURANCA.md`](./15-GAPS-PRODUCAO-SEGURANCA.md), [`16-PASSO-A-PASSO.md`](./16-PASSO-A-PASSO.md), [`18-MANUAL-MARKETING-EDITORIAL.md`](./18-MANUAL-MARKETING-EDITORIAL.md), [`19-DESENHO-MIDIA.md`](./19-DESENHO-MIDIA.md).

## Ordem de leitura recomendada

**Operacional (estado atual do código — 16/08/2026):**

1. [`14-STATUS-IMPLEMENTACAO.md`](./14-STATUS-IMPLEMENTACAO.md) — matriz backend/frontend/infra
2. [`16-PASSO-A-PASSO.md`](./16-PASSO-A-PASSO.md) — feito / em andamento / a fazer
3. [`15-GAPS-PRODUCAO-SEGURANCA.md`](./15-GAPS-PRODUCAO-SEGURANCA.md) — gaps, segurança, prioridades
4. [`12-DEPLOY.md`](./12-DEPLOY.md) — runbook Coolify/Hetzner/Neon
5. [`13-AMBIENTE-LOCAL-TESTES.md`](./13-AMBIENTE-LOCAL-TESTES.md) — dev local + smoke
6. [`18-MANUAL-MARKETING-EDITORIAL.md`](./18-MANUAL-MARKETING-EDITORIAL.md) — mapa editorial marketing → site
7. [`17-CUSTOS-S8-MIDIA-IA.md`](./17-CUSTOS-S8-MIDIA-IA.md) — custos S8, mídia, IA imagem
8. [`19-DESENHO-MIDIA.md`](./19-DESENHO-MIDIA.md) — desenho A (URL/embed) + B (R2 + IA); canônico no marketing
9. [`20-LOTE-EDITORIAL-SEMANAL.md`](./20-LOTE-EDITORIAL-SEMANAL.md) — impacto no site do lote semanal (proposta; canônico no marketing)

**Especificação e arquitetura (fonte de decisão):**

1. [01-POSICIONAMENTO-INSTITUCIONAL.md](./01-POSICIONAMENTO-INSTITUCIONAL.md)
2. [02-ARQUITETURA-CANONICA.md](./02-ARQUITETURA-CANONICA.md)
3. [03-FRONTEND-STACK-CANONICA.md](./03-FRONTEND-STACK-CANONICA.md)
4. [04-BACKEND-STACK-CANONICA.md](./04-BACKEND-STACK-CANONICA.md)
5. [05-BACKEND-ARQUITETURA-MVC.md](./05-BACKEND-ARQUITETURA-MVC.md)
6. [06-BACKEND-MINIMO-ESPECIFICACAO.md](./06-BACKEND-MINIMO-ESPECIFICACAO.md)
7. [07-MIGRACAO-PARALELA.md](./07-MIGRACAO-PARALELA.md)
8. [08-REDESIGN-DIRETRIZES.md](./08-REDESIGN-DIRETRIZES.md)
9. [09-PLANO-EXECUCAO-IA.md](./09-PLANO-EXECUCAO-IA.md)
10. [10-TESTES-QUALIDADE.md](./10-TESTES-QUALIDADE.md)
11. [11-SKILLS-AGENTS-CLAUDE.md](./11-SKILLS-AGENTS-CLAUDE.md)

## Papel de cada documento

| Documento | Papel |
|---|---|
| 01-POSICIONAMENTO-INSTITUCIONAL | Quem é a TRCon, o que vende, tom de voz, estrutura de páginas |
| 02-ARQUITETURA-CANONICA | Decisão de arquitetura híbrida (frontend estático + backend + pipeline) |
| 03-FRONTEND-STACK-CANONICA | Stack oficial do frontend (HTML/CSS/JS, build, estrutura de pastas, testes, integração com a API) |
| 04-BACKEND-STACK-CANONICA | Stack oficial do backend (Java/Spring Boot/Postgres) |
| 05-BACKEND-ARQUITETURA-MVC | Camadas MVC detalhadas: controller/service/repository/mapper/VO/DTO/domain/shared + SOLID |
| 06-BACKEND-MINIMO-ESPECIFICACAO | Primeira versão implementável do backend (módulos, entidades, endpoints) |
| 07-MIGRACAO-PARALELA | Como migrar o frontend atual e introduzir o backend sem tirar o site do ar |
| 08-REDESIGN-DIRETRIZES | Limites da evolução visual, identidade da marca |
| 09-PLANO-EXECUCAO-IA | Plano concreto: fases, prazos, pastas, custos, critérios de pronto |
| 10-TESTES-QUALIDADE | Estratégia de testes (backend e frontend), gate de cobertura ≥ 80%, ferramentas |
| 11-SKILLS-AGENTS-CLAUDE | Skills e subagents do Claude Code usados para construir e manter o projeto |
| 12-DEPLOY | Runbook de deploy em produção: Cloudflare + Hetzner + Coolify + Neon |
| 13-AMBIENTE-LOCAL-TESTES | Ambiente local, testes, integração com Sirius Marketing |
| 18-MANUAL-MARKETING-EDITORIAL | Mapa editorial Sirius Marketing → seções do site (Radar ≠ Novidades) |
| **14-STATUS-IMPLEMENTACAO** | **Matriz do que existe hoje (backend, frontend, infra, marketing)** |
| **15-GAPS-PRODUCAO-SEGURANCA** | **Gaps produção, segurança, priorização** |
| **16-PASSO-A-PASSO** | **Feito / em andamento / a fazer (visão consolidada)** |
| **17-CUSTOS-S8-MIDIA-IA** | **Custos Sprint 8, imagens, vídeo, IA visual (Sprint 9)** |
| **19-DESENHO-MIDIA** | **Desenho A (URL/embed) + B (R2 + IA); canônico no marketing `13_desenho_…`** |

## Regra de governança

Em caso de conflito sobre **estado atual vs spec**:

1. **14-STATUS-IMPLEMENTACAO** e **16-PASSO-A-PASSO** prevalecem sobre status operacional
2. **15-GAPS-PRODUCAO-SEGURANCA** prevalece sobre lacunas de produção e segurança

Em caso de conflito sobre **decisões técnicas e spec**:

1. 02-ARQUITETURA-CANONICA prevalece sobre decisões técnicas amplas
2. 03-FRONTEND-STACK-CANONICA prevalece sobre decisões específicas de frontend
3. 04-BACKEND-STACK-CANONICA prevalece sobre decisões específicas de backend
4. 05-BACKEND-ARQUITETURA-MVC prevalece sobre estilo de código e camadas
5. 06-BACKEND-MINIMO-ESPECIFICACAO prevalece sobre detalhes de implementação inicial
6. 07-MIGRACAO-PARALELA prevalece sobre estratégia de transição e segurança operacional
7. 08-REDESIGN-DIRETRIZES prevalece sobre decisões visuais
8. 09-PLANO-EXECUCAO-IA organiza prioridade, sequenciamento, prazos e custo
9. 10-TESTES-QUALIDADE prevalece sobre critério de aceite de qualidade

## Objetivo geral

Construir um site e uma plataforma que:

- representem a TRCon como empresa de tecnologia, IA, finanças e resultados de porte real
- vendam, demonstrem e sustentem os produtos e serviços da TRCon (venda/desenvolvimento/customização de software e alocação de mão de obra em tecnologia)
- tenham arquitetura MVC limpa, testável e aderente a SOLID no backend, e uma stack de frontend leve, organizada e testável
- mantenham cobertura de testes unitários e de integração ≥ 80%
- operem com baixo custo de infraestrutura
- sejam construídos e mantidos majoritariamente por execução assistida por IA (Claude Code), com checkpoints humanos de revisão
