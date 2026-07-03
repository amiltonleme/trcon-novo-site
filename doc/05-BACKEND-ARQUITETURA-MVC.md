# Arquitetura MVC do Backend — TRCon Site

## Objetivo

Detalhar, sem ambiguidade, as camadas obrigatórias do backend em `site/backend`,
suas responsabilidades, o que cada uma **não pode** fazer, e como isso mapeia para
SOLID e para o critério de cobertura de testes ≥ 80% ([10-TESTES-QUALIDADE.md](./10-TESTES-QUALIDADE.md)).

## Estrutura de pastas por módulo

```text
backend/
  pom.xml
  src/
    main/
      java/br/com/trcon/site/
        SiteBackendApplication.java
        shared/
          config/           # beans, CORS, Actuator, OpenAPI
          exception/         # ApiException, GlobalExceptionHandler, ErrorResponse
          vo/                # Value Objects transversais (Email, Telefone, Cpf...)
          util/
        lead/
          controller/        # LeadController
          service/           # LeadService (interface) + LeadServiceImpl
          repository/        # LeadRepository (Spring Data JPA)
          mapper/            # LeadMapper (MapStruct): domain <-> dto/vo
          domain/            # Lead (entidade JPA), LeadType, LeadStatus (enums)
          dto/
            request/          # LeadCreateRequest
            response/         # LeadCreateResponse
        highlights/
          controller/ service/ repository/ mapper/ domain/ dto/
        news/
          controller/ service/ repository/ mapper/ domain/ dto/
      resources/
        application.yml
        db/migration/         # Flyway V1__..., V2__...
    test/
      java/br/com/trcon/site/
        lead/                 # espelha a estrutura de main para cada módulo
        highlights/
        news/
        shared/
```

Cada módulo de domínio replica sempre o mesmo conjunto de pastas
(`controller/service/repository/mapper/domain/dto`). Um módulo novo (ex.: `content`)
segue o mesmo molde — isso é o que permite scaffolding automatizado
(ver skill `trcon-backend-scaffold` em [11-SKILLS-AGENTS-CLAUDE.md](./11-SKILLS-AGENTS-CLAUDE.md)).

## Responsabilidade de cada camada

### `controller/`
- recebe request HTTP, valida forma (Bean Validation nos DTOs de request)
- chama a `service` correspondente
- traduz o retorno da service em resposta HTTP (status code, corpo)
- **não contém regra de negócio**
- depende de uma interface de `service`, nunca da implementação concreta (DIP)

### `service/`
- interface (`LeadService`) + implementação (`LeadServiceImpl`)
- orquestra caso de uso: validação de regra de negócio, chamada a `repository`, chamada a `mapper`
- **não conhece detalhe HTTP** (não recebe `HttpServletRequest`, não monta `ResponseEntity`)
- depende de abstrações (`LeadRepository` como interface do Spring Data, outras `service`s por interface)

### `repository/`
- interface Spring Data JPA (`LeadRepository extends JpaRepository<Lead, UUID>`)
- pode ter métodos de consulta derivados ou `@Query`
- **não contém regra editorial nem regra de negócio** — só acesso a dado

### `mapper/`
- responsável exclusivo pela tradução `domain ↔ dto` e `domain ↔ vo`
- implementado com MapStruct (interfaces `@Mapper`, geração em build time)
- **não contém lógica de negócio** — apenas transformação estrutural
- é o único lugar que "conhece" os dois lados (domínio e contrato externo)

### `domain/`
- entidades JPA (`@Entity`) e enums de domínio
- pode conter métodos de comportamento do próprio objeto (ex.: `Lead.marcarComoContatado()`), desde que não dependam de infraestrutura
- **não conhece DTO, não conhece HTTP, não conhece banco além da própria anotação JPA**

### `dto/`
- `request/`: contrato de entrada (o que a API aceita)
- `response/`: contrato de saída (o que a API devolve)
- imutáveis sempre que possível (`record` do Java 21)
- **não é reaproveitado como entidade JPA** — DTO nunca é anotado `@Entity`

### `vo/` (Value Object)
- objetos pequenos, imutáveis, validados na construção, que representam um conceito de domínio sem identidade própria (ex.: `Email`, `Telefone`, `Money`)
- vivem em `shared/vo` quando usados por mais de um módulo, ou em `modulo/domain/vo` quando específicos de um domínio
- eliminam validação duplicada espalhada pelo código (ex.: toda validação de e-mail vive dentro do VO `Email`, não repetida em múltiplos services)

### `shared/`
- atende a aplicação inteira: configuração (`config/`), tratamento de erro global (`exception/`), VOs transversais (`vo/`), utilitários puros (`util/`)
- **não pode conter regra de negócio de um módulo específico** — se uma classe em `shared` só é usada por `lead`, ela pertence a `lead`, não a `shared`

## Fluxo de uma requisição (exemplo: criar lead)

```text
HTTP POST /api/v1/site/leads
  -> LeadController.criar(LeadCreateRequest)
    -> LeadService.criar(LeadCreateRequest)
       - valida regra de negócio (ex.: duplicidade por email+origem)
       - usa LeadMapper.toDomain(request) -> Lead
       - usa LeadRepository.save(lead)
       - usa LeadMapper.toResponse(leadSalvo) -> LeadCreateResponse
    <- LeadCreateResponse
  <- 201 Created + LeadCreateResponse
```

Erros de validação/negócio lançam exceções de domínio (`LeadDuplicadoException`),
capturadas pelo `GlobalExceptionHandler` em `shared/exception`, que traduz para o
formato de erro padrão (ver [06-BACKEND-MINIMO-ESPECIFICACAO.md](./06-BACKEND-MINIMO-ESPECIFICACAO.md)).

## SOLID aplicado por camada

### Single Responsibility
- um `service` por caso de uso coeso; um `mapper` só mapeia; um `repository` só acessa dado
- não criar "GodService" cobrindo lead + highlights + news

### Open/Closed
- novos tipos de lead (`LeadType.NOVO_TIPO`) entram como extensão de enum + regra adicional no service, sem reescrever o fluxo principal
- novos providers de conteúdo entram implementando uma interface existente (`ContentProvider`), sem alterar quem já consome a interface

### Liskov Substitution
- qualquer implementação de `LeadService`, `ContentProvider` etc. deve ser substituível por outra sem quebrar o chamador — testado via testes de contrato quando houver mais de uma implementação

### Interface Segregation
- interfaces pequenas e específicas por caso de uso (`LeadService`, não uma `SiteService` genérica cobrindo tudo)
- controller depende só da interface de `service` que efetivamente usa

### Dependency Inversion
- `service` depende da interface `repository` (fornecida pelo Spring Data), nunca de uma implementação JDBC direta
- injeção via construtor (nunca `@Autowired` em campo) — facilita teste unitário com mocks

## Regras de implementação (não negociáveis)

1. Frontend não fala com banco — fala apenas com a API.
2. Controller não contém regra de negócio.
3. Service não depende de detalhe HTTP.
4. Repository não contém regra editorial/negócio.
5. Mapper não contém regra de negócio — só transformação.
6. Toda entrada externa (DTO de request) é validada com Bean Validation antes de chegar ao service.
7. Toda classe pública de `service` e `mapper` tem teste unitário; todo endpoint tem teste de integração (Testcontainers).
8. Nenhum PR de backend é aceito com cobertura de linha/branch abaixo de 80% (ver [10-TESTES-QUALIDADE.md](./10-TESTES-QUALIDADE.md)).

## Convenção de nomes

| Elemento | Convenção | Exemplo |
|---|---|---|
| Controller | `<Dominio>Controller` | `LeadController` |
| Service (interface) | `<Dominio>Service` | `LeadService` |
| Service (impl) | `<Dominio>ServiceImpl` | `LeadServiceImpl` |
| Repository | `<Dominio>Repository` | `LeadRepository` |
| Mapper | `<Dominio>Mapper` | `LeadMapper` |
| Entidade de domínio | `<Dominio>` | `Lead` |
| DTO de entrada | `<Dominio><Acao>Request` | `LeadCreateRequest` |
| DTO de saída | `<Dominio><Acao>Response` | `LeadCreateResponse` |
| Value Object | substantivo do conceito | `Email`, `Telefone`, `Money` |
| Exceção de domínio | `<Motivo>Exception` | `LeadDuplicadoException` |

## Critério de pronto desta especificação

- todo módulo novo segue exatamente esta estrutura de pastas
- nenhuma camada assume responsabilidade de outra
- 100% dos módulos com `service` e `mapper` cobertos por teste unitário
- 100% dos endpoints públicos cobertos por teste de integração
- revisão de arquitetura feita pelo agent `trcon-backend-architect` antes do merge (ver [11-SKILLS-AGENTS-CLAUDE.md](./11-SKILLS-AGENTS-CLAUDE.md))
