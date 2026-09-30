# Frontend Stack Canônica — TRCon Site

## Objetivo

Definir a stack oficial de `trcongroup/site/frontend`: leve, de baixo custo,
compatível com a arquitetura híbrida definida em
[02-ARQUITETURA-CANONICA.md](02-ARQUITETURA-CANONICA.md), e minimamente
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
  [02-ARQUITETURA-CANONICA.md](02-ARQUITETURA-CANONICA.md))
- introduzir um framework de SPA agora aumentaria complexidade de build, custo
  cognitivo e superfície de falha sem ganho funcional real
- se o site evoluir para área logada/dashboard interativo pesado, essa decisão
  deve ser revisada explicitamente (ver "Quando revisar")

## Stack

| Camada | Escolha |
|---|---|
| Marcação | HTML5 semântico |
| Estilo | CSS3 modular em `styles/`, sem pré-processador; bundles públicos gerados por script Node |
| Comportamento | JavaScript ES2022+, módulos nativos (`<script type="module">`), sem transpiler obrigatório |
| Empacotamento | sem bundler de aplicação; concatenação determinística de CSS por script Node sem dependências |
| Lint/format | ESLint + Prettier (config mínima, sem regra exótica) |
| Testes de lógica JS | Vitest, apenas para funções não triviais (parsing/composição de dados de `data/*.json`) |
| Acessibilidade | checagem manual + `axe-core` via extensão de navegador no checkpoint de revisão |
| Hospedagem | Coolify no Hetzner, atrás do Cloudflare; alternativas estáticas ficam apenas como contingência |
| Integração com backend | `fetch` para endpoints públicos; Radar admite fallback local, enquanto Novidades usa somente a API institucional e fica oculta quando indisponível |

## O que não usar no início

- framework de SPA (React/Vue/Angular/Svelte)
- bundler pesado (Webpack) — se necessário empacotar algo no futuro, preferir
  uma ferramenta leve (Vite) apenas quando houver justificativa concreta
- CSS-in-JS, framework utilitário ou pipeline de design tokens complexo
- state management de frontend (Redux e equivalentes) — não há estado de
  aplicação complexo nesta fase

## Estrutura de pastas

```text
frontend/
  index.html
  style.css              # bundle gerado: base + site institucional/SPA
  article.css            # bundle gerado: base + artigo
  legal.css              # bundle gerado: base + página legal
  styles/                # CSS fonte editável
    tokens.css
    reset.css
    base.css
    layouts/
    components/
    pages/
    utilities.css
  scripts/
    build-css.mjs        # gera/verifica bundles em ordem explícita
    stamp-build-info.mjs
    core/                # pipeline Python
    providers/
    builders/
    tests/
  assets/
    app.js               # orquestração geral da página
    modules/
      config.js          # URLs de API por ambiente (TRCON_*_API_URL)
      content.js         # fallback do Radar e consumo institucional de Novidades
      article.js         # fallback CSR e metadados do artigo
      lead-form.js       # envio do lead comercial
      sanitize.js        # escaping e validações de apresentação
      chat-widget.js     # assistente institucional, histórico e CTAs seguros
  data/
    ai-radar.json
    tech-radar.json
    home-highlights.json
  tests/
    modules/               # testes Vitest dos módulos com lógica não trivial
```

Um módulo de comportamento (`assets/modules/*.js`) é criado por responsabilidade
(SRP) — o mesmo princípio já aplicado ao backend e aos scripts de pipeline em
[02-ARQUITETURA-CANONICA.md](02-ARQUITETURA-CANONICA.md).

O mesmo princípio vale para CSS. `styles/` é a fonte; os bundles públicos não são
editados manualmente. A ordem é tokens, reset, base, layouts, componentes, página e
utilidades. O plano de migração e os limites por arquivo estão em
[22-PLANO-REPOSICIONAMENTO-LIMPEZA.md](../22-PLANO-REPOSICIONAMENTO-LIMPEZA.md).

## Regras de implementação

1. Frontend nunca fala com banco — só com API pública do backend ou com JSON
   estático publicado.
2. Toda URL de API vem de `assets/modules/config.js`, nunca hardcoded espalhada
   pelo código (permite o rollout por configuração de
   [07-MIGRACAO-PARALELA.md](../07-MIGRACAO-PARALELA.md)).
3. Radar tem fallback explícito para JSON estático. Novidades não aceita fallback
   oriundo dos radares: se a API institucional estiver vazia ou indisponível, o
   bloco é ocultado sem quebrar a página.
4. Lógica de composição/parsing de dados (não trivial) fica isolada em função
   pura testável — não misturada com manipulação direta de DOM, para permitir
   teste unitário sem precisar de navegador.
5. Sem chave/segredo de API no frontend (herdado de
   [02-ARQUITETURA-CANONICA.md](02-ARQUITETURA-CANONICA.md)).
6. CSS de componente inclui sua própria adaptação responsiva; não existe um arquivo
   global de correções móveis que conheça todos os componentes.
7. Estilo repetido ou semântico usa classe. `style="..."` fica restrito a valor de
   conteúdo realmente dinâmico, por custom property validada.
8. `npm run build:css` deve ser reproduzível e `npm run check:css` impede bundle
   versionado desatualizado.
9. Página de artigo e página legal não carregam estilos exclusivos da Home,
   produtos ou formulários comerciais.

## Testes de frontend

Ver critério completo em [10-TESTES-QUALIDADE.md](10-TESTES-QUALIDADE.md).
Resumo: HTML/CSS estático não exige teste unitário de aparência; funções de
parsing/composição de dados em `assets/modules/*.js` devem ter teste unitário
(Vitest) para os casos de borda relevantes (payload vazio, campo ausente,
fallback acionado). O CSS exige verificação de bundle reproduzível, lint de sintaxe
e smoke visual responsivo nos checkpoints de migração.

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
