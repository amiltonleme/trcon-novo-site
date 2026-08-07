# Lote editorial semanal — impacto no site

> Resumo canônico no marketing: [`../../sirius-marketing/projeto/docs/cursor/16_lote_editorial_semanal.md`](../../sirius-marketing/projeto/docs/cursor/16_lote_editorial_semanal.md)  
> Atualizado em **06/08/2026** · **TTL (L0) implementado**; lote semanal ainda planejado

## O que muda no site

O Sirius Marketing continua publicando via:

| Tipo | API site | Seção |
|------|----------|--------|
| `ARTICLE` | `POST /api/internal/news` | Novidades + `/novidades/{slug}` |
| `NEWSLETTER` | news (Educação) + economy-tips | Educação Financeira + leitura |

**Radar** continua só com pipeline externo — artigos do lote **não** entram no Radar.

**Novo (L0 feito):** expiração automática (§ TTL abaixo). Lote semanal = L1+.

## Volume esperado (quando o lote existir)

| Destino | Ritmo |
|---------|--------|
| Novidades | até **1 artigo/dia** (7/semana) |
| Educação Financeira | até **1 tip editorial/dia** (7/semana) |
| LinkedIn | 2 person + 2 company **/dia** (só marketing; não site) |

Infra site (Neon, CF, Hetzner): **custo incremental ~R$ 0**. Storage texto ~MB/ano. Sem worker novo além de um job diário leve.

## TTL — exclusão / sumiço automático (artigos e newsletter)

Para a home **não** acumular dezenas de cards, cada peça editorial do marketing ganha prazo de visibilidade.

> **Importante:** **4 dias é só o default de fábrica (exemplo).** O valor é **configurável**: 2, 6, 7, 14… ou **permanente**. Não fica hard-coded.

| Item | Valor |
|------|--------|
| **Default de fábrica** | 4 dias (`SITE_CONTENT_TTL_DAYS=4`) — **ADMIN pode mudar** |
| **Por lote / peça** | Campo “Visível no site por N dias” (ex.: 2 ou 6) ou Permanente |
| **Escopo** | `news_items` (Novidades + `/novidades/{slug}`) e `economy_tips` (Educação Financeira) |
| **Fora** | Radar (pipeline), RSS estático externo, leads, LinkedIn |

### Comportamento

1. No publish interno, o site grava `expires_at` com N dias (env → lote → peça).
2. `GET` públicos, home, sitemap e RSS **só listam** itens ainda válidos.
3. Job diário (`ContentExpiryScheduler`): desativa tips vencidos (`active=false`); artigos somem só pelo filtro de `expires_at` (soft).
4. Hard `DELETE` opcional só depois (ex. 90 dias).

**Soft-hide primeiro** (recomendado): some da UI sem apagar histórico.

### Contrato (extensão)

```json
{
  "ttlDays": 6,
  "expiresAt": null
}
```

Exemplos: `2`, `4`, `6`, `0` (permanente).

### Custo

| Recurso | Impacto |
|---------|---------|
| CPU | 1 query/dia |
| Disco | Soft ≈ 0; hard purge libera KB |
| Dev | ~0,5–1 dia (Flyway + filtro + scheduler + testes) |

## Operação editorial (site)

1. Marketing configura **assuntos da semana + brief IA** (editável; seed TRCON só na primeira carga).
2. Marketing gera lote → revisão → agenda aprovados.
3. Scheduler do marketing publica no dia/hora do assunto (com TTL).
4. Home mostra só itens **ativos**; após **N dias** (configurável; fábrica = 4) somem das listas.
5. Detalhe lote/TTL: [`16_lote_editorial_semanal.md`](../../sirius-marketing/projeto/docs/cursor/16_lote_editorial_semanal.md) §2 e §7.6.1.

## Referências

- Passo a passo implementação: [`../../sirius-marketing/projeto/docs/cursor/17_passo_a_passo_lote_editorial.md`](../../sirius-marketing/projeto/docs/cursor/17_passo_a_passo_lote_editorial.md)
- Desenho completo: [`../../sirius-marketing/projeto/docs/cursor/16_lote_editorial_semanal.md`](../../sirius-marketing/projeto/docs/cursor/16_lote_editorial_semanal.md)
- Manual editorial: [`18-MANUAL-MARKETING-EDITORIAL.md`](./18-MANUAL-MARKETING-EDITORIAL.md)
- Custos mídia: [`17-CUSTOS-S8-MIDIA-IA.md`](./17-CUSTOS-S8-MIDIA-IA.md)
- Desenho mídia: [`19-DESENHO-MIDIA.md`](./19-DESENHO-MIDIA.md)

## Histórico

| Data | Alteração |
|------|-----------|
| 06/08/2026 | Assuntos da semana + brief IA configuráveis no marketing (espelho) |
| 06/08/2026 | § TTL: prazo **configurável** (2, 6…); 4 = só default de fábrica |
| 06/08/2026 | § TTL: expiração automática artigos/newsletter (default 4 dias) |
| 06/08/2026 | Documento inicial (espelho do lote) |
