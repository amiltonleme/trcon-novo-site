# Plano de reposicionamento tecnológico e limpeza — Site TRCONGROUP

> Criado em **28/09/2026**. Plano de execução; nenhuma exclusão descrita aqui
> deve ser feita sem concluir a etapa de substituição e as verificações da própria
> fase.
>
> **Última atualização: 30/09/2026.** Etapas 0, 1, 2, 3, 4, 5, 6 e 7 concluídas.
> Próximo gate: Etapa 8 — Higiene do repositório.

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
- assistente institucional e tecnológico: fatos da TRCONGROUP limitados à base
  autorizada e explicações gerais restritas ao contexto de tecnologia

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

## Estado real da execução em 30/09/2026

> **Desvio de execução registrado:** o commit `c3a2325` misturou partes das Etapas
> 0 a 6 em uma única alteração de 89 arquivos, sem concluir e registrar o gate de
> cada etapa antes de avançar. Isso não seguiu a estratégia definida acima. O
> commit não representa a conclusão integral deste plano.

| Etapa formal | Estado real | Entregue | Falta para o gate |
|---|---|---|---|
| 0 — Baseline e proteção | **Concluída em 28/09/2026** | estado do Git preservado; 260 testes executados; lint e build aprovados; baseline visual desktop/mobile registrado; produtor e consumidores de `economy-tips` inventariados; conteúdo financeiro classificado | nenhuma pendência do gate; a retirada do produtor continua sendo pré-condição da Etapa 7 |
| 1 — Fonte de verdade | **Concluída em 28/09/2026** | fonte canônica com 17 IDs estáveis após a inclusão do glossário tecnológico; YAML sincronizado; responsáveis e revisão definidos; conteúdo público e fallbacks auditados; teste de contrato entre documento, site e chat | nenhuma pendência do gate; fatos novos exigem o processo de aprovação registrado |
| 2 — Arquitetura e conteúdo | **Concluída em 28/09/2026** | Home e Empresa reposicionadas; página “Como ajudamos”; Conteúdo na navegação; quatro ofertas com problema, entregáveis, processo, contratação e CTA contextual; processo e métricas comerciais registrados | nenhuma pendência do gate |
| 3 — Visual e componentes | **Concluída em 29/09/2026** | overflow mobile corrigido; 76 estilos inline retirados; animação por classes e movimento reduzido; media queries distribuídas; Stylelint ativado; iconografia vetorial; backup de logo retirado; smoke automatizado | nenhuma pendência do gate |
| 4 — Trabalhe Conosco | **Concluída em 29/09/2026** | cultura e forma de trabalho publicadas; áreas identificadas como interesses, sem simular vagas; ausência de vagas e banco de talentos explícita; FAQ publicado; nenhuma coleta de candidatura; smoke editorial, semântico e responsivo automatizado | nenhuma pendência do gate; eventual banco de talentos continua condicionado à definição do ciclo de vida LGPD |
| 5 — Assistente institucional | **Concluída em 29/09/2026** | Assistente TRCONGROUP, endpoint, base factual e glossário, cliente DeepSeek, escopo institucional/tecnológico sem respostas fixas no fluxo real, validação estruturada, rate limit, orçamento, CTAs, observabilidade, stub exclusivamente local, interface responsiva e integração real homologada no Coolify dev | nenhuma pendência de implementação; redeploy da revisão atual, smoke e ativação em produção pertencem à Etapa 9 |
| 6 — Limpeza frontend/pipeline | **Concluída em 29/09/2026** | consumidores financeiros e artefatos financeiros removidos; Radar preservado; `news-log.json`, builder, testes e referências do workflow retirados; fontes canônicas sincronizadas; pipeline e frontend validados; publicação e observação concluídas | nenhuma pendência do gate |
| 7 — Backend financeiro | **Concluída em 30/09/2026** | produtor retirado do Sírius Marketing; pacotes, endpoints e testes de `economytips` removidos do site; scheduler preserva somente notícias; documentação funcional atualizada; backend aprovado com 192 testes; migration V11 criada | execução da V11, backup e validação operacional pertencem à Etapa 9 |
| 8 — Higiene do repositório | Parcial | regra de ignore criada; bytecode marcado para remoção; backup antigo do logo retirado | concluir remoção versionada dos caches; eliminar referências e documentação obsoletas |
| 9 — Verificação e publicação | Parcial local/dev | lint, frontend, pipeline e testes do chat executados; smoke visual local; integração com DeepSeek observada no endpoint público dev | republicar a revisão atual do backend no Coolify, executar smoke funcional, completar SEO/acessibilidade, deploy gradual e observação em produção |

### Ponto de retomada obrigatório

O próximo trabalho não deve avançar para novas funcionalidades fora da sequência. Deve:

1. concluir a Etapa 8 com a remoção dos caches e das referências obsoletas ainda
   identificadas.

Enquanto esse gate não for atendido, o estado do plano é **em execução**,
não “implementado por completo”.

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

### 3. Implantar a nova arquitetura de navegação — concluído na Etapa 2

- Home
- Empresa
- Soluções
- Produtos
- Conteúdo
- Trabalhe Conosco
- Contato

A navegação alvo foi implantada. A página “Como ajudamos” substituiu o destino
antigo e `#clientes` permanece temporariamente como alias compatível.

### 4. Reposicionar Home e Empresa — concluído na Etapa 2

- os 21 anos e a evolução tecnológica atual foram publicados;
- IA, desenvolvimento sob demanda, customização e outsourcing passaram a compor
  a atuação atual;
- a narrativa predominantemente financeira e os elementos de mercado saíram da
  Home;
- os pilares foram substituídos por Engenharia de Software, IA e Automação,
  Produtos Digitais e Squads/Profissionais.

### 5. Transformar Serviços em ofertas comerciais — concluído na Etapa 2

- Diagnóstico de IA e automação;
- Desenvolvimento de MVP ou produto;
- Modernização e customização;
- Outsourcing por profissional, célula ou squad.

Cada oferta apresenta público, problema, entregáveis, processo, modelo de
contratação e CTA próprio.

### 6. Melhorar a conversão — concluído na Etapa 2

- CTAs implantados: “Solicitar diagnóstico”, “Falar sobre um projeto”, “Receber
  uma proposta” e “Montar meu time”;
- `origem` e `leadType` preservados no formulário;
- processo interno de qualificação, reunião, proposta e acompanhamento definido;
- métricas de visitas, formulários iniciados, leads qualificados, reuniões,
  propostas e contratos definidas. A instrumentação aguarda aprovação do sistema
  de analytics e da retenção, conforme [23-PROCESSO-COMERCIAL.md](23-PROCESSO-COMERCIAL.md).

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

### Fechamento da Etapa 0 — 28/09/2026

#### Estado protegido do repositório

- branch: `preparar-repositorio-20260928`
- commit de referência: `c3a2325`
- `origin/preparar-repositorio-20260928` estava no mesmo commit no início da verificação
- alterações que já existiam e foram preservadas: este plano modificado e 11
  arquivos `*.pyc` versionados marcados para remoção
- o build alterou somente os carimbos de versão de `index.html`,
  `novidades.html` e `privacidade.html`; esses efeitos foram revertidos após a
  verificação
- os testes Python recriaram os bytecodes; eles foram removidos novamente para
  restaurar o estado de trabalho anterior ao teste

Nenhuma exclusão funcional adicional foi feita durante o fechamento desta etapa.

#### Baseline de testes

| Verificação | Resultado em 28/09/2026 |
|---|---|
| `frontend: npm test` | aprovado — 6 arquivos e 73 testes |
| `frontend: npm run lint` | aprovado |
| `frontend: npm run build` | aprovado — `style.css`, `article.css` e `legal.css` gerados |
| `frontend: python -m unittest discover -s scripts -p "test_*.py"` | aprovado — 18 testes |
| `backend: mvnw.cmd test` | aprovado — 169 testes, sem falhas, erros ou testes ignorados |

Avisos conhecidos do baseline, sem falha da execução:

- npm avisa que a configuração `min-release-age` deixará de ser aceita em uma
  versão principal futura;
- Flyway avisa que o PostgreSQL 18.4 do Testcontainers é mais novo que a versão
  oficialmente validada pela biblioteca, PostgreSQL 17;
- Mockito avisa sobre o carregamento dinâmico do agente em versões futuras do JDK.

#### Baseline visual local

Verificação feita no servidor já ativo em `http://127.0.0.1:4173`, com Edge
headless, após 3 segundos de renderização, nos viewports 1440×900 e 390×844.
Foram inspecionadas 18 capturas temporárias; elas não foram adicionadas ao
repositório.

> A tabela abaixo é o registro histórico anterior às correções das Etapas 1 e 2;
> não descreve o estado atual. O gate atual da Etapa 2 está documentado na seção
> “Evidência do gate”.

| Página/rota | Desktop 1440×900 | Mobile 390×844 |
|---|---|---|
| Home `#home` | navegação e hero carregam; o texto de apoio e os CTAs ficam com contraste/opacidade insuficiente no estado capturado | marca, texto e ações ultrapassam a largura; conteúdo e botão do chat ficam cortados |
| Empresa `#sobre` | primeira dobra carrega e identifica os 21 anos | título e texto ultrapassam a largura e ficam cortados |
| Soluções `#servicos` | primeira dobra carrega e comunica IA, software, modernização e outsourcing | título e texto ultrapassam a largura e ficam cortados |
| Produtos `#produtos` | primeira dobra carrega; o texto ainda apresenta o portfólio como predominantemente financeiro | título e texto ultrapassam a largura e ficam cortados |
| Trabalhe Conosco `#carreiras` | primeira dobra carrega e a rota está na navegação principal | título, texto e botão do chat ultrapassam a largura |
| Clientes `#clientes` | no baseline, não existia página própria e o código convertia o hash para `#carreiras` | registro histórico corrigido na Etapa 2: hoje o alias aponta para `#como-ajudamos` |
| Contato `#contato` | primeira dobra carrega e apresenta o contexto comercial | título, texto e botão do chat ultrapassam a largura |
| Sírius Hub `#hub` | primeira dobra do convite beta carrega | título, texto e botão do chat ultrapassam a largura |
| Sírius Agendamento `#agendamento` | primeira dobra carrega | título, texto e botão do chat ultrapassam a largura |
| Sírius Marketing `#marketing` | primeira dobra carrega | título, texto e botão do chat ultrapassam a largura |

O estouro horizontal mobile e a opacidade da Home são defeitos registrados para
a Etapa 3. O fechamento deste baseline não os classifica como aprovados.

#### Inventário de `economy-tips`

A busca foi feita em todos os projetos locais sob `C:\Trcongroup\projetos`, além
das configurações de desenvolvimento e produção versionadas. O inventário
encontrado é:

| Papel | Componente e evidência | Estado comprovado pelo código |
|---|---|---|
| produtor externo | Sírius Marketing: `ContentPublisher.publishToEconomyTips` chama `SiteContentClient.publishEconomyTip` para `NEWSLETTER` e `LANDING_PAGE` | ativo no código |
| configuração do produtor | Sírius Marketing: `application-prod.yml`, `.env.production.example` e compose do Coolify usam `APP_SITE_PUBLISH_ENABLED`, `APP_SITE_API_URL` e `APP_SITE_INTERNAL_API_KEY` | perfil de produção tem publicação habilitada por padrão e aponta para `https://api-site.trcongroup.com.br` no exemplo versionado |
| receptor interno | site backend: `POST /api/internal/economy-tips` | ativo e protegido pela chave interna compartilhada |
| persistência | entidade/repositório `EconomyTip`; migrations V6 e V9 | ativo; tabela e índice ainda fazem parte do schema |
| leitura pública | site backend: `GET /api/public/economy-tips` | ativo no backend |
| consumidor interno | `ContentExpiryScheduler` usa `EconomyTipRepository` | ativo |
| consumidor frontend | nenhum no frontend atual; URL, carregamento e renderização foram retirados no commit `c3a2325` | removido |
| pipeline do site | workflow atual não gera `economy-tips.json`; scripts e builders correspondentes foram retirados | removido |
| outros projetos locais | nenhuma referência executável encontrada no Sírius Hub Financeiro ou no Sírius Agendamento | nenhum consumidor identificado |

O repositório comprova a configuração do produtor, mas não comprova o valor real
das variáveis no painel do Coolify nem o tráfego atual de produção. Portanto, o
produtor é tratado como **ativo** até ser alterado no Sírius Marketing e validado
em produção. O backend, o scheduler, as migrations e os testes de
`economy-tips` não estão autorizados para remoção antes disso.

#### Destino do conteúdo financeiro

| Classificação | Conteúdo | Decisão registrada |
|---|---|---|
| manter no contexto do Sírius Hub | controle de receitas e despesas, visão mensal, metas, programação financeira, galeria do produto e convite beta | permanece nas rotas e cards do produto; não volta para a Home institucional |
| revisar e centralizar no produto | Market AI, análise de humor, cotações, recomendações, BI financeiro e textos que apresentam todo o portfólio como financeiro | a Etapa 2 retirou alegações não comprovadas e manteve conteúdo financeiro somente no contexto real do Sírius Hub; qualquer capacidade nova exige aprovação factual |
| arquivar | antigos payloads `market.json`, `economy-tips.json` e `recipes.json`, além do conteúdo gerado por seus pipelines | removidos da árvore atual e preservados no histórico Git anterior a `c3a2325`; não restaurar na Home |
| excluir do site institucional | ticker, tabela de ativos, humor de mercado, recomendações, cards de Educação Financeira e receitas econômicas | consumidores e geradores frontend já removidos; CSS órfão deve ser tratado na Etapa 3/8 |
| preservar por dependência | conteúdo publicado pelo Sírius Marketing na tabela `economy_tips` | manter até decidir a migração para notícia/produto ou o arquivamento e desligar o produtor |

#### Gate

O critério de aceite da Etapa 0 está atendido: o baseline está documentado, as
alterações anteriores foram preservadas, os testes são conhecidos e todos os
componentes locais associados a `economy-tips` foram identificados antes de
qualquer remoção do backend. A Etapa 0 está **concluída**. O próximo gate formal é
o da Etapa 1.

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

### Fechamento da Etapa 1 — 28/09/2026

#### Fonte canônica e responsáveis

`doc/canonical/01-POSICIONAMENTO-INSTITUCIONAL.md` passou a ser o registro
explícito dos fatos autorizados, inicialmente com 12 IDs estáveis espelhados em
`backend/src/main/resources/chat/trcon-knowledge.yml`. Na conclusão da Etapa 5,
foram acrescentados cinco IDs `glossary.*`, totalizando 17 sem alterar os IDs
anteriores.

- aprovação institucional: Direção da TRCONGROUP
- manutenção técnica: responsável técnico do site
- divulgações de privacidade: DPO identificado na Política de Privacidade
- revisão: trimestral e também imediata quando mudar produto, situação comercial,
  case publicado, vaga ou canal de contato
- ordem obrigatória: evidência e aprovação, fonte canônica, YAML e fallbacks,
  conteúdo público, testes e publicação

O histórico Git registra aprovação e reversão. Fatos ausentes usam
`governance.missing`; não podem ser completados por inferência.

#### Auditoria factual concluída

Foram revisados o texto institucional estático de `frontend/index.html`, os
contextos comerciais de `frontend/assets/app.js`, a base e o prompt do chat, os
fallbacks determinísticos, a página de privacidade e o shell público de Novidades.
Conteúdo externo do Radar continua identificado como curadoria externa e artigos
dinâmicos continuam sujeitos ao fluxo editorial próprio.

| Tema | Decisão aplicada |
|---|---|
| identidade | preservados 21 anos e foco tecnológico atual; retirada a data derivada “desde 2005” |
| ofertas | preservadas as quatro linhas aprovadas; garantias universais de cobertura, infraestrutura e composição de time foram substituídas por definições por contratação |
| produtos | lista pública restrita a Sírius Hub, Sírius Agendamento e Sírius Marketing, com estados explícitos |
| alegações retiradas | Market AI, BI & Analytics e IA generativa/produção de conteúdo como serviço ativo deixaram de ser apresentados como ofertas publicadas |
| beta do Hub | retiradas promessas sem aprovação registrada de 90 dias, gratuidade, desconto vitalício e vagas limitadas; o CTA agora registra interesse |
| comercial | ausência de cases publicados e estado sem contratos ativos registrados na fonte; o segundo fato só é informado quando perguntado diretamente |
| carreiras | mantidos “sem vagas abertas” e “sem banco de talentos”; áreas de interesse não são apresentadas como vagas |
| privacidade | fatos legais e operacionais permanecem sob governança específica do DPO e não são inferidos pelo assistente institucional |

#### Contrato e verificações

- o backend rejeita IDs duplicados e falha na inicialização se faltar qualquer ID
  obrigatório da base factual
- o stub do chat cita IDs compatíveis com cada afirmação e agora responde os três
  estados de produto sem criar condições comerciais
- `frontend/tests/institutional-facts.test.js` compara os IDs do documento e do
  YAML e impede o retorno das alegações retiradas
- testes focados executados no fechamento: 3 testes do contrato factual frontend e
  7 testes do provider/serviço do chat, todos aprovados
- suíte completa do frontend: 7 arquivos e 76 testes aprovados
- suíte completa do backend: 171 testes aprovados, sem falhas, erros ou ignorados
- `npm run lint` e `npm run build` aprovados; bundles CSS reproduzidos; os
  carimbos de versão alterados pelo build foram restaurados ao estado protegido
  porque não fazem parte da Etapa 1

Com isso, os três critérios de aceite da Etapa 1 estão atendidos. Mudanças futuras
de fatos institucionais devem seguir a governança registrada antes da publicação.

## Etapa 2 — Arquitetura de informação e conteúdo

**Estado: concluída em 28/09/2026.**

### Resultado implementado

| Frente | Estado entregue |
|---|---|
| Home | hero comunica software, IA e times de tecnologia; pilares são Engenharia de Software, IA e Automação, Produtos Digitais e Squads/Outsourcing; existe o fluxo “Como trabalhamos”; conteúdo financeiro não voltou à página |
| Empresa | comunica 21 anos e a evolução tecnológica atual; apresenta engenharia, qualidade, transparência e IA sem inventar origem histórica, clientes ou métricas |
| Navegação | contém Home, Empresa, Soluções, Produtos, Conteúdo, Trabalhe Conosco e Contato; desktop, mobile e rodapé possuem os destinos aplicáveis |
| Conteúdo | `#conteudo` concentra Radar de IA/Tecnologia e Novidades institucionais; os blocos saíram da Home sem reintroduzir notícia externa como novidade própria |
| Como ajudamos | `#como-ajudamos` orienta empresas que precisam construir, modernizar, aplicar IA ou ampliar o time |
| Compatibilidade | `#clientes` é alias temporário de `#como-ajudamos`; o redirecionamento incorreto para Carreiras foi removido |
| Soluções | quatro ofertas apresentam público, problema, entregáveis, processo, formato de contratação e CTA específico |
| Conversão | CTAs usam “Solicitar diagnóstico”, “Falar sobre um projeto”, “Receber uma proposta” e “Montar meu time”; `tipoInteresse` e uma `origem` própria seguem até o formulário |
| Operação comercial | resposta, qualificação, reunião, proposta, acompanhamento, estados, responsabilidades e métricas internas estão definidos em [23-PROCESSO-COMERCIAL.md](23-PROCESSO-COMERCIAL.md) |

### Ofertas e contexto do lead

| Oferta | CTA | `origem` | `tipoInteresse` |
|---|---|---|---|
| Diagnóstico de IA e automação | Solicitar diagnóstico | `site-trcon-diagnostico-ia` | `DESENVOLVIMENTO_SOB_DEMANDA` |
| MVP ou produto sob demanda | Falar sobre um projeto | `site-trcon-oferta-mvp` | `DESENVOLVIMENTO_SOB_DEMANDA` |
| Modernização e customização | Receber uma proposta | `site-trcon-oferta-modernizacao` | `CUSTOMIZACAO` |
| Outsourcing por profissional, célula ou squad | Montar meu time | `site-trcon-oferta-outsourcing` | `ALOCACAO_MAO_DE_OBRA` |

Prazos, preços, métricas de resultado, clientes e cases não foram publicados sem
registro factual. Contratos concluídos somente podem virar cases com autorização
e dados verificáveis.

### Evidência do gate

- primeira dobra identificada como empresa de tecnologia nas rotas alteradas;
- finanças restritas ao contexto do produto Sírius Hub;
- nenhuma prova social simulada introduzida;
- todas as rotas usadas por `data-page-link` possuem uma página correspondente e
  não existem IDs HTML duplicados;
- teste de contrato editorial cobre arquitetura, quatro ofertas, campos
  obrigatórios, origens, funil e métricas;
- 81 testes frontend, lint, build e `check:css` aprovados;
- smoke visual desktop das rotas `#conteudo`, `#como-ajudamos` e `#servicos`
  aprovado.

O estouro horizontal reproduzido em 390×844 não reabre a Etapa 2: é dívida visual
já atribuída à Etapa 3 e constitui o próximo ponto de execução.

## Etapa 3 — Evolução visual e componentes

**Estado: concluída em 29/09/2026.** O gate foi fechado com a modularização
responsiva, a retirada integral dos estilos inline, a padronização da iconografia,
a validação automática do CSS e o smoke de layout desktop/mobile.

### Diagnóstico CSS atual

Diagnóstico recalculado depois da conclusão da Etapa 2. `frontend/style.css` é um
bundle gerado; a dívida restante está nos módulos-fonte, nos estilos inline e na
responsividade:

| Medida em 28/09/2026 | Resultado |
|---|---:|
| Linhas do bundle `style.css` | 2.008 |
| Tamanho do bundle `style.css` | 59.527 bytes |
| Módulos CSS fonte | 21 |
| Maior módulo fonte | `home-hero.css`, 390 linhas |
| Blocos `@media` no bundle | 11 |
| Animações `@keyframes` no bundle | 12 |
| Usos de `!important` | 0 |
| Atributos `style` no `index.html` | 76 |
| CSS embutido em `privacidade.html` | 0 linhas |

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

Os 76 atributos `style` atualmente presentes no `index.html` devem ser
classificados:

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
8. Limpeza financeira da Etapa 6 nos módulos já separados — concluída.
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

### Fechamento da Etapa 3 — 29/09/2026

#### CSS, componentes e movimento

- os 76 atributos `style` do `index.html` foram substituídos por classes
  semânticas de componente, modificador ou layout;
- o `IntersectionObserver` deixou de escrever `opacity`, `transform` e
  `transition` diretamente e passou a controlar `.reveal` e `.is-visible`;
- posições e duração realmente dinâmicas dos nós decorativos usam custom
  properties validadas, e os canvas ocupam o contêiner pelo CSS;
- `prefers-reduced-motion: reduce` desativa movimento, transições e rolagem suave;
- o arquivo global `responsive.css` foi retirado e suas regras foram movidas para
  os módulos responsáveis; `home-hero-responsive.css` mantém a regra da Home
  separada sem ultrapassar o orçamento de tamanho;
- grades passaram a aceitar a largura disponível e títulos mobile receberam
  limites tipográficos próprios, eliminando o estouro horizontal;
- cards de capacidades, ofertas e carreiras usam o sprite vetorial
  `assets/brand/technology-icons.svg` no lugar de emojis;
- `.market-disclaimer` foi renomeada para `.context-note` e o seletor órfão
  `.insight-card.featured` foi eliminado;
- `trcon-logo.old-backup.png`, sem consumidor, foi retirado dos assets publicados.

#### Verificação automática

- Stylelint 16 foi adicionado com regras graduais para sintaxe, propriedades,
  unidades, seletores e declarações duplicadas;
- `npm run check:css` verifica bundles e executa o Stylelint;
- o ESLint também cobre os scripts JavaScript/MJS de build e smoke;
- `npm run smoke:layout` inicia o servidor local quando necessário, abre um
  navegador Chromium e valida página ativa, largura do documento e erros críticos;
- o smoke passou em 22 combinações: 11 rotas em 1440×900 e 390×844, todas sem
  overflow horizontal e sem erro crítico do frontend;
- quando o backend local está desligado, as requisições de API indisponíveis são
  classificadas separadamente; os fallbacks do site funcionam sem exceção de
  JavaScript.

| Verificação | Resultado em 29/09/2026 |
|---|---|
| `npm run build` | aprovado; bundles regenerados e cache busting atualizado |
| `npm run check:css` | aprovado; bundle reproduzível e Stylelint verde |
| `npm run lint` | aprovado, incluindo `scripts/**/*.{js,mjs}` |
| `npm test` | aprovado — 7 arquivos e 82 testes |
| `npm run smoke:layout` | aprovado — 22 verificações, zero overflow e zero erro crítico |

#### Orçamento dos bundles

| Bundle | Bruto | Gzip |
|---|---:|---:|
| `style.css` | 62.187 bytes | 11.842 bytes |
| `article.css` | 8.906 bytes | 2.645 bytes |
| `legal.css` | 9.922 bytes | 2.852 bytes |

### Auditoria estrutural após o assistente — 29/09/2026

- `style.css` permanece um bundle público gerado. Suas 2.211 linhas não representam
  um retorno ao CSS monolítico: a fonte editável está dividida em 24 módulos sob
  `styles/` e o arquivo começa com o aviso `GENERATED FILE`;
- o CSS do assistente saiu de `components/floating-actions.css` e passou para
  `components/chat.css`, conforme a arquitetura prevista nesta etapa;
- os nós decorativos da Home saíram de `pages/home-hero.css` e passaram para
  `components/home-data-nodes.css`; `home-hero.css` ficou com 382 linhas;
- `build-css.mjs` agora falha se um módulo listado ultrapassar 400 linhas, tornando
  o orçamento arquitetural verificável em vez de apenas documental.

O `index.html` continua com 1.340 linhas. O plano original fatiou o CSS, mas não
definiu fragmentação da fonte HTML; portanto, o HTML não deve ser descrito como
modularizado. Se essa dívida for priorizada, a solução compatível com o site
estático é composição em build: fragmentos-fonte por página/componente geram um
único `index.html` publicável. Não usar `fetch` de fragmentos no navegador, pois
isso prejudicaria primeiro carregamento, SEO e robustez. A implementação precisa
integrar geração, carimbo de versão, Docker, CI e smoke antes de substituir a
fonte atual.

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

### Fechamento da Etapa 4 — 29/09/2026

#### Conteúdo e limites factuais

- a página apresenta a trajetória de 21 anos, a cultura técnica e quatro
  princípios de trabalho: entendimento do contexto, engenharia responsável,
  colaboração clara e evolução em ciclos;
- as seis áreas aprovadas permanecem publicadas como áreas de interesse e o texto
  declara que elas não representam posições abertas;
- o estado atual informa em destaque que não há vagas abertas nem banco de
  talentos disponível;
- cinco perguntas frequentes esclarecem vagas, banco de talentos, áreas,
  divulgação de oportunidades e a separação do formulário comercial;
- a página não contém formulário, campo de arquivo ou qualquer coleta de dados de
  candidatura. Um banco de talentos futuro continua condicionado à especificação
  de consentimento, retenção, exclusão, proteção e auditoria.

#### Acessibilidade e verificação

- as seções de cultura, áreas, oportunidades e perguntas frequentes possuem nomes
  programáticos; a página mantém um único `h1` e ordem válida de títulos;
- o FAQ usa `details` e `summary`, com operação nativa por teclado e foco visível;
- o smoke verifica, em desktop e mobile, os avisos de vagas e banco de talentos,
  os cinco controles de FAQ, a ausência de coleta, links nomeados, seções
  identificadas, hierarquia de títulos, overflow e erros críticos do navegador;
- o callback de carreira do assistente continua direcionando para
  `#page-carreiras`, separado do formulário comercial.

| Verificação | Resultado em 29/09/2026 |
|---|---|
| `npm run build` | aprovado; `style.css` regenerado com o módulo `pages/careers.css` e cache busting atualizado |
| `npm run check:css` | aprovado; bundle reproduzível e Stylelint verde |
| `npm run lint` | aprovado |
| `npm test` | aprovado — 7 arquivos e 83 testes |
| `npm run smoke:layout` | aprovado — 22 verificações, sem overflow, falha editorial/semântica ou erro crítico |

## Etapa 5 — Assistente institucional

**Estado: concluída em 29/09/2026.** O módulo foi validado localmente com Spring
Boot, PostgreSQL em Testcontainers, servidor HTTP DeepSeek simulado e casos
adversariais determinísticos. A integração real também foi homologada pelo
endpoint público do Coolify dev, com respostas do provedor e contabilização do
uso. A publicação da revisão mais recente e a ativação em produção permanecem na
Etapa 9, sem reabrir o gate de implementação desta etapa.

O desenho final evita transformar o assistente em uma árvore de frases prontas.
No fluxo real, o modelo interpreta semanticamente perguntas sobre a TRCONGROUP e
tecnologia. Afirmações específicas sobre a empresa continuam exigindo IDs da base
autorizada; explicações tecnológicas gerais usam `generalTechnology=true` e não
podem ser apresentadas como experiência, oferta ou compromisso da TRCONGROUP sem
fonte institucional. O stub com respostas determinísticas existe somente para
desenvolvimento local sem custo e deve permanecer desligado no Coolify.

Executar a especificação de [21-CHAT-IA-DEEPSEEK.md](21-CHAT-IA-DEEPSEEK.md):

1. implementar base factual YAML, glossário tecnológico e validador
2. implementar cliente DeepSeek somente no backend
3. exigir JSON estruturado e `sourceIds` válidos
4. aplicar rate limit, orçamento e fallback
5. implementar CTAs separados para contato e carreira
6. testar desconhecimento, escopo institucional/tecnológico e prompt injection
7. publicar o aviso de processamento por terceiro
8. apresentar o componente como “Assistente TRCONGROUP”, com identidade visual
   oficial e finalidade “Tecnologia e soluções”

### Critério de aceite

- nenhuma afirmação específica sobre a TRCONGROUP sem fonte válida chega ao navegador
- conceitos tecnológicos relacionados ao escopo são explicados sem cair no
  fallback de conhecimento institucional ausente
- perguntas sem relação razoável com a empresa ou tecnologia são recusadas
- clientes, cases, contratos, vagas e história não são inventados
- indisponibilidade do provedor não quebra o site

### Evidência de conclusão do gate — 29/09/2026

| Verificação | Resultado |
|---|---|
| cliente HTTP DeepSeek simulado | aprovado — autorização, JSON mode, tokens e erro seguro |
| endpoint integrado | aprovado — 200, 400, rate limit 429 e orçamento 429 |
| casos adversariais determinísticos | aprovado — fonte inventada, JSON inválido, truncamento, flags incoerentes, escopo, tecnologia geral e desconhecimento |
| backend `mvnw.cmd -B clean verify` | aprovado — 194 testes, zero falhas; pacote `chat` com 94,41% de linhas e 88,00% de branches; gate global JaCoCo de 80% atendido |
| módulo atual `mvnw.cmd -B "-Dtest=br.com.trcon.site.chat.**" test` | aprovado — 33 testes, zero falhas/erros e 1 teste live opt-in ignorado por desenho |
| frontend `npm test`, `npm run lint`, `npm run check:css` | aprovado — 86 testes, ESLint, bundle reproduzível e Stylelint verdes |
| smoke visual responsivo | aprovado — 22 verificações em 11 rotas, desktop/mobile, sem overflow ou erro crítico |
| configuração Docker | aprovada por `docker compose --env-file .env.example config --quiet` |
| DeepSeek real no Coolify dev | aprovado — endpoint público respondeu por meio do provedor real durante a homologação |
| revisão atualmente publicada no dev | desatualizada — ainda usa o escopo institucional anterior; republicação e smoke da revisão atual ficam registrados na Etapa 9 |
| produção | responsabilidade da Etapa 9 — publicar, executar smoke funcional e observar rate limit, orçamento e logs |

#### Fechamento

O critério de aceite da Etapa 5 está atendido na revisão atual do código. A etapa
está concluída; a publicação dessa revisão no ambiente dev e a homologação final
em produção permanecem no gate operacional da Etapa 9.

## Etapa 6 — Limpeza do frontend e do pipeline

**Estado: concluída em 29/09/2026.** A nova Home e a arquitetura da Etapa 2
satisfazem a pré-condição editorial. Consumidores e geradores financeiros foram
retirados, e o pipeline deixou de gerar `news-log.json` a partir dos radares
externos. O frontend usa somente a API institucional para Novidades: se ela
estiver vazia ou indisponível, a seção fica oculta. O pipeline e o frontend foram
validados, publicados e observados no ambiente implantado. A limpeza CSS residual
foi concluída na Etapa 3.

### HTML retirado ou substituído

Em `frontend/index.html`:

- `#ticker`, `.ticker-wrap`, `#section-market`, `#block-economy-tips` e
  `#section-economy-tips` foram retirados;
- `#page-clientes` não existe; `#como-ajudamos` é a página corporativa e
  `#clientes` permanece apenas como alias de compatibilidade;
- páginas e imagens do Sírius Hub que explicam o produto foram preservadas.

### JavaScript retirado

Em `frontend/assets/app.js`:

- `renderTicker`, `renderMarket`, `renderTips` e `renderRecipes` foram removidos;
- carregamentos de `market.json`, `economy-tips.json` e `recipes.json` foram
  removidos;
- imports sem uso e testes exclusivos foram removidos.

O antigo `recipes.json` era código morto e não existe mais na árvore atual.

Em `frontend/assets/modules/content.js`:

- `loadEconomyTips` e `normalizeTitleKey` foram removidos
- `fetchWithFallback` e `fetchRadarHighlights` foram preservados para o Radar; a
  renderização de Novidades permanece, agora alimentada somente pela API
  institucional

Em `frontend/assets/modules/sanitize.js`, a busca de referências foi concluída e
as funções sem consumidor foram removidas:

- `changeClass`
- `safePercent`
- `safeCssColor`
- `safeGradient`

Os testes exclusivos dessas funções também foram retirados. Funções genéricas
ainda usadas, como `escapeHtml`, `safeUrl` e `safeClass`, permanecem.

### CSS residual — concluído na Etapa 3

Os blocos de ticker, mercado e receitas já estavam retirados. No fechamento da
Etapa 3, `.insight-card.featured` foi removido junto do antigo `responsive.css`, e
a nota ainda necessária em Soluções passou de `.market-disclaimer` para a classe
genérica `.context-note`. A busca atual não encontra os seletores antigos.

### Dados estáticos retirados

`frontend/data/market.json`, `frontend/data/economy-tips.json` e
`frontend/data/recipes.json` foram removidos depois da retirada dos consumidores.

Preservar:

- `ai-radar.json`
- `tech-radar.json`
- `home-highlights.json`

`news-log.json` foi retirado. Novidades usa somente a API institucional; se ela
estiver vazia ou indisponível, a seção é ocultada. Notícia externa não é usada
como fallback nem apresentada como novidade da empresa.

### Scripts Python retirados

Foram removidos `update_market.py`, `update_economy_tips.py`,
`update_daily_content.py`, `economy_tips_builder.py` e
`economy_tips_fallback.py`, além dos testes exclusivos desses fluxos.

`core/`, `providers/`, `radar_builder.py`, `update_ai_radar.py` e
`update_tech_radar.py` permanecem porque alimentam o Radar tecnológico. O README
do pipeline descreve somente os três artefatos ainda gerados e consumidos.

### GitHub Actions — concluído

Em `.github/workflows/update-content.yml`:

- os passos “Atualizar mercado”, “Atualizar Educação Financeira” e “Atualizar
  receitas econômicas” foram retirados;
- testes do pipeline e geração dos radares foram preservados;
- `build_home_payload.py` gera somente `home-highlights.json`; o builder, os
  testes e a documentação de `news-log.json` foram retirados.

### Configuração do frontend — concluída

`TRCON_ECONOMY_TIPS_API_URL`, `economyTipsApiUrl` e os testes exclusivos dessa
configuração foram removidos.

### Critério de aceite

- nenhuma requisição a arquivos ou endpoints removidos no navegador
- nenhum import, função, seletor ou teste órfão
- pipeline gera apenas arquivos consumidos
- Radar de IA/Tecnologia e Novidades continuam funcionando

### Evidência de conclusão — 29/09/2026

| Verificação | Resultado |
|---|---|
| busca por `news-log.json` e `build_news_log` | nenhuma referência ativa; ocorrências restantes pertencem ao histórico e ao registro desta retirada |
| execução de `build_home_payload.py` | aprovada — 6 destaques consolidados a partir de 2 radares; `news-log.json` não foi recriado |
| pipeline Python | aprovado — 18 testes, zero falhas |
| frontend `npm test` | aprovado — 8 arquivos e 86 testes |
| frontend `npm run lint` | aprovado |
| artefatos em `frontend/data/` | somente `ai-radar.json`, `tech-radar.json` e `home-highlights.json` |
| publicação e observação | concluídas; nenhuma requisição aos artefatos removidos e comportamento de Radar e Novidades preservado |

#### Fechamento

O critério de aceite da Etapa 6 está atendido. O frontend não solicita os
artefatos retirados, o pipeline gera somente arquivos consumidos, o Radar mantém
o fallback tecnológico e Novidades permanece restrita à API institucional. A
O produtor externo foi retirado do código do Sírius Marketing em 30/09/2026.
Newsletter e landing page permanecem somente no produto e não são divulgadas no
site institucional.

## Etapa 7 — Desativação do backend financeiro

O módulo `economytips` não deve ser apagado antes de parar os produtores e
consumidores externos.

### Ordem segura

1. Alterar o Sirius Marketing para não publicar mais em
   `POST /api/internal/economy-tips`; decidir se o conteúdo passa a ser notícia
   institucional ou deixa de ser enviado ao site. (Não será mais divulgado no site.)
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
`V11__drop_economy_tips.sql`, para remover tabela e índices. A migration
deve ser executada primeiro em ambiente de teste. Se o conteúdo precisar ser
preservado para o Sírius Hub, exportá-lo ou migrá-lo antes do `DROP TABLE`.

Como `V10__chat_usage_logs.sql` já existe, a migration desta etapa deve ser
`V11__drop_economy_tips.sql`.

### Backup e rollback definidos

Antes do deploy que executará a V11:

1. gerar um backup em formato custom do PostgreSQL limitado à tabela
   `public.economy_tips`, incluindo estrutura, dados e índices;
2. registrar quantidade de linhas, tamanho do arquivo e SHA-256;
3. validar o arquivo com `pg_restore --list` e uma restauração em banco isolado;
4. armazenar o backup fora do repositório e do container da aplicação.

Rollback operacional:

1. interromper o backend novo;
2. restaurar `public.economy_tips` a partir do backup validado;
3. republicar a revisão anterior do backend;
4. confirmar inicialização, contagem restaurada e ausência de erro no scheduler.

A migration histórica não será editada nem removida. A V11 foi criada, mas sua
execução permanece condicionada ao backup e à validação operacional previstos
para a Etapa 9.

### Fechamento da Etapa 7 — 30/09/2026

- o Sírius Marketing deixou de chamar `/api/internal/economy-tips`; `ARTICLE`
  continua em `/api/internal/news`, enquanto `NEWSLETTER` e `LANDING_PAGE`
  concluem apenas o fluxo interno;
- `backend/.../economytips/`, `backend/.../internal/economytips/` e o IT exclusivo
  foram removidos;
- `ContentExpiryScheduler` mantém apenas a contagem de notícias expiradas;
- `mvnw.cmd -B clean verify` foi aprovado com 192 testes, zero falhas, zero erros,
  um teste live ignorado por desenho e gate JaCoCo atendido;
- `V11__drop_economy_tips.sql` foi criada para remover `economy_tips`, sem alterar
  as migrations históricas V6 e V9;
- por determinação operacional, a V11 não foi executada e nenhuma nova rodada de
  testes, build ou comandos Git foi feita após sua criação;
- backup, execução da migration e smoke no ambiente implantado permanecem no gate
  operacional da Etapa 9.

### Critério de aceite

- nenhum produtor ou consumidor chama os endpoints removidos
- aplicação inicia sem beans de `economytips`
- scheduler e testes passam
- migration nova funciona em banco atualizado desde V1
- rollback de aplicação e recuperação do backup foram definidos

#### Fechamento

O critério de implementação da Etapa 7 está atendido: não há produtor ou
consumidor ativo, o módulo foi removido, o scheduler foi ajustado, o backend foi
validado antes da criação da migration e a V11 está incorporada como próxima
migration da cadeia. A exclusão física dos dados será realizada somente durante
a publicação controlada da Etapa 9, depois do backup previsto nesta seção.

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

`frontend/assets/trcon-logo.old-backup.png` não tinha referência de execução e foi
retirado na Etapa 3 depois da conferência visual do logo atual e da busca de
consumidores. Backups necessários devem ficar no histórico do Git ou em
armazenamento de design, não misturados aos assets publicados.

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
