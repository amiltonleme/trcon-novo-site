# Posicionamento Institucional — TRCONGROUP

> Atualizado em **28/09/2026** — evolução tecnológica, foco comercial e página Trabalhe Conosco.

## Quem é a TRCONGROUP

**TRCONGROUP — Tecnologia, Inteligência e Resultados.**

A TRCONGROUP tem **21 anos de existência** e evoluiu sua atuação para novas
tecnologias, inteligência artificial, desenvolvimento sob demanda, customização e
outsourcing de profissionais de tecnologia. Essa evolução já está em curso há seis
meses e compõe a atuação atual da empresa; não deve ser comunicada como tentativa,
retorno ou preparação futura.

O site deve transformar essa capacidade em oportunidades comerciais: explicar
problemas que a TRCONGROUP resolve, apresentar formas objetivas de contratação e
conduzir empresas interessadas para diagnóstico, proposta e contato.

Empresa de tecnologia que atua em quatro frentes conectadas:

- **Inteligência Artificial** — soluções de IA aplicada a negócio (automação, análise, copilotos internos)
- **Tecnologia** — desenvolvimento, customização e manutenção de software
- **Finanças** — produtos e módulos voltados a controle financeiro, dados de mercado e resultado
- **Resultados** — o critério comum a tudo: cada entrega deve gerar resultado mensurável para o cliente

## O que a TRCONGROUP vende (linhas de negócio)

1. **Venda de software/produto próprio** — produtos prontos (ex.: módulo de fluxo de caixa, radar de mercado) licenciados ou por assinatura.
2. **Desenvolvimento sob demanda** — squads ou projetos fechados para construir software para o cliente.
3. **Customização** — adaptação de produtos existentes (próprios ou do cliente) a necessidades específicas.
4. **Alocação de mão de obra em tecnologia (staffing/bodyshop)** — profissionais de tecnologia (dev, dados, IA, QA) alocados em squads do cliente.

O site institucional precisa deixar essas 4 linhas claras e navegáveis — hoje o site fala de produto (fluxo de caixa, beta), mas não comunica a empresa como prestadora de serviço/staffing. Isso é gap de conteúdo, não só de código.

## Tom de voz

- direto, técnico, sem jargão vazio
- fala com decisor de negócio e com decisor técnico ao mesmo tempo
- demonstra capacidade por produtos próprios, protótipos, arquitetura, processo de
  trabalho e conteúdo técnico verificável
- usa logotipos de clientes, depoimentos, contratos, cases e métricas somente
  depois que existirem e houver autorização para publicação
- confiante sem exagero — empresa de 21 anos com ofertas atuais, objetivas e
  comercialmente acionáveis

## Verdades institucionais obrigatórias

- a empresa tem 21 anos de existência
- a evolução para IA, novas tecnologias, desenvolvimento sob demanda e outsourcing
  já faz parte da atuação atual e está em curso há seis meses
- não apresentar trabalho interno, protótipo ou produto próprio como case de cliente
- não inventar nomes de clientes, segmentos atendidos, projetos entregues,
  depoimentos, certificações, parceiros, equipe, vagas ou números de resultado
- datas, números e marcos históricos além dos 21 anos só podem ser publicados após
  confirmação e registro neste documento

## Estrutura de páginas alvo do site institucional

### Home
- hero institucional: "TRCONGROUP — Tecnologia, Inteligência e Resultados"
- as 4 linhas de negócio em blocos claros (produto, dev sob demanda, customização, alocação)
- demonstrações de capacidade: produtos próprios, protótipos, processo de entrega e conteúdo técnico
- Radar IA / Tecnologia / Mercado (conteúdo recorrente já planejado)
- CTAs comerciais: "Falar sobre um projeto", "Aplicar IA ao meu negócio" e
  "Montar ou ampliar um time"

### Sobre a TRCONGROUP
- 21 anos de existência e evolução contínua da atuação tecnológica
- missão, forma de trabalhar, princípios técnicos (SOLID, qualidade, resultado)
- não é obrigatório expor "IA usada no processo interno" publicamente, mas pode compor diferencial ("construímos com rigor de engenharia e velocidade de execução")
- não publicar narrativa de fundação, clientes anteriores ou marcos históricos que
  ainda não tenham sido confirmados e documentados

### Serviços
- Desenvolvimento sob demanda
- Customização de sistemas
- Alocação de mão de obra em tecnologia (staffing) — com modelo de engajamento (squad dedicado, célula, profissional avulso)
- IA aplicada a negócio

### Produtos
- página existente de produtos, mantida e reforçada narrativamente (ver [08-REDESIGN-DIRETRIZES.md](08-REDESIGN-DIRETRIZES.md))

### Produtos (detalhe)
- páginas de detalhe por produto Sírius (Hub, Agendamento, Marketing), sem formulário embutido
- Hub: status beta; Agendamento e Marketing: em desenvolvimento
- CTAs de cada página levam ao formulário de contato com contexto do produto

### Novidades / Laboratório
- conteúdo recorrente e demonstrações (já previstos no backlog anterior)

### Contato / Waitlist / Fale com um especialista
- formulário único (`#page-contato`) que serve lead de produto e de serviço/staffing, com campo de "interesse" (produto, desenvolvimento, customização, alocação)
- painel contextual conforme origem (`data-product`: hub, agendamento, marketing, servicos)
- identificação do produto no lead via `origem` e prefixo na mensagem (sem campo novo no backend)

### Trabalhe Conosco

- página institucional própria, acessível pela navegação principal e pelo rodapé
- explica cultura técnica, forma de trabalho e áreas profissionais de interesse
- não anuncia vagas abertas quando não houver processo seletivo real
- pode oferecer **banco de talentos**, deixando claro que o cadastro não representa
  vaga, contratação ou prazo de retorno
- perfis de interesse: desenvolvimento back-end, front-end e full-stack, dados/BI,
  IA/ML, QA, produto, UX/UI, DevOps/cloud e gestão de projetos
- se houver coleta de currículo ou dados profissionais, exige consentimento LGPD,
  política de retenção, canal de exclusão e armazenamento protegido; essa coleta não
  deve reutilizar silenciosamente o formulário de lead comercial

## Impacto na arquitetura

- o domínio de **lead/waitlist** deixa de ser só "waitlist de produto beta" e passa a ser um domínio de **Lead comercial** com tipo (`PRODUCT`, `CUSTOM_DEV`, `STAFFING`, `CUSTOMIZATION`) — isso é refletido em [06-BACKEND-MINIMO-ESPECIFICACAO.md](06-BACKEND-MINIMO-ESPECIFICACAO.md)
- conteúdo institucional (serviços, páginas) continua estático/editorial (Camada 1/3 de [02-ARQUITETURA-CANONICA.md](02-ARQUITETURA-CANONICA.md)) — não precisa de banco
- a página Trabalhe Conosco pode ser estática na primeira versão; banco de talentos
  com formulário exige especificação própria de candidato, consentimento e retenção
  antes da implementação

## Conversão comercial

O site precisa gerar conversas qualificadas e propostas. Cada oferta deve ter uma
página ou seção com problema, entrega, forma de contratação e CTA específico:

1. **Diagnóstico de IA e automação** — identificar processos, dados e casos de uso
   com potencial de ganho.
2. **Desenvolvimento sob demanda** — construir MVP, produto ou integração com
   escopo e evolução definidos.
3. **Modernização e customização** — evoluir sistemas, integrações e fluxos já
   existentes.
4. **Outsourcing e squads** — ampliar capacidade com profissional, célula ou time
   dedicado.

O formulário deve preservar a origem e o interesse para que o contato comece com o
contexto correto. Produtos, demonstrações e arquitetura comprovam capacidade até
que contratos reais gerem cases autorizados.

## Critério de pronto do reposicionamento

- site comunica claramente as 4 linhas de negócio
- existe página/seção de Serviços com alocação de mão de obra
- Home e Sobre comunicam os 21 anos e a evolução tecnológica como atuação atual
- não existem cases, logotipos, depoimentos ou métricas comerciais não comprovados
- existe página Trabalhe Conosco com estado real de vagas e, se aplicável, banco de talentos com aviso claro
- formulário de contato captura o tipo de interesse
- ofertas e CTAs conduzem a diagnóstico, proposta ou composição de time
- identidade visual (logo, fundo, paleta) preservada conforme [08-REDESIGN-DIRETRIZES.md](08-REDESIGN-DIRETRIZES.md)
