# Deploy em Produção — TRCon Site (Fase 8)

Este documento é o runbook de deploy. Os artefatos já existem no repositório; o
que falta são ações que **exigem provisionamento humano** (criar conta na cloud,
domínio, definir segredos). A IA preparou tudo; você executa os passos abaixo.

## O que já está pronto

| Artefato | Onde | Papel |
|---|---|---|
| `backend/Dockerfile` | backend | Imagem de produção (multi-stage, JRE 21, non-root, healthcheck) |
| `infra/render.yaml` | infra | Blueprint Render: backend + Postgres gerenciado + frontend estático |
| `infra/fly.toml` | infra | Alternativa Fly.io para o backend |
| `.github/workflows/backend-ci.yml` | infra/CI | Build+testes+gate 80% em cada PR |
| `.github/workflows/backend-cd.yml` | infra/CD | Deploy (Render automático / Fly por token) |
| `.github/workflows/update-content.yml` | infra | Pipeline de conteúdo 2x/dia |
| `frontend/assets/env.js` | frontend | Injeção das URLs de API em produção |
| `application.yml` (profile `prod`) | backend | Lê `PORT` e compõe a URL do banco por partes |

## Pré-requisitos (ações humanas)

1. Repositório Git hospedado (GitHub) com `trcongroup/site` como raiz — ou ajustar
   os `paths:` dos workflows se a raiz for outra.
2. Conta no provedor escolhido (recomendado: **Render**, pelo Blueprint único).
3. (Opcional) Domínio próprio (ex.: `trcongroup.com.br`) e acesso ao DNS.

## Caminho recomendado — Render (Blueprint)

1. No Render: **New → Blueprint** e selecione o repositório. Ele lê `infra/render.yaml`.
2. Render cria automaticamente: o **Postgres** (`trcon-site-db`), o **backend**
   (Docker) e o **frontend** (estático).
3. Defina o único segredo marcado como `sync: false`:
   - `TRCON_CORS_ALLOWED_ORIGINS` = a URL pública do frontend
     (ex.: `https://trcon-site-frontend.onrender.com` e/ou `https://trcongroup.com.br`).
4. Primeiro deploy: o Flyway roda as migrations (`V1..V3`) no banco novo
   automaticamente na subida do backend.
5. Aponte o frontend para a API editando `frontend/assets/env.js` com a URL
   pública do backend e faça commit:
   ```js
   window.TRCON_LEADS_API_URL      = 'https://trcon-site-backend.onrender.com/api/v1/site/leads';
   window.TRCON_HIGHLIGHTS_API_URL = 'https://trcon-site-backend.onrender.com/api/public/highlights';
   window.TRCON_NEWS_API_URL       = 'https://trcon-site-backend.onrender.com/api/public/news';
   ```
6. (Opcional) Domínio próprio: adicione o custom domain no serviço de frontend do
   Render e configure o CNAME no seu DNS. Atualize `TRCON_CORS_ALLOWED_ORIGINS`.

## Caminho alternativo — Fly.io (backend) + host estático (frontend)

1. Backend: a partir de `site/backend`, `fly launch --copy-config` usando
   `../infra/fly.toml`, depois `fly deploy`.
2. Banco: `fly postgres create` e use os dados para setar os segredos
   (o attach fornece `postgres://`, não JDBC — ver comentário no `fly.toml`):
   ```
   fly secrets set DB_HOST=... DB_PORT=5432 DB_NAME=... DB_USERNAME=... DB_PASSWORD=...
   fly secrets set TRCON_CORS_ALLOWED_ORIGINS=https://trcongroup.com.br
   ```
3. CD por token: defina o secret `FLY_API_TOKEN` e a variável de repositório
   `DEPLOY_TARGET=fly`. Aí o `backend-cd.yml` faz deploy a cada push em `main`.
4. Frontend: publique `frontend/` em GitHub Pages / Netlify / Cloudflare Pages
   (estático puro). Ajuste `env.js` como no passo Render.

## Checklist de variáveis de ambiente (backend, produção)

| Variável | Origem | Obrigatória |
|---|---|---|
| `SPRING_PROFILES_ACTIVE=prod` | fixa | sim |
| `PORT` | injetada pelo provedor | sim (auto) |
| `DB_HOST` / `DB_PORT` / `DB_NAME` | banco gerenciado | sim |
| `DB_USERNAME` / `DB_PASSWORD` | banco gerenciado | sim |
| `TRCON_CORS_ALLOWED_ORIGINS` | você define (URL do frontend) | sim |

Nenhum segredo vai para o frontend (`env.js` só tem URLs públicas).

## Verificação pós-deploy (smoke test)

1. `GET https://<backend>/actuator/health` → `{"status":"UP"}`.
2. `GET https://<backend>/api/public/highlights` → 200 com envelope JSON.
3. No site, enviar o formulário de contato → 201; reenviar igual → 409.
4. Abrir a home: seções Radar e Novidades carregam (selo "Fonte: API TRCon"
   quando `env.js` aponta para o backend; senão "conteúdo publicado").
5. Simular backend fora do ar (parar o serviço) → a home continua abrindo com o
   JSON estático (degradação prevista em `07-MIGRACAO-PARALELA.md`).

## Rollback

- **Render/Fly**: reverter para o deploy anterior pelo painel/`fly releases`.
- **Frontend → só JSON**: comentar as linhas de `env.js` e publicar; o site volta
  a consumir apenas o conteúdo estático, sem depender do backend.
- **Banco**: não editar migrations já aplicadas — criar sempre `V{n+1}` nova.

## Custos (resumo)

Faixa inicial estimada: **R$ 35 a R$ 200/mês**, começando perto de R$ 0 usando os
tiers free/hobby (Render free por 90 dias no Postgres; frontend estático
gratuito; CI dentro do free tier do GitHub Actions). Detalhe em
[09-PLANO-EXECUCAO-IA.md](./09-PLANO-EXECUCAO-IA.md) (seção "Custos estimados").

## Limite honesto desta fase

A IA preparou 100% dos artefatos e a configuração, e validou o backend + frontend
em container localmente (Fases 1–7). O que **só você pode fazer**: criar as contas
na cloud, aprovar o custo, registrar/apontar o domínio e definir os segredos de
produção. Depois disso, os workflows assumem o ciclo (CI no PR, CD no merge,
conteúdo 2x/dia).
