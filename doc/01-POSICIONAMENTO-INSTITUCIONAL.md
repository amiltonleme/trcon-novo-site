# Posicionamento Institucional — TRCon

## Quem é a TRCon

**TRCon — Tecnologia, Inteligência e Resultados.**

Empresa de tecnologia que atua em quatro frentes conectadas:

- **Inteligência Artificial** — soluções de IA aplicada a negócio (automação, análise, copilotos internos)
- **Tecnologia** — desenvolvimento, customização e manutenção de software
- **Finanças** — produtos e módulos voltados a controle financeiro, dados de mercado e resultado
- **Resultados** — o critério comum a tudo: cada entrega deve gerar resultado mensurável para o cliente

## O que a TRCon vende (linhas de negócio)

1. **Venda de software/produto próprio** — produtos prontos (ex.: módulo de fluxo de caixa, radar de mercado) licenciados ou por assinatura.
2. **Desenvolvimento sob demanda** — squads ou projetos fechados para construir software para o cliente.
3. **Customização** — adaptação de produtos existentes (próprios ou do cliente) a necessidades específicas.
4. **Alocação de mão de obra em tecnologia (staffing/bodyshop)** — profissionais de tecnologia (dev, dados, IA, QA) alocados em squads do cliente.

O site institucional precisa deixar essas 4 linhas claras e navegáveis — hoje o site fala de produto (fluxo de caixa, beta), mas não comunica a empresa como prestadora de serviço/staffing. Isso é gap de conteúdo, não só de código.

## Tom de voz

- direto, técnico, sem jargão vazio
- fala com decisor de negócio e com decisor técnico ao mesmo tempo
- prova por resultado (números, cases, clareza de escopo), não por adjetivo
- confiante sem ser exagerado — porte de empresa séria, não de startup em validação

## Estrutura de páginas alvo do site institucional

### Home
- hero institucional: "TRCon — Tecnologia, Inteligência e Resultados"
- as 4 linhas de negócio em blocos claros (produto, dev sob demanda, customização, alocação)
- prova social / diferenciais
- Radar IA / Tecnologia / Mercado (conteúdo recorrente já planejado)
- CTA dupla: "Quero um produto" vs "Quero um time/serviço"

### Sobre a TRCon
- missão, forma de trabalhar, princípios técnicos (SOLID, qualidade, resultado)
- não é obrigatório expor "IA usada no processo interno" publicamente, mas pode compor diferencial ("construímos com rigor de engenharia e velocidade de execução")

### Serviços
- Desenvolvimento sob demanda
- Customização de sistemas
- Alocação de mão de obra em tecnologia (staffing) — com modelo de engajamento (squad dedicado, célula, profissional avulso)
- IA aplicada a negócio

### Produtos
- página existente de produtos, mantida e reforçada narrativamente (ver [08-REDESIGN-DIRETRIZES.md](./08-REDESIGN-DIRETRIZES.md))

### Beta
- página de conversão para produto em beta (já prevista no backlog anterior)

### Novidades / Laboratório
- conteúdo recorrente e demonstrações (já previstos no backlog anterior)

### Contato / Waitlist / Fale com um especialista
- formulário único que serve tanto lead de produto quanto lead de serviço/staffing, com campo de "interesse" (produto, desenvolvimento, customização, alocação)

## Impacto na arquitetura

- o domínio de **lead/waitlist** deixa de ser só "waitlist de produto beta" e passa a ser um domínio de **Lead comercial** com tipo (`PRODUCT`, `CUSTOM_DEV`, `STAFFING`, `CUSTOMIZATION`) — isso é refletido em [06-BACKEND-MINIMO-ESPECIFICACAO.md](./06-BACKEND-MINIMO-ESPECIFICACAO.md)
- conteúdo institucional (serviços, páginas) continua estático/editorial (Camada 1/3 de [02-ARQUITETURA-CANONICA.md](./02-ARQUITETURA-CANONICA.md)) — não precisa de banco

## Critério de pronto do reposicionamento

- site comunica claramente as 4 linhas de negócio
- existe página/seção de Serviços com alocação de mão de obra
- formulário de contato captura o tipo de interesse
- identidade visual (logo, fundo, paleta) preservada conforme [08-REDESIGN-DIRETRIZES.md](./08-REDESIGN-DIRETRIZES.md)
