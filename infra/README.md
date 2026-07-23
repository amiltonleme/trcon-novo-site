# TRCon Site — Infra

Infraestrutura e orquestração do site TRCon: ambiente local (Docker Compose),
pipelines de CI/CD e configuração de deploy. A produção oficial é
Cloudflare + Hetzner + Coolify + Neon, conforme `../doc/12-DEPLOY.md`.

## Status

- **Fase 1 (backend mínimo):** concluída — ver `../backend`.
- **Fase 2 (infra local + CI):** concluída. `docker-compose.yml` sobe backend +
  PostgreSQL e foi validado ponta a ponta (health `UP`, migrations aplicadas,
  `POST /api/v1/site/leads` persistindo, `GET /api/public/highlights` e
  `/news` respondendo, 409 em duplicado e 400 em payload inválido).
- **Fase 8 (deploy real):** documentação atualizada para Hetzner + Coolify +
  Neon; provisionamento externo pendente.

## Ambiente local (Docker Compose)

Pré-requisito: Docker Desktop rodando.

```bash
cd infra
cp .env.example .env        # ajuste se quiser (portas, senha, CORS)
docker compose up -d        # sobe postgres + backend
```

- Backend: http://localhost:8080 (health: `/actuator/health`)
- Postgres: localhost:5432 (db `trcon_site`, user/senha `trcon`/`trcon` por padrão)

Se a porta 8080 já estiver ocupada na sua máquina, suba em outra porta sem editar
o arquivo:

```bash
BACKEND_PORT=8081 docker compose up -d
```

Parar e limpar (inclui o volume do banco):

```bash
docker compose down -v
```

Arquivos:

- `docker-compose.yml` — backend + PostgreSQL para desenvolvimento local
- `.env.example` — modelo de variáveis (copie para `.env`, que é ignorado pelo git)
- `../backend/Dockerfile` — build multi-stage (Maven → JRE 21 slim, usuário não-root, healthcheck via Actuator)

## CI/CD

- Pipeline de CI: `../.github/workflows/backend-ci.yml` — em push/PR que toquem
  `backend/**`, roda `./mvnw clean verify` (build + testes unitários e de
  integração com Testcontainers + gate de cobertura ≥ 80% via JaCoCo) e publica
  o relatório de cobertura como artefato. Ver critério em `../doc/10-TESTES-QUALIDADE.md`.
  > O workflow assume `trcongroup/site` como raiz do repositório git (é onde a
  > pasta `.github/` vive). Se o repositório for inicializado em outro nível,
  > mover `.github/` para a raiz e ajustar os `paths:` do workflow.
- Pipeline de CD (build/deploy de produção): deve ser configurado no Coolify por
  Git/webhook após CI verde. O workflow antigo de Render/Fly é legado.
- Pipeline de conteúdo recorrente (2x/dia — herdado do workflow atual em
  `fluxo-caixa-app/site-trcon/.github/workflows/update-site-data.yml`): pendente
  (entra na fase de migração do frontend).

Estimativa de custo de operação: `../doc/09-PLANO-EXECUCAO-IA.md` (seção
"Custos estimados").

## Nota de ambiente local (Windows + Docker Desktop 4.8x/Engine 29.x)

Os testes de integração de `backend` usam Testcontainers (PostgreSQL). Com
Docker Desktop recente (Engine 29.x) em Windows, o Testcontainers pode falhar
com `Could not find a valid Docker environment` mesmo com o Docker rodando,
porque a versão de API mínima negociada por padrão pelo Testcontainers (1.32)
é menor que o mínimo exigido pelo Engine 29 (1.44).

Fix local (uma vez por máquina de desenvolvimento):

1. Habilitar em Docker Desktop → Settings → General →
   "Expose daemon on tcp://localhost:2375 without TLS" (reiniciar o Docker Desktop).
2. Criar `~/.testcontainers.properties` com `docker.host=tcp://localhost:2375`.
3. Criar `~/.docker-java.properties` com `api.version=1.44`.

Isso é um ajuste de máquina local, não do projeto — não deve ser commitado.
Ambientes de CI em Linux normalmente não precisam disso.
