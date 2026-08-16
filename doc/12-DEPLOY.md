# Deploy em Produção — Hetzner + Coolify + Neon + Cloudflare

Runbook canônico de produção do ecossistema TRCon.

> **Status jul/2026:** backend site **no ar** (`api-site.*`). Frontend: redeploy + DNS `@`/`www` pendente.  
> Matriz e pendências: [`14-STATUS-IMPLEMENTACAO.md`](./14-STATUS-IMPLEMENTACAO.md), [`16-PASSO-A-PASSO.md`](./16-PASSO-A-PASSO.md).

Decisão oficial:

- **Borda:** Cloudflare para DNS, SSL, CDN, cache e segurança.
- **Compute:** Hetzner VPS/dedicado.
- **Orquestração:** Coolify rodando no Hetzner.
- **Banco:** Neon PostgreSQL gerenciado, fora do Hetzner.
- **Aplicações no Hetzner:** frontend, backend/APIs, Hub Financeiro, Agendador, Redis, RabbitMQ e Workers IA.

## Desenho esperado

```text
Internet
  |
  v
Cloudflare
  |-- DNS
  |-- SSL/TLS
  |-- CDN
  |-- Cache
  |-- WAF / regras de segurança
  |
  v
Hetzner
  |
  v
Coolify
  |
  |-- Site TRCON frontend
  |-- Site TRCON backend / APIs
  |-- Hub Financeiro
  |-- Agendador
  |-- APIs internas
  |-- Redis
  |-- RabbitMQ
  |-- Workers IA
  |
  v
Neon PostgreSQL
```

Cloudflare fica apenas na borda. O tráfego HTTP público chega no Hetzner pelo proxy do Coolify. O Neon é banco gerenciado externo e deve ser acessado pelas aplicações via TLS.

## Topologia de domínios

| Serviço | Domínio em produção (jul/2026) | Alvo futuro | Origem Coolify |
|---|---|---|---|
| Site TRCON | `trcongroup.com.br` / `www.trcongroup.com.br` | idem | frontend estático |
| **API do site** | **`api-site.trcongroup.com.br`** | `api.trcongroup.com.br` *(após financeiro migrar)* | backend Spring Boot |
| API financeiro (legado) | `api.trcongroup.com.br` | `hub.trcongroup.com.br` | Render *(CNAME — não usar para site)* |
| Hub Financeiro | `hub.trcongroup.com.br` | idem | app Hub Financeiro |
| Agendador | `agenda.trcongroup.com.br` | idem | app Agendador |
| Coolify | `coolify.trcongroup.com.br` | idem | painel administrativo |

**Motivo de `api-site`:** `api.trcongroup.com.br` já serve o backend do **Sirius Financeiro** no Render. Detalhes: [`sirius-marketing/projeto/docs/cursor/10_estrategia_infra_ecossistema.md`](../../sirius-marketing/projeto/docs/cursor/10_estrategia_infra_ecossistema.md).

Regras:

- `coolify.trcongroup.com.br` deve ter proteção forte: senha robusta, 2FA quando disponível, IP allowlist se fizer sentido.
- Redis, RabbitMQ e banco não devem ter exposição pública.
- APIs internas só devem ganhar domínio público se houver necessidade real.

## Artefatos do repositório

| Artefato | Papel atual |
|---|---|
| `backend/Dockerfile` | Imagem de produção do backend Spring Boot. Usar no Coolify. |
| `frontend/` | Site estático + nginx; proxy `/novidades/` → backend (`SITE_API_UPSTREAM`) |
| `frontend/assets/env.js` | URLs públicas da API — produção: `https://api-site.trcongroup.com.br/api/...` |
| `frontend/robots.txt` | Política de crawlers do site institucional |
| `frontend/_headers` | Headers herdados do fluxo Cloudflare Pages. Pode servir como referência para configurar headers no proxy/Coolify/Cloudflare. |
| `backend/src/main/resources/application.yml` | Profile `prod`, leitura de `PORT`, `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` e CORS. |
| `infra/docker-compose.yml` | Ambiente local, não produção. |
| `infra/render.yaml` / `infra/fly.toml` | Legado de estratégia anterior. Não são a rota oficial de produção. |

## Passo 1 — Hetzner

1. Criar um servidor Hetzner para produção inicial.
2. Usar Ubuntu LTS.
3. Habilitar firewall no painel Hetzner e no servidor.
4. Liberar somente:
   - `22/tcp` para SSH, idealmente restrito por IP.
   - `80/tcp` e `443/tcp` para tráfego HTTP/HTTPS.
5. Criar usuário administrativo sem login por senha.
6. Configurar SSH por chave.
7. Atualizar o sistema antes de instalar Coolify.

Tamanho inicial recomendado:

| Uso | Perfil mínimo |
|---|---|
| Site + API + Redis/RabbitMQ pequenos + workers leves | 2 vCPU / 4 GB RAM |
| Hub + Agendador + workers IA com folga | 4 vCPU / 8 GB RAM |

Começar pequeno é aceitável, desde que haja margem para aumentar o servidor ou separar workers depois.

## Passo 2 — Coolify

1. Instalar Coolify no servidor Hetzner.
2. Acessar o painel inicial.
3. Configurar o domínio administrativo: `coolify.trcongroup.com.br`.
4. Configurar o proxy padrão do Coolify.
5. Conectar o repositório Git.
6. Criar um projeto/ambiente de produção, por exemplo:
   - projeto: `trcon`
   - ambiente: `production`

No Coolify, cada aplicação deve ser um recurso separado. Evite colocar tudo em um único container.

## Passo 3 — Neon PostgreSQL

1. Criar o projeto no Neon.
2. Criar o database do site, por exemplo `trcon_site`.
3. Copiar a connection string.
4. Para o backend Java/JDBC, usar URL JDBC direta com SSL:

```text
jdbc:postgresql://ep-xxxx.us-east-1.aws.neon.tech/trcon_site?sslmode=require
```

Atenções:

- Não usar hostname `-pooler` para o backend Spring/JPA nesta fase. O backend já usa HikariCP e o pooler transacional pode conflitar com prepared statements do Hibernate.
- Remover `channel_binding=require` da URL JDBC, pois é parâmetro de clientes libpq e não do driver JDBC.
- Manter `sslmode=require`.

Variáveis para o Coolify:

| Variável | Valor |
|---|---|
| `DB_URL` | JDBC do Neon com `?sslmode=require` |
| `DB_USERNAME` | usuário do Neon |
| `DB_PASSWORD` | senha do Neon |

As migrations Flyway (`V1..V3`) rodam automaticamente na primeira subida do backend.

## Passo 4 — Backend/API no Coolify

Repositório GitHub: **`amiltonleme/trcon-novo-site`**. Monorepo: `backend/` e `frontend/` na raiz.

**Coolify:** Project `site-trcon` → **+ New** → Git (Deploy Key ou GitHub App).

### Wizard — Create Application

| Campo | Valor |
|---|---|
| Repository | `trcon-novo-site` |
| Branch | `main` |
| Build Pack | **Dockerfile** |
| Base Directory | **`backend`** |
| Port | **`8080`** |
| Is it a static site? | **não** |

### Configuration → General

| Campo | Valor |
|---|---|
| Name | `site-trcon-backend` |
| Domains | **`https://api-site.trcongroup.com.br`** |
| Dockerfile location | `Dockerfile` |
| Ports Exposes | `8080` |

### Healthcheck

| Campo | Valor |
|---|---|
| Path | `/actuator/health` |
| Port | `8080` |
| Host | `localhost` *(check interno — não é URL pública)* |
| Start Period | **90** s |

Variáveis obrigatórias:

| Variável | Valor |
|---|---|
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `PORT` | `8080` |
| `DB_URL` | JDBC Neon database **`trcon_site`** |
| `DB_USERNAME` | usuário Neon |
| `DB_PASSWORD` | senha Neon |
| `TRCON_SITE_INTERNAL_API_KEY` | `openssl rand -hex 32` — **guardar** para Sirius Marketing |
| `TRCON_CORS_ALLOWED_ORIGINS` | `https://trcongroup.com.br,https://www.trcongroup.com.br` |
| `TRCON_SITE_MAIL_ENABLED` | `true` (liga notificação de lead via Resend) |
| `TRCON_SITE_MAIL_API_KEY` | API key Resend (`re_...`) — **nunca** no Git |
| `TRCON_SITE_MAIL_FROM` | Remetente verificado no Resend, ex. `Site TRCon <noreply@trcongroup.com.br>` |
| `TRCON_SITE_LEAD_NOTIFY_TO` | Destinatário comercial (default `amilton.leme@trcongroup.com.br`) |

Smoke test:

```text
GET https://api-site.trcongroup.com.br/actuator/health
GET https://api-site.trcongroup.com.br/api/public/news
GET https://api-site.trcongroup.com.br/api/public/highlights
GET https://api-site.trcongroup.com.br/api/public/economy-tips
```

Após redeploy com Flyway **V6 + V7**, validar `economy-tips` e `GET /api/public/news/{slug}` (200). Lista de economy-tips pode estar vazia até marketing publicar.

Com mail ligado, smoke: `POST /api/v1/site/leads` → 201 e e-mail em `TRCON_SITE_LEAD_NOTIFY_TO` (falha de Resend não deve quebrar o 201).

A raiz `/` pode retornar JSON `INTERNAL_ERROR` — não há rota na raiz; isso **não** indica falha de deploy.

Resposta esperada no health:

```json
{"status":"UP"}
```

## Passo 5 — Frontend no Coolify

**+ New** no **mesmo repositório** — Base Directory **`frontend`**.

| Campo | Valor |
|---|---|
| Build Pack | **Dockerfile** |
| Base Directory | `frontend` |
| Dockerfile location | `Dockerfile` |
| Port | `80` |
| Domains | `https://trcongroup.com.br`, `https://www.trcongroup.com.br` |
| Env | **`SITE_API_UPSTREAM`** = URL interna/HTTP do backend (ex. `http://trcon-site-backend:8080` ou host Coolify da API). Sem isso, `/novidades/` cai no fallback CSR. |

O `Dockerfile` gera `nginx.conf` via `envsubst` (`nginx.conf.template` + `docker-entrypoint.sh`). Proxy:

- `/novidades/*` → `$SITE_API_UPSTREAM` (HTML SSR com SEO)
- falha 502/503/504 → fallback `novidades.html` (CSR)

### Versão e hash no rodapé

Gravados **direto no `index.html`** (HTML estático — sem JS, sem env do Coolify).

- **`npm run build`** (local): `node scripts/stamp-build-info.mjs` — lê `package.json` + git
- **CI**: workflow `.github/workflows/stamp-site-version.yml` roda a cada push em `frontend/**`, commita o rodapé com `github.sha` e `[skip ci]`

Coolify só publica os arquivos; não precisa build command. **Obrigatório em 0.8.0+:** variável `SITE_API_UPSTREAM` apontando ao backend do site.

**Healthcheck** (opcional):

| Campo | Valor |
|---|---|
| Path | `/` |
| Port | `80` |
| Start Period | `30` s |

Antes do deploy, `frontend/assets/env.js`:

```js
window.TRCON_LEADS_API_URL        = 'https://api-site.trcongroup.com.br/api/v1/site/leads';
window.TRCON_HIGHLIGHTS_API_URL   = 'https://api-site.trcongroup.com.br/api/public/highlights';
window.TRCON_NEWS_API_URL         = 'https://api-site.trcongroup.com.br/api/public/news';
window.TRCON_ECONOMY_TIPS_API_URL = 'https://api-site.trcongroup.com.br/api/public/economy-tips';
```

Nenhum segredo no frontend.

## Passo 6 — Redis, RabbitMQ e Workers IA

Redis e RabbitMQ devem rodar como serviços privados no Coolify.

| Serviço | Exposição | Uso |
|---|---|---|
| Redis | rede interna do Coolify | cache, filas leves, locks ou sessões se necessário |
| RabbitMQ | rede interna do Coolify | mensageria entre APIs, Agendador, Hub e Workers IA |
| Workers IA | sem domínio público por padrão | processamento assíncrono, curadoria, automações |

Regras:

- Não publicar portas de Redis/RabbitMQ na internet.
- Definir senhas fortes nos serviços.
- Usar variáveis de ambiente nas aplicações consumidoras.
- Separar filas por domínio quando houver múltiplos produtos: `site.*`, `hub.*`, `agenda.*`, `ia.*`.

O backend atual do site ainda não depende de Redis/RabbitMQ. Eles entram como infraestrutura compartilhada para a evolução do ecossistema.

## Passo 7 — Cloudflare

No Cloudflare, configurar DNS para apontar os domínios públicos para o Hetzner.

Registros Coolify / Hetzner (**tipo `A`** para IP fixo):

| Tipo | Nome | Valor | Notas |
|---|---|---|---|
| `A` | `api-site` | IP Hetzner | **API site** |
| `A` | `@` | IP Hetzner | Site *(substituir CNAME Pages)* |
| `A` | `www` | IP Hetzner | Site |
| `A` | `coolify` | IP Hetzner | Painel |
| `A` | `marketing` | IP Hetzner | Marketing |
| `A` | `hub` | IP Hetzner | Futuro financeiro |
| `A` | `agenda` | IP Hetzner | Futuro agendador |

**Não alterar** o CNAME `api` → Render (financeiro) até a migração da Fase 3.

**A vs CNAME:** Hetzner = **`A`** para o IP. Render/Pages = **`CNAME`** para hostname do SaaS.

SSL/TLS:

- Modo recomendado: **Full (strict)**.
- Certificados na origem devem ser válidos. Pode usar certificado gerenciado pelo Coolify/Let's Encrypt ou certificado de origem Cloudflare.

Cache:

- Cachear assets estáticos do frontend.
- Não cachear `/api/*`.
- Não cachear endpoints administrativos.

Segurança:

- Ativar WAF/regras gerenciadas compatíveis com o plano usado.
- Criar rate limit para endpoints sensíveis, especialmente formulário de lead.
- Proteger `coolify.trcongroup.com.br`.

## Checklist de produção

- [ ] Hetzner criado, atualizado e com firewall ativo.
- [ ] Coolify instalado e acessível por domínio administrativo.
- [ ] Cloudflare apontando DNS para o IP do Hetzner.
- [ ] SSL em modo Full (strict).
- [ ] Neon criado com `sslmode=require`.
- [ ] Backend publicado no Coolify (`api-site.trcongroup.com.br`).
- [ ] Frontend publicado no Coolify.
- [ ] `env.js` apontando para `https://api-site.trcongroup.com.br/api/...`.
- [ ] CORS do backend liberando somente os domínios finais.
- [ ] Redis e RabbitMQ privados, sem portas públicas.
- [ ] Workers IA sem exposição pública por padrão.
- [ ] Backups/snapshots definidos para servidor e banco.

## Smoke test pós-deploy

1. `GET https://api-site.trcongroup.com.br/actuator/health` retorna `{"status":"UP"}`.
2. `GET https://api-site.trcongroup.com.br/api/public/highlights` retorna 200.
3. `GET https://api-site.trcongroup.com.br/api/public/news` retorna 200.
4. `GET https://api-site.trcongroup.com.br/api/public/economy-tips` retorna 200.
5. Site abre em `https://trcongroup.com.br`.
6. Home: seções Radar, Novidades e **Educação Financeira** (merge API + JSON RSS).
7. Formulário de contato envia lead e recebe 201.
8. Reenvio do mesmo lead retorna 409.
9. Se a API ficar indisponível, a home continua abrindo com JSON estático.
10. Cloudflare não cacheia respostas de `/api/*`.
11. *(Integração)* Aprovar `LANDING_PAGE` no marketing → item aparece em economy-tips.

## CI/CD

Fluxo alvo:

- Push/merge em `main`.
- GitHub Actions roda CI e testes.
- Coolify detecta atualização do repositório ou recebe webhook.
- Coolify rebuilda e publica os recursos afetados.

O workflow `.github/workflows/backend-cd.yml` apenas registra que a produção oficial é Coolify e mantém o caminho Fly.io como legado quando `DEPLOY_TARGET=fly`. Para a rota Hetzner/Coolify, o deploy deve ser configurado no Coolify por Git/webhook. Não usar Render/Fly como produção oficial.

## Rollback

- **Frontend/backend:** usar rollback de deployment no Coolify ou redeployar commit anterior.
- **Banco:** nunca editar migration Flyway já aplicada. Criar sempre `V{n+1}`.
- **Servidor:** manter snapshots do Hetzner antes de mudanças grandes.
- **Cloudflare:** alterações de DNS/cache/WAF devem ser pequenas e reversíveis.

## Backups

| Item | Estratégia mínima |
|---|---|
| Neon | backups/PITR conforme plano contratado |
| Hetzner | snapshot antes de releases estruturais e backup recorrente |
| Coolify | backup das configurações e volumes relevantes |
| Redis/RabbitMQ | persistência conforme criticidade; mensagens temporárias podem não exigir backup |

## Custos

| Item | Faixa inicial |
|---|---|
| Hetzner VPS | ~R$ 30–120/mês, conforme tamanho |
| Neon | R$ 0 no início ou plano pago conforme uso |
| Cloudflare | R$ 0 no plano inicial, pago se precisar de recursos avançados |
| Domínio | ~R$ 40–60/ano |
| GitHub Actions | R$ 0 dentro do uso comum do projeto |

## Limite honesto desta fase

A documentação define a rota oficial, mas ainda há artefatos legados de Render/Fly no repositório. Para fechar a migração operacional, criar os artefatos específicos de Coolify quando o servidor e os domínios finais forem provisionados.

## Sirius Marketing (ecossistema)

App editorial separado — monorepo `sirius-marketing/projeto`.

| Componente | Coolify | Domínio sugerido |
|---|---|---|
| Frontend Angular | `frontend/Dockerfile` (nginx + proxy `/api`) | `marketing.trcongroup.com.br` |
| Backend Spring Boot | `backend/Dockerfile` | rede interna ou `api-marketing.trcongroup.com.br` |
| PostgreSQL | Neon — database **`sirius_marketing`** | — |

Runbook dedicado: `sirius-marketing/projeto/docs/cursor/09_deploy_producao.md`.

Integração: após approve, o marketing chama `POST /api/internal/news` e `POST /api/internal/highlights` neste site backend (`APP_SITE_API_URL` + API key compartilhada).
