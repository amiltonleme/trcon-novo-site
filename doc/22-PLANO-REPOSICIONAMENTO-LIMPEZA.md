# Plano de reposicionamento tecnológico e limpeza — Site TRCONGROUP

> Criado em **28/09/2026**. Plano de execução; nenhuma exclusão descrita aqui
> deve ser feita sem concluir a etapa de substituição e as verificações da própria
> fase.

## Objetivo

Reposicionar o site institucional para comunicar tecnologia, desenvolvimento de
software e inteligência artificial, preservando logo e cores, e retirar código,
conteúdo e infraestrutura que sustentam a percepção antiga de portal financeiro.

O resultado deve apresentar com transparência:

- 21 anos de existência da TRCONGROUP
- evolução já consolidada para novas tecnologias, IA, desenvolvimento sob demanda,
  customização e outsourcing
- foco em gerar oportunidades, propostas e contratos reais
- quatro linhas de negócio: produtos, desenvolvimento sob demanda, customização e
  alocação de profissionais
- IA aplicada como capacidade concreta da empresa
- página Trabalhe Conosco com estado real de vagas
- assistente institucional limitado à base factual da TRCONGROUP

Este plano complementa:

- [01-POSICIONAMENTO-INSTITUCIONAL.md](canonical/01-POSICIONAMENTO-INSTITUCIONAL.md)
- [08-REDESIGN-DIRETRIZES.md](canonical/08-REDESIGN-DIRETRIZES.md)
- [21-CHAT-IA-DEEPSEEK.md](21-CHAT-IA-DEEPSEEK.md)

## Decisões de escopo

### Permanece no site institucional

- marca, logo, paleta, fundo escuro e linguagem visual tecnológica
- páginas dos produtos Sírius, inclusive o Sírius Hub financeiro
- formulário de lead comercial
- Radar de IA e Tecnologia
- Novidades publicadas pela própria TRCONGROUP
- backend de leads, notícias, feed, sitemap e destaques tecnológicos

### Sai da Home institucional

- ticker de cotações
- tabela de ativos e recomendações
- humor de mercado
- Educação Financeira
- receitas econômicas
- linguagem que apresenta a empresa inteira como negócio financeiro

Conteúdo financeiro que ainda tiver valor de produto deve ser movido para o
contexto do **Sírius Hub** antes de ser excluído. Ele não deve permanecer na Home
apenas porque o código já existe.

### Não pode ser apresentado

- clientes, contratos, cases, depoimentos e logotipos inexistentes
- métricas de projetos, equipe ou resultados não comprovadas
- parcerias, certificações ou vagas sem registro factual aprovado
- produtos internos ou protótipos descritos como trabalhos para clientes

## Navegação alvo

1. Home
2. Empresa
3. Soluções
4. Produtos
5. Conteúdo
6. Trabalhe Conosco
7. Contato

O assistente TRCONGROUP fica disponível nas páginas públicas. Intenção comercial
leva ao Contato; intenção de carreira leva a Trabalhe Conosco.

## Estratégia de execução

A substituição vem antes da exclusão. Cada fase deve terminar com o site navegável,
testes verdes e um ponto de reversão. Não misturar toda a reformulação, o chat e a
remoção do backend financeiro em um único deploy.

## Sequência executiva recomendada

Esta é a ordem prática para transformar o plano em trabalho de implementação. As
seções seguintes detalham cada item.

### 1. Preparar o repositório

- separar alterações que já estejam em andamento em `index.html`, `style.css` e
  demais arquivos
- executar os testes atuais e registrar o baseline visual
- remover `__pycache__` e `*.pyc` e adicioná-los ao `.gitignore`
- confirmar se `trcon-logo.old-backup.png` pode ser retirado
- não iniciar exclusões funcionais enquanto o baseline não estiver conhecido

### 2. Fatiar o CSS sem alterar o visual

- criar `frontend/styles/`
- separar tokens, reset, base, layouts, componentes e páginas
- implementar `scripts/build-css.mjs`
- gerar `style.css`, `article.css` e `legal.css`
- migrar o CSS embutido de `privacidade.html`
- adicionar `build:css` e `check:css`
- comparar desktop e mobile antes e depois de cada extração

### 3. Implantar a nova arquitetura de navegação

- Home
- Empresa
- Soluções
- Produtos
- Conteúdo
- Trabalhe Conosco
- Contato

Substituir a página atual “Clientes” por “Como ajudamos” ou “Para empresas”, com
alias temporário para links antigos.

### 4. Reposicionar Home e Empresa

- apresentar os 21 anos
- comunicar IA, novas tecnologias, desenvolvimento sob demanda, customização e
  outsourcing como atuação atual
- remover a narrativa predominantemente financeira
- trocar os pilares por Engenharia de Software, IA e Automação, Produtos Digitais
  e Squads/Profissionais
- retirar ticker, ativos, humor de mercado e educação financeira da Home

### 5. Transformar Serviços em ofertas comerciais

- Diagnóstico de IA e automação
- Desenvolvimento de MVP ou produto
- Modernização e customização
- Outsourcing por profissional, célula ou squad

Cada oferta apresenta problema, entrega, processo, modelo de contratação e CTA.

### 6. Melhorar a conversão

- usar CTAs “Solicitar diagnóstico”, “Falar sobre um projeto”, “Receber uma
  proposta” e “Montar meu time”
- preservar `origem` e `leadType` no formulário
- definir processo interno de qualificação, reunião, proposta e acompanhamento
- medir visitas por oferta, formulários iniciados, leads qualificados, reuniões,
  propostas e contratos

### 7. Criar Trabalhe Conosco

- publicar cultura, forma de trabalho e áreas profissionais de interesse
- mostrar o estado real das vagas
- incluir perguntas frequentes
- deixar claro quando não houver posições abertas
- implementar banco de talentos apenas depois de definir consentimento, retenção,
  exclusão e proteção dos dados

### 8. Implementar o assistente TRCONGROUP

- criar `trcon-knowledge.yml`
- integrar DeepSeek somente pelo backend
- exigir JSON estruturado e `sourceIds` válidos
- separar CTA comercial de CTA de carreira
- aplicar rate limit, orçamento e fallback
- testar perguntas externas, informação ausente, clientes/cases e prompt injection

Para clientes e cases, usar a formulação factual definida na especificação:

> Ainda não há cases de clientes publicados na base institucional da TRCONGROUP.
> Posso apresentar nossas capacidades, produtos e formas de contratação.

### 9. Limpar o legado financeiro do frontend

- remover consumidores de `market.json`, `economy-tips.json` e `recipes.json`
- remover ticker, tabela de mercado e cards de educação financeira
- remover funções, imports, estilos e testes que ficarem sem consumidor
- preservar conteúdo financeiro útil somente nas páginas do Sírius Hub

### 10. Simplificar o pipeline

- remover `update_market.py`
- remover `update_economy_tips.py`
- remover `update_daily_content.py`
- retirar builders, catálogos e testes exclusivos desses fluxos
- atualizar `.github/workflows/update-content.yml`
- manter Radar IA, Radar Tecnologia e conteúdo institucional real

### 11. Desativar o backend financeiro

- interromper primeiro os produtores externos de `economy-tips`
- publicar o frontend sem consumir os endpoints antigos
- verificar logs e configurações
- remover controllers, services, DTOs, entidades e testes
- retirar `EconomyTipRepository` do scheduler
- criar migration nova para excluir tabela e índices somente após backup
- nunca editar migrations já aplicadas

### 12. Validar e publicar gradualmente

- rodar testes de frontend, backend e pipeline
- executar lint, build e `check:css`
- fazer smoke em desktop e mobile
- verificar console, rede, SEO, acessibilidade e navegação
- publicar conteúdo e páginas antes de remover infraestrutura
- implantar na ordem: posicionamento, CSS, páginas, conversão, carreiras, chat,
  limpeza frontend, pipeline, backend e banco

## Organização recomendada em PRs

| PR | Escopo | Condição para merge |
|---|---|---|
| 1 | Baseline, `.gitignore`, caches e inventário | testes atuais executados; alterações anteriores preservadas |
| 2 | Infraestrutura CSS e extração sem mudança visual | bundles reproduzíveis; comparação visual aprovada |
| 3 | Navegação, Home e Empresa | posicionamento tecnológico claro; hashes e mobile funcionando |
| 4 | Soluções, ofertas e conversão | cada oferta possui CTA e formulário recebe contexto |
| 5 | Trabalhe Conosco | estado real das vagas; nenhuma coleta sem LGPD definida |
| 6 | Base factual e chat | respostas com fonte; escopo e desconhecimento testados |
| 7 | Remoção do legado financeiro no frontend | nenhuma requisição aos arquivos/endpoints antigos |
| 8 | Simplificação do pipeline | workflow gera somente artefatos consumidos |
| 9 | Retirada do módulo backend | produtores desligados; testes e scheduler verdes |
| 10 | Migration de limpeza e fechamento documental | backup validado; smoke de produção; documentos sincronizados |

## Etapa 0 — Baseline e proteção do estado atual

### Ações

1. Revisar `git status` e separar alterações em andamento da execução deste plano.
2. Registrar o comportamento atual da Home, Empresa, Serviços, Produtos, Clientes,
   Contato e páginas de produto em desktop e mobile.
3. Rodar a suíte atual antes de alterar o código:
   - frontend: `npm test`, `npm run lint`, `npm run build`
   - pipeline: `python -m unittest discover -s scripts -p "test_*.py"`
   - backend: `mvnw test`
4. Inventariar consumidores dos endpoints públicos e internos de `economy-tips`,
   incluindo o Sirius Marketing e configurações de produção.
5. Criar a lista de conteúdo financeiro que será:
   - movido para o Sírius Hub
   - arquivado
   - excluído por não ter mais função

### Critério de aceite

- baseline documentado
- alterações anteriores preservadas
- testes atuais conhecidos
- nenhum endpoint ou arquivo marcado para exclusão sem consumidor identificado

## Etapa 1 — Fonte de verdade institucional

### Ações

1. Consolidar em `01-POSICIONAMENTO-INSTITUCIONAL.md` somente fatos aprovados.
2. Revisar todo o texto público e remover afirmações sem comprovação.
3. Criar `backend/src/main/resources/chat/trcon-knowledge.yml` com IDs estáveis
   para identidade, serviços, produtos, carreiras e situação comercial.
4. Registrar explicitamente na base:
   - 21 anos de existência
   - foco tecnológico atual
   - ofertas comerciais e formas de contratação
   - cases de clientes efetivamente publicados, inicialmente vazios
   - lista real de produtos e seus estados
   - vagas abertas e disponibilidade do banco de talentos
5. Definir responsável e processo de revisão da base factual.

### Critério de aceite

- site e chat usam a mesma verdade institucional
- nenhum fato comercial depende apenas de texto livre no prompt
- informação ausente tem fallback explícito, sem inferência do modelo

## Etapa 2 — Arquitetura de informação e conteúdo

### Home

1. Reescrever o hero para software, IA e times de tecnologia.
2. Trocar os pilares financeiros por:
   - Engenharia de Software
   - IA, Dados e Automação
   - Produtos Digitais
   - Squads e Profissionais
3. Usar CTAs “Conheça nossas soluções” e “Converse com a TRCONGROUP”.
4. Inserir “Como trabalhamos”: descoberta, arquitetura, construção, operação e
   evolução.
5. Exibir produtos próprios como demonstração de capacidade.
6. Manter Radar apenas para IA e tecnologia.
7. Exibir Novidades apenas com conteúdo publicado pela TRCONGROUP.

### Empresa

1. Comunicar os 21 anos e a evolução tecnológica como atuação atual da empresa.
2. Remover a narrativa não documentada de que a empresa nasceu para democratizar
   finanças pessoais.
3. Apresentar princípios de engenharia, qualidade, transparência e aplicação de IA.
4. Não preencher a ausência de cases com números genéricos.

### Conversão e aquisição de clientes

1. Definir públicos prioritários para cada oferta: empresa que precisa construir,
   modernizar, aplicar IA ou ampliar time.
2. Criar quatro ofertas de entrada com escopo compreensível:
   - diagnóstico de IA e automação
   - desenvolvimento de MVP/produto sob demanda
   - modernização e customização de sistemas
   - outsourcing por profissional, célula ou squad
3. Para cada oferta, apresentar problema, entregáveis, processo, formato de
   contratação e CTA próprio.
4. Usar CTAs orientados à ação: “Solicitar diagnóstico”, “Falar sobre um projeto”,
   “Receber uma proposta” e “Montar meu time”.
5. Preservar `origem` e `leadType` no formulário para qualificar o contato.
6. Definir processo interno de resposta, qualificação, reunião, proposta e
   acompanhamento; publicar prazo somente depois que houver capacidade de cumpri-lo.
7. Medir visitas por oferta, abertura de formulário, leads qualificados, reuniões,
   propostas e contratos, sem exibir essas métricas publicamente.
8. Transformar contratos concluídos em cases apenas com autorização e dados
   verificáveis.

### Soluções

1. Organizar por resultado e entrega:
   - desenvolvimento sob demanda
   - modernização e customização
   - IA e automação
   - dados e BI
   - squads e alocação
2. Separar soluções de modelos de contratação.
3. Publicar tecnologias específicas somente quando fizerem parte da capacidade
   real da empresa.

### Clientes

A página atual de perfis financeiros não representa clientes corporativos. O
destino recomendado é substituí-la por **Como ajudamos** ou **Para empresas**.
Durante uma versão, `#clientes` pode funcionar como alias para a nova página; em
seguida o identificador antigo é removido.

### Critério de aceite

- a primeira dobra é entendida como empresa de tecnologia
- finanças aparecem como domínio de um produto, não como identidade da empresa
- não há prova social simulada
- links e hashes antigos têm tratamento de compatibilidade definido

## Etapa 3 — Evolução visual e componentes

### Diagnóstico CSS atual

`frontend/style.css` concentra responsabilidades demais:

| Medida em 28/09/2026 | Resultado |
|---|---:|
| Linhas | 2.170 |
| Tamanho | 62.594 bytes |
| Blocos `@media` | 9 |
| Animações `@keyframes` | 14 |
| Usos de `!important` | 0 |
| Atributos `style` no `index.html` | 87 |
| CSS embutido em `privacidade.html` | 42 linhas |

O arquivo mistura tokens, reset, navegação, dois tipos de hero, componentes,
produtos, formulários, páginas, conteúdo financeiro, responsividade, artigos e
botão flutuante. Há regras repetidas para `.tr-corner`, `.tr-full-name`, títulos,
cards de produto e vários blocos para o mesmo breakpoint de 768px.

A ausência de `!important` é positiva e deve ser preservada. O problema é de
responsabilidade, ordem e manutenção, não de uma cascata já irrecuperável.

### Arquitetura CSS proposta

Manter CSS puro e sem framework. O código-fonte passa a viver em arquivos menores;
arquivos públicos são gerados em ordem determinística.

```text
frontend/
  styles/                         # fonte editável
    tokens.css                    # cores, tipografia, espaçamento, raios, sombras
    reset.css                     # box-sizing, margens, elementos básicos
    base.css                      # body, links, títulos, foco, scrollbar
    layouts/
      navigation.css
      page-shell.css
      sections.css
      footer.css
    components/
      buttons.css
      cards.css
      forms.css
      subpage-hero.css
      product-cards.css
      lightbox.css
      mobile-nav.css
      floating-actions.css
      chat.css                    # quando o assistente for implementado
    pages/
      home.css
      company.css
      services.css
      products.css
      careers.css
      article.css
      legal.css
    utilities.css                 # poucas utilidades intencionais
  scripts/
    build-css.mjs                 # concatena manifests e valida ordem
  style.css                       # bundle gerado para o site institucional
  article.css                     # bundle gerado para artigo/SSR
  legal.css                       # bundle gerado para privacidade
```

`style.css`, `article.css` e `legal.css` devem começar com comentário de arquivo
gerado e não ser editados manualmente. Como o Docker atual copia a árvore estática
diretamente para o nginx, os bundles permanecem versionados até que exista uma
etapa de build no container.

### Ordem dos bundles

O script usa manifests explícitos, sem glob que dependa da ordem do sistema de
arquivos:

1. tokens
2. reset
3. base
4. layouts
5. components
6. página
7. utilities

Na primeira migração, não usar `@import` no CSS entregue ao navegador. A
concatenação evita cascata de requisições e mantém um contrato previsível. Também
não introduzir Sass, Tailwind, PostCSS ou CSS-in-JS para resolver um problema que
CSS modular e um script Node pequeno já resolvem.

### Responsividade

As regras responsivas ficam junto do componente ou página que modificam. Evitar um
grande arquivo `responsive.css` no fim da cascata, pois ele recriaria o mesmo
acoplamento atual.

Breakpoints permitidos inicialmente:

- 480px: telefones pequenos
- 768px: navegação e layout móvel
- 1024px: tablets e grades intermediárias
- 1400px: grades largas específicas

Um breakpoint novo exige justificativa no próprio módulo. Regras hoje espalhadas
em 600px e múltiplos blocos de 768px devem ser consolidadas durante a migração.

### Convenções

- componentes não dependem do ID da página
- estados usam `.is-open`, `.is-active`, `.is-visible` e atributos ARIA
- hooks de JavaScript usam `data-*`; uma classe de estilo não deve existir apenas
  para o JavaScript encontrá-la
- modificadores visuais usam classes, não `style="..."`
- valores reutilizados viram tokens; valores realmente únicos permanecem no módulo
- evitar seletores com mais de três níveis
- preservar especificidade baixa e não introduzir `!important`
- animações ficam no módulo do componente que as usa
- toda animação relevante respeita `prefers-reduced-motion: reduce`

Não fazer renomeação em massa de todas as classes apenas para adotar BEM ou outro
padrão. Classes antigas podem ser migradas por componente; código novo usa nomes
orientados a componente, layout, página, utilidade e estado.

### Retirada de CSS inline

Os 87 atributos `style` do `index.html` devem ser classificados:

1. **repetidos**: transformar em classe de componente ou modificador, como margens
   de grids, grupos de botão e variações de ícone
2. **tokens visuais**: transformar em variável ou modificador, como cor de destaque
3. **estado dinâmico**: substituir por classe/ARIA controlada pelo JavaScript
4. **valor de conteúdo realmente dinâmico**: permitir custom property validada,
   nunca uma declaração CSS arbitrária

O `IntersectionObserver` atual escreve `opacity`, `transform` e `transition`
diretamente no elemento. Ele deve passar a alternar `.reveal` e `.is-visible`; o
CSS controla a animação e o modo de movimento reduzido.

As 42 linhas do `<style>` de `privacidade.html` migram para `pages/legal.css`.
Estilos inline usados em HTML de e-mail não entram nessa regra, porque clientes de
e-mail exigem CSS inline.

### Build e verificação

Adicionar ao `package.json`:

```json
{
  "scripts": {
    "build:css": "node scripts/build-css.mjs",
    "check:css": "node scripts/build-css.mjs --check",
    "build": "npm run build:css && node scripts/stamp-build-info.mjs"
  }
}
```

`--check` gera o conteúdo em memória e falha se o bundle versionado estiver
desatualizado. O CI deve executar `check:css`. O script de cache busting deve
versionar todos os bundles usados, não apenas `style.css`.

Depois da migração estrutural, adicionar Stylelint como dependência de
desenvolvimento para validar sintaxe, duplicações acidentais e propriedades
inválidas. Primeiro organizar os arquivos; depois habilitar regras gradualmente,
sem uma formatação massiva misturada à mudança visual.

Não usar PurgeCSS na primeira etapa. O site aplica classes via JavaScript e a
remoção automática pode eliminar estilos válidos. Código morto deve ser removido
por componente com busca de referências e smoke visual.

### Sequência de migração do CSS

1. Criar `tokens.css`, `reset.css` e `base.css` sem mudar o resultado visual.
2. Extrair navegação, estrutura de página e footer.
3. Extrair componentes compartilhados: botões, cards, formulários e heroes.
4. Extrair páginas de artigo e privacidade e trocar os respectivos `<link>`.
5. Extrair páginas institucionais e produtos.
6. Migrar estilos inline para classes.
7. Consolidar media queries dentro de cada módulo.
8. Executar a limpeza financeira da Etapa 6 nos módulos já separados.
9. Ativar `check:css`, Stylelint e orçamento de tamanho.

Cada extração deve produzir o mesmo layout antes de qualquer redesign. Isso torna
o diff revisável e permite distinguir regressão de refatoração estrutural.

### Orçamento e critérios CSS

- nenhum arquivo-fonte deve ultrapassar 400 linhas sem justificativa
- objetivo normal por arquivo: 50 a 250 linhas
- `style.css` é bundle gerado; seu tamanho não mede organização do código-fonte
- artigo e privacidade não carregam CSS de produtos, formulários comerciais ou Home
- zero CSS embutido nas páginas HTML, exceto HTML de e-mail
- zero `!important`, salvo exceção documentada
- zero seletor sem consumidor depois de cada etapa
- bundles reproduzíveis por `npm run build:css`
- `npm run check:css`, lint, testes e smoke visual verdes
- registrar tamanho bruto e comprimido dos bundles no checkpoint de release
- nenhuma página carrega um bundle específico de outra página

### Ações

1. Preservar variáveis de cor, logo e fundo principal.
2. Substituir elementos de bolsa por componentes ligados a engenharia:
   arquiteturas, integrações, pipelines, automações e telas reais de produto.
3. Padronizar cards e trocar emojis por iconografia vetorial consistente.
4. Criar componentes reutilizáveis para capacidades, etapas de entrega, produtos,
   chamadas comerciais e chamadas de carreira.
5. Reduzir estilos inline do `index.html` e criar classes semânticas.
6. Validar contraste, navegação por teclado, movimento reduzido e responsividade.

### Critério de aceite

- identidade visual preservada
- percepção tecnológica não depende de imagens genéricas de cérebro/robô
- componentes não carregam nomes financeiros quando têm uso genérico
- layout funciona nos principais tamanhos de tela
- CSS fonte está dividido por responsabilidade e os bundles são reproduzíveis
- artigo e privacidade carregam apenas estilos comuns e próprios

## Etapa 4 — Página Trabalhe Conosco

### V1 recomendada

Página estática com:

1. trajetória de 21 anos e cultura tecnológica atual
2. cultura e forma de trabalho
3. áreas de interesse
4. estado atual das vagas
5. perguntas frequentes
6. aviso claro quando não houver vagas abertas

### Banco de talentos opcional

Somente implementar formulário depois de definir:

- finalidade e consentimento LGPD
- dados mínimos coletados
- armazenamento do currículo
- prazo de retenção
- canal de consulta e exclusão
- acesso administrativo e auditoria
- resposta automática sem promessa de entrevista ou contratação

O fluxo de candidato não reutiliza silenciosamente a entidade ou o formulário de
lead comercial. Se aprovado, deve ter contrato, domínio e testes próprios.

### Critério de aceite

- página acessível pelo menu e rodapé
- nenhuma vaga inexistente é anunciada
- o chat aponta para a página de carreiras, não para o lead comercial
- eventual coleta de candidato tem ciclo de vida documentado

## Etapa 5 — Assistente institucional

Executar a especificação de [21-CHAT-IA-DEEPSEEK.md](21-CHAT-IA-DEEPSEEK.md):

1. implementar base factual YAML e validador
2. implementar cliente DeepSeek somente no backend
3. exigir JSON estruturado e `sourceIds` válidos
4. aplicar rate limit, orçamento e fallback
5. implementar CTAs separados para contato e carreira
6. testar desconhecimento, escopo e prompt injection
7. publicar o aviso de processamento por terceiro

### Critério de aceite

- nenhuma resposta factual sem fonte válida chega ao navegador
- perguntas externas são recusadas
- clientes, cases, contratos, vagas e história não são inventados
- indisponibilidade do provedor não quebra o site

## Etapa 6 — Limpeza do frontend e do pipeline

Esta etapa ocorre depois que a nova Home estiver pronta.

### HTML a retirar ou substituir

Em `frontend/index.html`:

- retirar `#ticker` e `.ticker-wrap` do hero
- retirar `#section-market`
- retirar `#block-economy-tips` e `#section-economy-tips`
- substituir `#page-clientes` pela página corporativa definida na Etapa 2
- preservar as páginas e imagens do Sírius Hub que ainda explicam o produto

### JavaScript a retirar

Em `frontend/assets/app.js`:

- `renderTicker`
- `renderMarket`
- `renderTips`
- `renderRecipes`
- carregamentos de `market.json`, `economy-tips.json` e `recipes.json`
- imports que ficarem sem uso depois dessas remoções

`recipes.json` já é um caso de código morto: o pipeline gera o arquivo e o
JavaScript tenta carregá-lo, mas não existe `#recipeGrid` no HTML atual.

Em `frontend/assets/modules/content.js`:

- retirar `loadEconomyTips` e `normalizeTitleKey` se nenhum consumidor permanecer
- preservar `fetchWithFallback`, `fetchRadarHighlights` e renderização de notícias

Em `frontend/assets/modules/sanitize.js`, remover somente após nova busca de
referências:

- `changeClass`
- `safePercent`
- `safeCssColor`
- `safeGradient`

Os testes exclusivos dessas funções também saem. Funções genéricas ainda usadas,
como `escapeHtml`, `safeUrl` e `safeClass`, permanecem.

### CSS a retirar

Após remover HTML e JS, procurar seletores sem referência e retirar os blocos de:

- ticker e animação `@keyframes ticker`
- tabela/status de mercado
- `insight-card` financeiro
- `recipe-card`, `recipe-thumb` e `recipe-body`

Não apagar `.market-disclaimer` de forma global sem revisar o uso remanescente na
página de Serviços. Se o elemento continuar necessário, renomeá-lo para uma classe
genérica, como `.context-note`, antes de remover o bloco financeiro.

### Dados estáticos a retirar

Depois de eliminar todos os consumidores:

- `frontend/data/market.json`
- `frontend/data/economy-tips.json`
- `frontend/data/recipes.json`

Preservar:

- `ai-radar.json`
- `tech-radar.json`
- `home-highlights.json`

`news-log.json` exige correção: hoje é gerado a partir dos radares externos e pode
parecer “Novidades da TRCONGROUP”. A nova versão deve usar a API de notícias
institucionais. Se a API estiver vazia ou indisponível, ocultar a seção ou usar um
fallback editorial realmente publicado pela empresa; não renomear notícia externa
como novidade própria.

### Scripts Python a retirar

Quando os JSONs deixarem de ser consumidos:

- `frontend/scripts/update_market.py`
- `frontend/scripts/update_economy_tips.py`
- `frontend/scripts/update_daily_content.py`
- `frontend/scripts/builders/economy_tips_builder.py`
- `frontend/scripts/catalog/economy_tips_fallback.py`

Retirar os testes desses builders e atualizar `frontend/scripts/README.md`.
Preservar `core/`, `providers/`, `radar_builder.py`, `update_ai_radar.py` e
`update_tech_radar.py` enquanto alimentarem o Radar tecnológico.

### GitHub Actions

Em `.github/workflows/update-content.yml`:

- retirar os passos “Atualizar mercado”, “Atualizar Educação Financeira” e
  “Atualizar receitas econômicas”
- manter testes do pipeline e geração dos radares
- ajustar a consolidação para produzir somente artefatos realmente consumidos
- parar de gerar `news-log.json` a partir de notícias externas

### Configuração do frontend

Retirar, depois da desativação do endpoint:

- `TRCON_ECONOMY_TIPS_API_URL` de `frontend/assets/env.js`
- `economyTipsApiUrl` de `frontend/assets/modules/config.js`
- testes exclusivos dessa configuração

### Critério de aceite

- nenhuma requisição a arquivos ou endpoints removidos no navegador
- nenhum import, função, seletor ou teste órfão
- pipeline gera apenas arquivos consumidos
- Radar de IA/Tecnologia e Novidades continuam funcionando

## Etapa 7 — Desativação do backend financeiro

O módulo `economytips` não deve ser apagado antes de parar os produtores e
consumidores externos.

### Ordem segura

1. Alterar o Sirius Marketing para não publicar mais em
   `POST /api/internal/economy-tips`; decidir se o conteúdo passa a ser notícia
   institucional ou deixa de ser enviado ao site.
2. Publicar o frontend sem chamadas a `GET /api/public/economy-tips`.
3. Verificar logs e configurações para confirmar ausência de tráfego.
4. Durante uma versão de transição, documentar a depreciação ou responder `410
   Gone` nos endpoints antigos, se houver risco de consumidor residual.
5. Remover os pacotes:
   - `backend/.../economytips/`
   - `backend/.../internal/economytips/`
6. Remover os testes de `economytips`.
7. Retirar `EconomyTipRepository` do `ContentExpiryScheduler`; manter a expiração
   de notícias se ela continuar necessária.
8. Atualizar documentação, smoke tests e configurações de produção.

### Banco de dados

Não editar nem excluir migrations já aplicadas, especialmente
`V6__economy_tips.sql` e a parte correspondente de
`V9__content_expires_at.sql`.

Depois de backup e confirmação de que não há consumidor, criar uma migration nova,
por exemplo `V10__drop_economy_tips.sql`, para remover tabela e índices. A migration
deve ser executada primeiro em ambiente de teste. Se o conteúdo precisar ser
preservado para o Sírius Hub, exportá-lo ou migrá-lo antes do `DROP TABLE`.

### Critério de aceite

- nenhum produtor ou consumidor chama os endpoints removidos
- aplicação inicia sem beans de `economytips`
- scheduler e testes passam
- migration nova funciona em banco atualizado desde V1
- rollback de aplicação e recuperação do backup foram definidos

## Etapa 8 — Higiene do repositório

### Artefatos gerados

Retirar todos os diretórios `__pycache__` e arquivos `*.pyc` versionados ou não
versionados. Adicionar ao `.gitignore`:

```gitignore
__pycache__/
*.py[cod]
```

Depois, confirmar com `git ls-files` e `rg --files` que nenhum artefato Python
permanece na árvore versionada.

### Backups dentro do código

`frontend/assets/trcon-logo.old-backup.png` não tem referência de execução. Antes
de removê-lo, confirmar visualmente que o logo atual é o definitivo e que o arquivo
não é exigido por documentação ou processo de design. Backups necessários devem
ficar no histórico do Git ou em armazenamento de design, não misturados aos assets
publicados.

### Código e documentação residual

1. Procurar referências a:
   - `economy-tips`
   - `EconomyTip`
   - `market.json`
   - `recipes.json`
   - `update_market`
   - `update_daily_content`
2. Classificar cada resultado como código ativo, migration histórica ou documento
   histórico.
3. Atualizar documentos canônicos, deploy, ambiente local, status e custos.
4. Não reescrever migrations históricas apenas para remover palavras antigas.

### Critério de aceite

- `git status` não mostra cache ou bytecode gerado
- assets publicados não contêm backups
- buscas residuais retornam apenas migrations e histórico explicitamente mantidos
- documentação descreve o sistema que realmente está implantado

## Etapa 9 — Verificação e publicação

### Testes técnicos

- frontend: testes, lint e build
- pipeline Python: testes sem rede
- backend: testes unitários e de integração
- smoke de Home, Empresa, Soluções, Produtos, Trabalhe Conosco, Contato, artigos e chat
- console do navegador sem 404, import quebrado ou erro de JavaScript
- validação de sitemap, feed, SEO, navegação por hash e responsividade

### Testes editoriais

- busca por alegações de clientes, contratos, cases e vagas
- revisão de todos os números e datas
- perguntas adversariais no chat
- confirmação de que conteúdo financeiro aparece apenas no contexto de produto
- confirmação de que notícias externas não são apresentadas como novidades da empresa

### Ordem de deploy

1. fonte factual e conteúdo novo
2. nova Home, Empresa, Soluções e Trabalhe Conosco
3. chat institucional
4. frontend sem consumidores financeiros
5. pipeline simplificado
6. backend com endpoints desativados/removidos
7. migration de remoção de dados, somente após observação e backup

## Definição de pronto

- o site se apresenta como empresa de tecnologia, desenvolvimento e IA
- 21 anos e evolução tecnológica são comunicados como atuação atual
- não há clientes, cases, contratos, parceiros, métricas ou vagas inventados
- ofertas, landing sections e CTAs conduzem a contatos qualificados e propostas
- Trabalhe Conosco existe e mostra o estado real das oportunidades
- o assistente responde somente com a base factual da TRCONGROUP
- Home não carrega ticker, mercado, educação financeira ou receitas
- conteúdo financeiro remanescente está restrito ao produto correspondente
- frontend não solicita dados ou endpoints removidos
- pipeline não gera arquivos sem consumidor
- módulo `economytips` foi desativado com sequência segura ou está marcado com
  prazo explícito de retirada
- caches, bytecode e backups não fazem parte dos assets/código versionado
- testes, smoke e documentação estão atualizados
