# Testes e Qualidade — TRCon Site

## Objetivo

Garantir que todo backend em `site/backend` mantenha cobertura de testes
**unitários e de integração acima de 80%**, com testes que validam comportamento
real (não só cobertura por vaidade de métrica), e que a lógica não trivial do
frontend em `site/frontend` (ver [03-FRONTEND-STACK-CANONICA.md](./03-FRONTEND-STACK-CANONICA.md))
também seja coberta por teste automatizado.

## Pirâmide de testes

1. **Unitários** — maioria dos testes. Cobrem `service`, `mapper`, VOs, validações
   customizadas. Isolados via Mockito, sem subir Spring Context.
2. **Integração** — cobrem `controller` + `service` + `repository` + banco real via
   Testcontainers (PostgreSQL). Validam contrato HTTP (status code, corpo, erros).
3. **Contrato/borda** (quando aplicável) — validam que providers/adapters
   alternativos (ex.: dois `ContentProvider`) respeitam o mesmo contrato (LSP).

## O que testar em cada camada

| Camada | O que testar | O que não testar |
|---|---|---|
| `controller` | status code, serialização de request/response, tratamento de erro | regra de negócio (já coberta no service) |
| `service` | regra de negócio, casos de borda, exceções de domínio | detalhe de SQL/HTTP |
| `repository` | query customizada, constraint de unicidade (via Testcontainers) | regra de negócio |
| `mapper` | mapeamento correto de todos os campos, inclusive nulos/opcionais | — |
| `domain`/VO | validação na construção, comportamento próprio do objeto | — |

## Ferramentas

- JUnit 5 + Mockito — testes unitários
- Spring Boot Test + Testcontainers (PostgreSQL) — testes de integração
- JaCoCo — medição de cobertura (linha e branch)
- AssertJ — assertions legíveis

## Gate de cobertura (obrigatório no CI)

- cobertura mínima de **80% de linha e 80% de branch** por módulo (`lead`,
  `highlights`, `news`, `shared`)
- build falha (`mvn verify`) se qualquer módulo ficar abaixo do limite
- configuração via `jacoco-maven-plugin` com regra `PACKAGE` mínima de 0.80

## Regras de teste

1. Todo `service` público tem teste unitário cobrindo caminho feliz e pelo menos
   um caminho de erro/borda.
2. Todo endpoint público tem teste de integração cobrindo 2xx e o principal erro
   esperado (400/409/404, conforme o caso).
3. Testes de integração usam Testcontainers — nunca H2 ou mock de banco para
   validar comportamento real de constraint/índice.
4. Não é permitido `@Disabled`/teste comentado sem justificativa registrada no
   próprio código (comentário curto explicando o motivo temporário).
5. Teste não deve depender de ordem de execução nem de estado deixado por outro teste.

## Quando falha o gate

Se cobertura cair abaixo de 80% em um módulo:

- a sessão de execução de IA deve adicionar os testes faltantes antes de propor
  o merge — não é aceitável reduzir o limite do gate para "passar"
- o agent `trcon-qa-reviewer` (ver [11-SKILLS-AGENTS-CLAUDE.md](./11-SKILLS-AGENTS-CLAUDE.md))
  é responsável por apontar exatamente quais classes/métodos ficaram descobertos

## Testes de frontend

Stack e estrutura de pastas em [03-FRONTEND-STACK-CANONICA.md](./03-FRONTEND-STACK-CANONICA.md).

| O que | Testar? | Ferramenta |
|---|---|---|
| HTML/CSS estático | não | — |
| `assets/modules/config.js` (seleção de URL por ambiente) | sim | Vitest |
| `assets/modules/highlights.js` / `news.js` (parsing de payload, ativação de fallback) | sim | Vitest |
| `assets/modules/lead-form.js` (validação de payload antes do envio) | sim | Vitest |
| manipulação direta de DOM/animações | não (verificação manual em checkpoint) | — |

Regras:

1. Função de parsing/composição de dados vive isolada de manipulação de DOM
   (ver regra 4 de `03-FRONTEND-STACK-CANONICA.md`) especificamente para viabilizar
   este teste sem precisar de navegador.
2. Todo módulo com fallback (highlights, news, lead-form) tem teste cobrindo o
   caminho de sucesso e o caminho de fallback (API indisponível).
3. Frontend não tem gate numérico de cobertura obrigatório (não é o critério
   central como no backend) — o critério é: **toda função não trivial tem teste
   cobrindo seu caso de borda principal**. Ausência de teste em lógica nova de
   parsing/fallback é bloqueante na revisão, mesmo sem métrica de cobertura.

## Critério de pronto de qualidade

- pipeline de CI roda testes de backend (gate 80%) e testes de frontend (Vitest) a cada PR
- nenhum merge em `main` acontece com o gate de cobertura do backend vermelho ou com teste de frontend quebrado
- relatório de cobertura do backend acessível como artefato do CI
