# Frontend Stack Canônica — TRCon Site

## Objetivo

Definir a stack oficial de `trcongroup/site/frontend`: leve, de baixo custo,
compatível com a arquitetura híbrida definida em
[02-ARQUITETURA-CANONICA.md](./02-ARQUITETURA-CANONICA.md), e minimamente
testável — sem introduzir um framework pesado que o porte atual do site não
justifica.

## Decisão oficial

Manter **HTML/CSS/JavaScript estático (vanilla)**, sem framework de UI
(React/Vue/Angular) nesta fase.

## Motivo da decisão

- o site é majoritariamente conteúdo público, editorial e institucional — não é
  uma aplicação com estado complexo de UI
- vanilla estático é o que já está publicado, funciona, e tem custo de hospedagem
  próximo de zero (herda o princípio de baixo custo de
  [02-ARQUITETURA-CANONICA.md](./02-ARQUITETURA-CANONICA.md))
- introduzir um framework de SPA agora aumentaria complexidade de build, custo
  cognitivo e superfície de falha sem ganho funcional real
- se o site evoluir para área logada/dashboard interativo pesado, essa decisão
  deve ser revisada explicitamente (ver "Quando revisar")

## Stack

| Camada | Escolha |
|---|---|
| Marcação | HTML5 semântico |
| Estilo | CSS3 (arquivo único `style.css`, sem pré-processador nesta fase) |
| Comportamento | JavaScript ES2022+, módulos nativos (`<script type="module">`), sem transpiler obrigatório |
| Empacotamento | nenhum bundler nesta fase — arquivos servidos diretamente |
| Lint/format | ESLint + Prettier (config mínima, sem regra exótica) |
| Testes de lógica JS | Vitest, apenas para funções não triviais (parsing/composição de dados de `data/*.json`) |
| Acessibilidade | checagem manual + `axe-core` via extensão de navegador no checkpoint de revisão |
| Hospedagem | Coolify no Hetzner, atrás do Cloudflare; alternativas estáticas ficam apenas como contingência |
| Integração com backend | `fetch` para endpoints públicos do backend (`/api/public/...`), com fallback para JSON local conforme [07-MIGRACAO-PARALELA.md](./07-MIGRACAO-PARALELA.md) |

## O que não usar no início

- framework de SPA (React/Vue/Angular/Svelte)
- bundler pesado (Webpack) — se necessário empacotar algo no futuro, preferir
  uma ferramenta leve (Vite) apenas quando houver justificativa concreta
- CSS-in-JS ou pipeline de design tokens complexo
- state management de frontend (Redux e equivalentes) — não há estado de
  aplicação complexo nesta fase

## Estrutura de pastas

```text
frontend/
  index.html
  style.css
  assets/
    app.js               # orquestração geral da página
    modules/
      radar.js            # renderização dos blocos Radar IA/Tecnologia/Mercado
      highlights.js        # consumo de highlights (API com fallback JSON)
      news.js               # consumo de novidades (API com fallback JSON)
      lead-form.js          # envio do formulário de lead (POST /api/v1/site/leads)
      config.js             # URLs de API por ambiente (TRCON_*_API_URL)
  data/
    market.json
    economy-tips.json
    recipes.json
    ai-radar.json          # planejado
    tech-radar.json        # planejado
    news-log.json          # planejado
    home-highlights.json   # planejado
  scripts/                 # geração offline de conteúdo (Python), ver ARQUITETURA-CANONICA
  tests/
    modules/               # testes Vitest dos módulos com lógica não trivial
```

Um módulo de comportamento (`assets/modules/*.js`) é criado por responsabilidade
(SRP) — o mesmo princípio já aplicado ao backend e aos scripts de pipeline em
[02-ARQUITETURA-CANONICA.md](./02-ARQUITETURA-CANONICA.md).

## Regras de implementação

1. Frontend nunca fala com banco — só com API pública do backend ou com JSON
   estático publicado.
2. Toda URL de API vem de `assets/modules/config.js`, nunca hardcoded espalhada
   pelo código (permite o rollout por configuração de
   [07-MIGRACAO-PARALELA.md](./07-MIGRACAO-PARALELA.md)).
3. Todo consumo de API pública tem fallback explícito para o JSON estático
   correspondente — nenhuma seção do site pode quebrar por indisponibilidade do
   backend.
4. Lógica de composição/parsing de dados (não trivial) fica isolada em função
   pura testável — não misturada com manipulação direta de DOM, para permitir
   teste unitário sem precisar de navegador.
5. Sem chave/segredo de API no frontend (herdado de
   [02-ARQUITETURA-CANONICA.md](./02-ARQUITETURA-CANONICA.md)).

## Testes de frontend

Ver critério completo em [10-TESTES-QUALIDADE.md](./10-TESTES-QUALIDADE.md).
Resumo: HTML/CSS estático não exige teste automatizado; funções de
parsing/composição de dados em `assets/modules/*.js` devem ter teste unitário
(Vitest) para os casos de borda relevantes (payload vazio, campo ausente,
fallback acionado).

## Performance e SEO mínimos

- imagens otimizadas (formato moderno quando possível, `alt` sempre presente)
- `meta description`, `title` únicos por página, `Open Graph` básico
- carregamento de script não bloqueante (`defer`/`module`) para não penalizar
  a primeira renderização
- Lighthouse (performance/acessibilidade/SEO) como checagem manual em
  checkpoints de release, sem exigir ferramenta paga

## Suporte de navegador

Últimas duas versões estáveis de Chrome, Edge, Firefox e Safari. Sem suporte a
Internet Explorer.

## Quando revisar esta decisão

Revisar a stack de frontend apenas se surgir:

- necessidade real de área logada com estado de UI complexo (dashboard
  interativo, múltiplas telas com navegação client-side)
- necessidade de reuso de componentes de UI entre múltiplos produtos do grupo
  TRCon (justificaria avaliar um design system com framework)

Sem isso, a stack vanilla estática permanece a decisão oficial.
