# Backend Stack Canônica — TRCon Site

## Objetivo

Definir a stack oficial de `trcongroup/site/backend`: leve, de baixo custo, compatível
com a arquitetura MVC obrigatória e pronta para crescer sem retrabalho.

## Decisão oficial

- Java 21
- Spring Boot 3.5.x

Mantém a mesma família tecnológica usada nos demais backends do grupo — menor fricção
de manutenção, menor custo cognitivo, maior consistência.

## Stack

| Camada | Escolha |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 3.5.x |
| Web | Spring Web MVC |
| Persistência | Spring Data JPA |
| Banco | PostgreSQL |
| Migrações | Flyway |
| Validação | spring-boot-starter-validation |
| Observabilidade | Spring Boot Actuator |
| Testes unitários | JUnit 5, Mockito |
| Testes de integração | Spring Boot Test, Testcontainers (PostgreSQL) |
| Cobertura | JaCoCo (gate ≥ 80%, ver [10-TESTES-QUALIDADE.md](./10-TESTES-QUALIDADE.md)) |
| Build | Maven |
| Ambiente local | Docker Compose (`site/infra`) |
| Mapeamento objeto↔objeto | MapStruct (mappers explícitos, ver [05-BACKEND-ARQUITETURA-MVC.md](./05-BACKEND-ARQUITETURA-MVC.md)) |

## O que não usar no início

Spring WebFlux, microsserviços, Kafka, Redis, NoSQL, mensageria distribuída,
arquitetura orientada a eventos como fundação inicial. Só entram com demanda concreta.

## Arquitetura recomendada

**Modular monolith MVC** — uma aplicação, módulos por domínio, fronteiras claras entre
`controller / service / repository / mapper / domain / dto / vo` e um `shared/` transversal.
Ver especificação completa em [05-BACKEND-ARQUITETURA-MVC.md](./05-BACKEND-ARQUITETURA-MVC.md).

## Módulos iniciais (domínios)

1. **lead** — captação comercial (produto, dev sob demanda, customização, staffing) — substitui o antigo módulo "waitlist", generalizado para os 4 tipos de negócio da TRCon
2. **content** — leitura de conteúdo persistido / novidades
3. **highlights** — destaques principais da home
4. **shared** — config, persistência comum, serialização, tratamento de erro, segurança comum, VOs transversais

## Banco de dados

PostgreSQL — maduro, padrão de mercado, boa integração com Spring Boot/JPA, bom
equilíbrio simplicidade/robustez.

## Fronteira entre banco e JSON

**Vão para banco**: leads comerciais, dados de usuário, histórico persistido de
novidades, destaques configuráveis, configurações administrativas, feature flags.

**Podem continuar em JSON gerado**: radar diário público, cards editoriais da home,
sinais de mercado públicos, resumos automatizados de baixo risco.

## Contrato HTTP inicial

### Público
- `GET /api/public/highlights`
- `GET /api/public/news`
- `GET /api/public/status`

### Leads
- `POST /api/v1/site/leads`
- `GET /api/v1/site/leads/{id}` (uso interno/admin, se necessário)

### Health
- `GET /actuator/health`

## Estilo de arquitetura no código

`controller → service → repository`, com `mapper` fazendo a tradução
`domain ↔ dto/vo` em cada fronteira. Ver [05-BACKEND-ARQUITETURA-MVC.md](./05-BACKEND-ARQUITETURA-MVC.md)
para regras completas e exemplos.

## Segurança mínima

- validação de payload (Bean Validation)
- sanitização básica
- CORS controlado (sem wildcard em produção)
- segredos via variáveis de ambiente (nunca hardcoded)
- rate limiting no endpoint de leads (proteção contra spam de formulário)

## Deploy e operação

- aplicação Spring Boot empacotada em container (imagem definida em `site/infra`)
- PostgreSQL gerenciado ou em container, conforme ambiente
- variáveis por ambiente (`local`, `dev`, `prod`)
- healthcheck via Actuator

## Ambiente local

Docker Compose subindo `backend` + `postgres` (definido em `site/infra/docker-compose.yml`,
a ser criado na fase de implementação — ver [09-PLANO-EXECUCAO-IA.md](./09-PLANO-EXECUCAO-IA.md)).

## Quando revisar esta decisão

Apenas se houver demanda forte por alta concorrência reativa, necessidade operacional
comprovada de separar serviços, ou padrão corporativo obrigando outra base.
