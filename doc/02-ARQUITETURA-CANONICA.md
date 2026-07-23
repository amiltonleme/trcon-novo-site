# Arquitetura Canônica — TRCon Site (monorepo trcongroup/site)

## Objetivo

Definir a arquitetura oficial do site institucional/plataforma TRCon garantindo:

- atualizações automáticas de conteúdo
- baixo custo operacional e tecnológico
- preservação da identidade visual
- arquitetura MVC no backend, aderente a SOLID
- cobertura de testes ≥ 80% (ver [10-TESTES-QUALIDADE.md](./10-TESTES-QUALIDADE.md))

## Localização física (monorepo)

```text
trcongroup/
  site/
    frontend/   Camada 1 — apresentação (estático)
    backend/    Camada 2 — backend próprio (Java/Spring Boot, MVC)
    infra/      Camada 5 — orquestração/deploy/CI-CD
    doc/        Documentação canônica (este diretório)
```

`plataforma-agendamento-inteligente` é um produto separado e não faz parte deste
escopo — nenhum documento ou artefato aqui deve referenciá-lo ou alterá-lo.

## Decisão principal

O site evolui para uma arquitetura **híbrida**, com:

- frontend estático (`site/frontend`) para experiência pública e conteúdo editorial
- backend próprio (`site/backend`) para dados persistidos e capacidades transacionais, em **MVC** (ver [05-BACKEND-ARQUITETURA-MVC.md](./05-BACKEND-ARQUITETURA-MVC.md))
- conteúdo dinâmico gerado offline (scripts) sempre que isso reduzir custo e complexidade

## Regra canônica de fronteira

- frontend estático → apresentação e blocos editoriais
- geração offline (scripts) → conteúdo recorrente e público (radar de IA, tecnologia, mercado)
- backend próprio → persistência, integridade, histórico, autenticação, regras transacionais, **leads comerciais** (produto, dev sob demanda, customização, alocação de mão de obra)

## Modelo alvo (camadas)

### Camada 1 — Apresentação (`site/frontend`)

- renderização da interface institucional (posicionamento em [01-POSICIONAMENTO-INSTITUCIONAL.md](./01-POSICIONAMENTO-INSTITUCIONAL.md))
- leitura de JSON publicado em `data/`
- consumo de API pública do backend quando aplicável
- animações e interatividade no navegador

### Camada 2 — Backend próprio (`site/backend`)

Responsável por:

- persistência em banco de dados (PostgreSQL)
- captação estruturada de leads comerciais (produto, dev sob demanda, customização, staffing)
- autenticação e autorização, se existirem áreas privadas
- histórico de eventos e novidades
- APIs internas do ecossistema TRCon Site

Arquitetura obrigatória: MVC modular monolith — controller / service / repository / mapper / VO / DTO / domain / shared. Detalhes completos em [05-BACKEND-ARQUITETURA-MVC.md](./05-BACKEND-ARQUITETURA-MVC.md).

### Camada 3 — Conteúdo gerado (`site/frontend/data`)

- `market.json`, `economy-tips.json`, `recipes.json` (existentes)
- `ai-radar.json`, `tech-radar.json`, `news-log.json`, `home-highlights.json` (planejados)

### Camada 4 — Geração automática (`site/frontend/scripts` ou `site/infra/pipeline`)

- scripts pequenos e especializados por fonte/domínio (SRP)

### Camada 5 — Orquestração e produção

- Cloudflare na borda: DNS, SSL, CDN, cache e segurança
- Hetzner como compute de produção para frontend, backend/APIs e aplicações TRCon
- Coolify no Hetzner como orquestrador de deploy, proxy e serviços
- Neon PostgreSQL como banco gerenciado externo
- Redis, RabbitMQ e Workers IA como serviços privados no Coolify quando houver demanda concreta
- GitHub Actions para pipeline de conteúdo (2x/dia) e validação de CI
- docker-compose para ambiente local (backend + Postgres)

## Quando usar backend

Obrigatório quando houver: banco de dados, dados de usuário/lead, histórico/auditoria, autenticação, painel administrativo, integrações privadas, regra de negócio que não pertence ao navegador.

## Quando não usar backend

Não introduzir backend para: texto editorial estático, cards públicos pré-gerados, homepage pública simples.

## Princípios SOLID aplicados (nível arquitetura)

- **S**: cada módulo do backend (lead, highlights, news) e cada script de pipeline tem uma única responsabilidade
- **O**: novas fontes de conteúdo ou novos tipos de lead entram por extensão (novo provider/novo enum), sem reescrever o núcleo
- **L**: providers/adapters equivalentes são substituíveis sem quebrar consumidores
- **I**: contratos pequenos — frontend não conhece detalhe interno do backend; providers não conhecem renderização
- **D**: serviços dependem de abstrações internas (`ContentProvider`, `LeadRepository` como interface), não de detalhes concretos espalhados

Detalhamento por camada de código em [05-BACKEND-ARQUITETURA-MVC.md](./05-BACKEND-ARQUITETURA-MVC.md).

## Regra de atualização automática

- pipeline de conteúdo: 2 execuções diárias (08:00 e 20:00 UTC)
- deploy de produção: via Coolify/Git webhook após pipeline verde com gate de cobertura ≥ 80%

## Observabilidade mínima

- pipeline: `generated_at`, `source_note`, `errors`, `items` em cada JSON
- backend: Spring Boot Actuator (`/actuator/health`), logs estruturados

## Política de falha

- o site nunca quebra por ausência de fonte externa ou por indisponibilidade do backend (fallback para JSON estático — ver [07-MIGRACAO-PARALELA.md](./07-MIGRACAO-PARALELA.md))

## Segurança e governança

- nenhuma chave/segredo no frontend
- segredos de IA em lote e credenciais de banco apenas em CI/ambiente do backend
- nenhum dado sensível de usuário no pipeline de conteúdo editorial

## Decisão oficial

- frontend público leve (`site/frontend`)
- backend próprio MVC para persistência e regras transacionais (`site/backend`)
- conteúdo editorial gerado offline
- produção em Cloudflare + Hetzner + Coolify + Neon, conforme [12-DEPLOY.md](./12-DEPLOY.md)
- infraestrutura local e pipelines centralizados em `site/infra`
- cobertura de testes ≥ 80% como critério de aceite de qualquer entrega de backend
- evolução incremental, guiada por SOLID e pelo [09-PLANO-EXECUCAO-IA.md](./09-PLANO-EXECUCAO-IA.md)
