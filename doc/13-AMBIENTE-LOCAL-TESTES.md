# Ambiente Local e Testes — TRCon Site

Guia prático para abrir o projeto, subir o ambiente local e validar frontend,
backend e integração ponta a ponta.

## Abrindo no IntelliJ

Abra a pasta raiz do projeto:

```text
C:\Trcongroup\projetos\site
```

Não é necessário abrir duas instâncias do IntelliJ. O ideal é abrir uma única
janela com a raiz `site`, porque ela contém:

- `backend/` — projeto Java/Spring Boot com Maven.
- `frontend/` — site HTML/CSS/JS com Node/Vitest.
- `infra/` — Docker Compose e arquivos de infraestrutura.
- `doc/` — documentação canônica.

No IntelliJ:

1. `File -> Open`.
2. Selecione `C:\Trcongroup\projetos\site`.
3. Confirme abrir como projeto.
4. Aguarde o IntelliJ importar o Maven do `backend/pom.xml`.
5. Configure SDK Java 21 para o projeto/backend.
6. Para o frontend, use o terminal integrado na pasta `frontend`.

Documentação de integração com Sirius Marketing: [`../../sirius-marketing/projeto/docs/cursor/08_passo_a_passo.md`](../../sirius-marketing/projeto/docs/cursor/08_passo_a_passo.md) (Sprint 3).

Só faz sentido abrir `backend/` e `frontend/` separados se você quiser janelas
isoladas por preferência pessoal. Tecnicamente, para este monorepo, uma janela
na raiz é mais simples.

## Pré-requisitos

- Java 21.
- Docker Desktop rodando.
- Node.js LTS.
- IntelliJ IDEA.
- PowerShell ou terminal equivalente.

No Windows, se os testes de integração com Testcontainers falharem por Docker
Desktop recente, veja a nota em [infra/README.md](../infra/README.md).

## Opção recomendada — ambiente completo com Docker Compose

Essa opção sobe backend e PostgreSQL em containers.

```powershell
cd C:\Trcongroup\projetos\site\infra
docker compose up -d --build
```

Serviços:

| Serviço | URL |
|---|---|
| Backend (Docker default) | `http://localhost:8080` |
| Backend (dev local com marketing) | **`http://localhost:8081`** |
| Health | `http://localhost:8081/actuator/health` |
| API Novidades | `http://localhost:8081/api/public/news` |
| API Radar (highlights) | `http://localhost:8081/api/public/highlights` |
| API Chat institucional | `POST http://localhost:8081/api/v1/site/chat` |
| PostgreSQL | `localhost:5434` (se `5432` ocupada) ou `5432` |

Banco local padrão:

| Campo | Valor |
|---|---|
| database | `trcon_site` |
| user | `trcon` |
| password | `trcon` |

Para usar outra porta no backend:

```powershell
cd C:\Trcongroup\projetos\site\infra
$env:BACKEND_PORT="8081"
docker compose up -d --build
```

Parar o ambiente mantendo volume do banco:

```powershell
cd C:\Trcongroup\projetos\site\infra
docker compose down
```

Parar e apagar o volume do banco local:

```powershell
cd C:\Trcongroup\projetos\site\infra
docker compose down -v
```

## Frontend local

Em outro terminal:

```powershell
cd C:\Trcongroup\projetos\site\frontend
npm install
npm run dev
```

O site local sobe em:

```text
http://127.0.0.1:4173
```

`npm run dev` faz proxy de `/novidades/*` para `SITE_API_UPSTREAM` (default `http://127.0.0.1:8081`). Sem backend, cai no template CSR `novidades.html`.

Por padrão, o frontend usa JSON estático quando a API não está configurada.
Para integração local com o backend (e com Sirius Marketing), `frontend/assets/env.js`
já aponta para `:8081`:

```js
window.TRCON_LEADS_API_URL = 'http://localhost:8081/api/v1/site/leads';
window.TRCON_HIGHLIGHTS_API_URL = 'http://localhost:8081/api/public/highlights';
window.TRCON_NEWS_API_URL = 'http://localhost:8081/api/public/news';
window.TRCON_CHAT_API_URL = 'http://localhost:8081/api/v1/site/chat';
```

### Radar TRCONGROUP, Novidades e Chat

| Seção na home | API | Conteúdo | Layout |
|---|---|---|---|
| **Radar TRCONGROUP** | `/api/public/highlights` | Sinais IA/tecnologia (pipeline JSON 2×/dia). API **exclui** artigos marketing (`/novidades/`, `-radar`). Fallback `home-highlights.json`. | `cards-grid` |
| **Novidades TRCONGROUP** | `/api/public/news` | Artigos Sirius Marketing (`news_items`). Link → `/novidades/{slug}`. | `cards-grid` |
| **Chat institucional** | `POST /api/v1/site/chat` | Base factual autorizada; profile `dev` usa stub sem custo | Widget flutuante |

**Não são o mesmo feed.** Artigos aprovados vão **somente** a Novidades (correção 27/07/2026).

Em produção, substitua as URLs em `env.js` pelos domínios reais no deploy.

## Opção alternativa — Postgres no Docker e backend pelo IntelliJ

Use esta opção quando quiser debugar o backend dentro do IntelliJ.

1. Suba apenas o banco local:

```powershell
cd C:\Trcongroup\projetos\site\infra
docker compose up -d postgres
```

2. No IntelliJ, crie uma Run Configuration para
   `br.com.trcon.site.SiteBackendApplication`.

3. Profile Spring (**Active profiles** = `dev`, ou vazio — default é `dev`):

```text
SPRING_PROFILES_ACTIVE=dev
```

Credenciais e porta do Postgres (exemplo quando site usa `5434:5432`):

```text
DB_URL=jdbc:postgresql://localhost:5434/trcon_site;DB_USERNAME=trcon;DB_PASSWORD=trcon;TRCON_CORS_ALLOWED_ORIGINS=http://localhost:4173,http://127.0.0.1:4173
```

4. Rode a aplicação pelo IntelliJ (porta **8081** via `application-dev.yml`).

O profile `dev` usa:

```text
jdbc:postgresql://localhost:5434/trcon_site
DB_USERNAME=trcon
DB_PASSWORD=trcon
server.port=8081
```

Se a porta `5432` estiver ocupada por outro projeto, defina `DB_PORT` ou `DB_URL` na Run Configuration.

## Testes do backend

Rodar todos os testes e gate de cobertura:

```powershell
cd C:\Trcongroup\projetos\site\backend
.\mvnw.cmd clean verify
```

O `verify` executa:

- testes unitários;
- testes de integração;
- Testcontainers com PostgreSQL;
- relatório JaCoCo;
- gate mínimo de 80% para linha e branch.

Relatório local de cobertura:

```text
backend\target\site\jacoco\index.html
```

Rodar só os testes, sem o gate completo:

```powershell
cd C:\Trcongroup\projetos\site\backend
.\mvnw.cmd test
```

## Testes do frontend

```powershell
cd C:\Trcongroup\projetos\site\frontend
npm install
npm test
```

Lint:

```powershell
cd C:\Trcongroup\projetos\site\frontend
npm run lint
```

Watch mode durante desenvolvimento:

```powershell
cd C:\Trcongroup\projetos\site\frontend
npm run test:watch
```

## Testes dos scripts de conteúdo

Os scripts Python do frontend possuem testes com `unittest`.

```powershell
cd C:\Trcongroup\projetos\site\frontend
python -m unittest discover scripts/tests
```

## Smoke test manual

Com backend e frontend rodando:

1. Abrir `http://localhost:8081/actuator/health`.
2. Esperar resposta:

```json
{"status":"UP"}
```

3. Abrir `http://localhost:8081/api/public/highlights` (Radar — sinais pipeline; **não** inclui artigos marketing).
4. Abrir `http://localhost:8081/api/public/news` (Novidades — artigos aprovados).
5. Abrir `http://localhost:8081/api/public/news/{slug}` — artigo completo (S8).
6. Enviar uma pergunta a `POST http://localhost:8081/api/v1/site/chat` e conferir `sourceIds`.
7. Abrir `http://127.0.0.1:4173` — Home tecnológica, Radar, Novidades, Trabalhe Conosco e widget de IA.
8. Enviar o formulário de contato.
8. Esperar HTTP 201 no primeiro envio.
9. Reenviar o mesmo lead e esperar HTTP 409.
10. Parar o backend e confirmar que a home continua abrindo com JSON estático.

## Acessar o banco local

Pelo terminal, usando o container Docker:

```powershell
docker exec -it trcon-site-postgres psql -U trcon -d trcon_site
```

Listar tabelas:

```sql
\dt
```

Consultar os últimos leads:

```sql
select id, nome, email, telefone, tipo_interesse, origem, created_at
from leads
order by created_at desc
limit 10;
```

Apagar um lead de teste por e-mail:

```sql
delete from leads
where email = 'email-de-teste@exemplo.com';
```

Sair do `psql`:

```sql
\q
```

No IntelliJ, também é possível acessar em **Database** criando uma conexão
PostgreSQL com:

| Campo | Valor |
|---|---|
| Host | `localhost` |
| Port | porta publicada no Docker, ex. `5434` |
| Database | `trcon_site` |
| User | `trcon` |
| Password | `trcon` |

## Problemas comuns

| Sintoma | Causa provável | Ação |
|---|---|---|
| `Failed to determine a suitable driver class` ao iniciar o backend | backend iniciou sem profile ativo | profile **`dev`** (default) ou Active profiles = `dev` |
| `FATAL: autenticação do tipo senha falhou` | Postgres de outro projeto ou credenciais erradas | conferir porta (`5434`); `DB_URL=...5434/trcon_site` |
| `localhost:8081` não abre | backend não subiu ou porta ocupada | verificar `Get-NetTCPConnection -State Listen` e iniciar o site em `:8081` |
| `Failed to start bean 'webServerStartStop'` | outra instância já ocupa a porta 8081 | localizar `OwningProcess`, conferir com `Get-Process` e encerrar somente a instância anterior reconhecida |
| `No default constructor found` em bean do chat | build anterior à correção de wiring | atualizar o código e recompilar; `ChatWiringTest` cobre `ChatRateLimiter` e `DeepSeekChatClient` |
| Chat retorna 503 no ambiente local | profile `dev` não está ativo ou variáveis sobrescreveram o stub | usar profile `dev`; remover override `TRCON_SITE_CHAT_ENABLED=false`/`TRCON_SITE_CHAT_STUB_ENABLED=false` |
| Radar vazio, Novidades ok | API só tinha highlights legados (artigos) ou pipeline não ingerido | esperado após 27/07: Radar usa JSON pipeline; conferir `fetchRadarHighlights` e `GET /api/public/highlights` |
| Novidades vazia, Radar ok | site backend off ou `env.js` apontando prod | `env.js` → `localhost:8081`; marketing publicou? `GET /api/public/news` |
| Artigo duplicado Radar + Novidades | deploy antigo (pré-27/07) | redeploy marketing + site; highlights legados filtrados na API |
| Frontend abre, mas formulário não envia | `env.js` ou CORS | conferir `TRCON_CORS_ALLOWED_ORIGINS` e URLs em `env.js` |
| Preview do Coolify tenta acessar `localhost:8081` | deploy anterior à resolução de hosts remotos | redeploy do frontend; configurar `SITE_API_UPSTREAM` para o backend dev; o preview passa a usar `/api/*` na própria origem |
| `npm test` falha por dependência ausente | `node_modules` não instalado | rodar `npm install` em `frontend/` |
| Backend falha por banco indisponível | PostgreSQL local não está pronto | aguardar health do container ou recriar com `docker compose up -d postgres` |

## Comandos rápidos

Subir tudo:

```powershell
cd C:\Trcongroup\projetos\site\infra
docker compose up -d --build
cd ..\frontend
npm install
npm run dev
```

Validar tudo:

```powershell
cd C:\Trcongroup\projetos\site\backend
.\mvnw.cmd clean verify
cd ..\frontend
npm test
npm run lint
python -m unittest discover scripts/tests
```
