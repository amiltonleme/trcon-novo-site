# Processo comercial do site TRCONGROUP

> Registro operacional interno criado em 28/09/2026 para concluir a Etapa 2 do
> plano de reposicionamento. Prazos de resposta não devem ser publicados até que
> a capacidade de atendimento esteja validada pela Direção.

## Contexto capturado

Cada CTA de oferta abre o formulário com `tipoInteresse` e `origem` próprios. A
origem identifica a oferta de entrada; o tipo mantém o contrato atual do backend.

| Oferta | `origem` | `tipoInteresse` |
|---|---|---|
| Diagnóstico de IA e automação | `site-trcon-diagnostico-ia` | `DESENVOLVIMENTO_SOB_DEMANDA` |
| MVP ou produto sob demanda | `site-trcon-oferta-mvp` | `DESENVOLVIMENTO_SOB_DEMANDA` |
| Modernização e customização | `site-trcon-oferta-modernizacao` | `CUSTOMIZACAO` |
| Outsourcing por profissional, célula ou squad | `site-trcon-oferta-outsourcing` | `ALOCACAO_MAO_DE_OBRA` |

## Fluxo de atendimento

1. **Recepção:** registrar a entrada com data, origem, interesse e consentimento.
   O responsável comercial verifica se há dados suficientes para iniciar a
   qualificação.
2. **Qualificação:** confirmar problema, resultado esperado, contexto atual,
   prioridade, partes envolvidas e restrições conhecidas. Contato sem aderência é
   encerrado com motivo registrado; informação ausente gera pedido objetivo de
   complemento.
3. **Reunião de entendimento:** envolver liderança técnica quando a oportunidade
   exigir avaliação de arquitetura, dados, integração, segurança ou composição de
   time. Registrar decisões e pendências.
4. **Proposta:** formalizar escopo, entregáveis, responsabilidades, premissas,
   formato de contratação, prazo e condições. Nenhum preço ou prazo genérico é
   publicado no site.
5. **Acompanhamento:** registrar envio, retornos, revisões e decisão. Proposta
   aceita segue para contratação; recusada ou suspensa recebe motivo e próximo
   passo, se houver.
6. **Aprendizado:** contratos concluídos só viram case público com autorização e
   dados verificáveis. O resultado comercial permanece interno.

## Estados mínimos

`RECEBIDO` → `EM_QUALIFICACAO` → `REUNIAO_REALIZADA` → `PROPOSTA_ENVIADA` →
`CONTRATO_FECHADO` ou `ENCERRADO`.

O estado `ENCERRADO` exige um motivo controlado: sem aderência, sem resposta,
prioridade adiada, proposta recusada ou duplicidade. Os nomes podem ser adaptados
ao CRM quando ele for definido, preservando o significado e a data de transição.

## Métricas internas

As métricas abaixo não são prova social e não devem aparecer no site público:

| Métrica | Definição |
|---|---|
| Visitas por oferta | sessões que acessaram a seção ou página da oferta |
| Formulários iniciados | sessões com primeira interação válida no formulário após um CTA da oferta |
| Contatos recebidos | envios aceitos pelo backend, separados por `origem` |
| Leads qualificados | contatos que avançaram para reunião de entendimento |
| Reuniões | reuniões de entendimento realizadas |
| Propostas | propostas comerciais enviadas |
| Contratos | propostas aceitas e formalizadas |

As taxas são calculadas entre etapas consecutivas e sempre por período e origem.
Eventos de navegação e formulário não devem incluir mensagem, nome, e-mail ou
telefone. O sistema de analytics e a retenção desses eventos precisam ser
aprovados antes da instrumentação em produção.

## Responsabilidades

- atendimento comercial: recepção, qualificação, acompanhamento e integridade do
  funil;
- liderança técnica: viabilidade, escopo, riscos e composição da entrega;
- Direção: aprovação de condições, proposta, contrato e eventual publicação de
  case;
- responsável técnico do site: preservação de `origem`/`tipoInteresse` e futura
  instrumentação sem dados pessoais.

