# Especificação do Backend Mínimo — TRCon Site

## Objetivo

Primeira versão implementável de `site/backend`, coerente com:
[02-ARQUITETURA-CANONICA.md](./02-ARQUITETURA-CANONICA.md),
[04-BACKEND-STACK-CANONICA.md](./04-BACKEND-STACK-CANONICA.md),
[05-BACKEND-ARQUITETURA-MVC.md](./05-BACKEND-ARQUITETURA-MVC.md).

## Escopo da primeira versão

- persistência de **leads comerciais** (produto, desenvolvimento sob demanda, customização, alocação de mão de obra) — generalização do antigo módulo "waitlist" para refletir o posicionamento institucional ([01-POSICIONAMENTO-INSTITUCIONAL.md](./01-POSICIONAMENTO-INSTITUCIONAL.md))
- exposição de highlights públicos
- exposição de novidades públicas persistidas
- healthcheck
- base pronta para crescer sem retrabalho estrutural

## Fora de escopo da primeira versão

Autenticação de usuários finais, painel administrativo completo, upload de arquivos,
backoffice de edição, multi-tenant, mensageria, cache distribuído.

## Módulo 1 — Lead

### Finalidade
Receber qualquer contato comercial originado no site: interesse em produto, em
desenvolvimento sob demanda, em customização ou em alocação de mão de obra.

### Regras funcionais
- `nome` obrigatório
- `email` obrigatório
- `telefone` obrigatório
- `tipoInteresse` obrigatório — enum `LeadType`
- `mensagem` opcional
- `origem` obrigatória (ex.: `site-trcon-home`, `site-trcon-servicos`)
- `consentimentoLgpd` obrigatório e deve ser `true`

### Enum `LeadType`
- `PRODUTO`
- `DESENVOLVIMENTO_SOB_DEMANDA`
- `CUSTOMIZACAO`
- `ALOCACAO_MAO_DE_OBRA`

### Enum `LeadStatus`
- `PENDING`, `CONTACTED`, `QUALIFIED`, `WON`, `REJECTED`

### Regra de unicidade
Restringir e-mail único por `origem` ativa. Se já existir, responder conflito
controlado (`LEAD_DUPLICADO`).

### Entidade `Lead`
`id: UUID`, `nome`, `email`, `telefone`, `tipoInteresse: LeadType`, `mensagem?`,
`origem`, `status: LeadStatus`, `consentimentoLgpd: boolean`, `createdAt`, `updatedAt`.

### Endpoint `POST /api/v1/site/leads`

Request:
```json
{
  "nome": "Fulano da Silva",
  "email": "fulano@empresa.com",
  "telefone": "+55 11 99999-9999",
  "tipoInteresse": "ALOCACAO_MAO_DE_OBRA",
  "mensagem": "Preciso de 2 devs sêniores para squad de 3 meses.",
  "origem": "site-trcon-servicos",
  "consentimentoLgpd": true
}
```

Response 201:
```json
{ "id": "3f6f08a1-6c5a-45fd-84a5-ef4d65ce8e5f", "status": "PENDING", "message": "Cadastro recebido com sucesso." }
```

Response 409:
```json
{ "code": "LEAD_DUPLICADO", "message": "Já existe um cadastro para este email nesta origem." }
```

Response 400:
```json
{ "code": "VALIDATION_ERROR", "message": "Payload inválido.", "fields": { "email": "deve ser um email válido", "consentimentoLgpd": "deve ser verdadeiro" } }
```

## Módulo 2 — Highlights

### Entidade `DailyHighlight`
`id`, `category`, `title`, `summary`, `link?`, `priority`, `active`, `publishedAt`,
`createdAt`, `updatedAt`.

### Endpoint `GET /api/public/highlights`
- retorna apenas itens `active`
- ordena por `priority ASC`, depois `publishedAt DESC`
- limite inicial: 6 itens

## Módulo 3 — News

### Entidade `NewsItem`
`id`, `source`, `category`, `title`, `summary`, `url`, `publishedAt`,
`ingestionBatch`, `createdAt`.

### Endpoint `GET /api/public/news`
- query params: `category` opcional, `limit` opcional (teto 50)
- ordena por `publishedAt DESC`

## Módulo 4 — Health

`GET /actuator/health`.

## Banco de dados

Tabelas: `leads`, `daily_highlights`, `news_items`. Opcional: `content_updates`.

Migrations:
- `V1__create_leads.sql`
- `V2__create_daily_highlights.sql`
- `V3__create_news_items.sql`

```sql
create table leads (
  id uuid primary key,
  nome varchar(160) not null,
  email varchar(160) not null,
  telefone varchar(40) not null,
  tipo_interesse varchar(40) not null,
  mensagem text null,
  origem varchar(80) not null,
  status varchar(32) not null,
  consentimento_lgpd boolean not null,
  created_at timestamp with time zone not null,
  updated_at timestamp with time zone not null
);

create index idx_leads_email_origem on leads (email, origem);
create index idx_leads_tipo_interesse on leads (tipo_interesse);
```

## DTOs

- `LeadCreateRequest` (nome, email, telefone, tipoInteresse, mensagem, origem, consentimentoLgpd)
- `LeadCreateResponse` (id, status, message)
- `HighlightResponse` (id, category, title, summary, link, priority, publishedAt)
- `NewsItemResponse` (id, source, category, title, summary, url, publishedAt)

## Validações

- `@NotBlank` nos campos obrigatórios, `@Email`, `@Size` conservador
- validação customizada: `consentimentoLgpd == true`
- `limit` com teto máximo; `category` restrita a conjunto suportado

## Tratamento de erro padrão

```json
{ "code": "ERROR_CODE", "message": "Mensagem legível.", "timestamp": "2026-07-02T12:10:00Z", "path": "/api/v1/site/leads" }
```

Códigos: `VALIDATION_ERROR`, `LEAD_DUPLICADO`, `RESOURCE_NOT_FOUND`, `INTERNAL_ERROR`.

## Configuração por ambiente

Profiles: `local`, `dev`, `prod`. Valores sensíveis (usuário/senha de banco, origem CORS)
sempre por variável de ambiente.

## Testes mínimos obrigatórios

Ver critérios completos em [10-TESTES-QUALIDADE.md](./10-TESTES-QUALIDADE.md).

- Unitários: `LeadService`, `LeadMapper`, validações customizadas
- Integração (Testcontainers + Postgres): `POST /api/v1/site/leads`, `GET /api/public/highlights`, `GET /api/public/news`

## Ordem de implementação recomendada

1. criar projeto Spring Boot em `site/backend`
2. configurar PostgreSQL e Flyway (`site/infra`)
3. criar módulo `lead` completo (domain/dto/mapper/repository/service/controller + testes)
4. criar módulo `highlights`
5. criar módulo `news`
6. adicionar Actuator
7. testes de integração de todos os endpoints
8. gate de cobertura ≥ 80% no CI

## Definição de pronto da primeira versão

- sobe localmente com PostgreSQL via docker-compose
- executa migrations automaticamente
- persiste lead com validação e distingue os 4 tipos de interesse
- expõe highlights e news públicos
- responde healthcheck
- possui testes unitários e de integração com cobertura ≥ 80%
