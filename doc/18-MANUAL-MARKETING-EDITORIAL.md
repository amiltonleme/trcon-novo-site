# Manual editorial — Sirius Marketing (resumo site)

> Manual completo: [`../../sirius-marketing/projeto/docs/cursor/12_manual_usuario_marketing.md`](../../sirius-marketing/projeto/docs/cursor/12_manual_usuario_marketing.md)  
> Atualizado em **27/07/2026**

## O que vai para onde

| Tipo marketing | Seção no site `trcongroup.com.br` |
|----------------|-----------------------------------|
| **Artigo** | **Novidades TRCon** apenas (grid de cards + `/novidades/{slug}`) |
| **Newsletter** / **Landing page** | Educação Financeira |
| **Post social** | LinkedIn (não site) |

**Radar TRCon** (Sinais de IA e tecnologia) **não** recebe artigos do marketing — só pipeline automático de sinais externos.

## Layout na home (jul/2026)

As seções **Radar** e **Novidades** usam o mesmo **grid de cards** (`cards-grid`):

- Tag com categoria/fonte
- Título + resumo
- Link verde com seta →

Novidades: link interno `/novidades/{slug}` (mesma aba). Radar: links externos (Google News, etc.).

## Página do artigo (Sprint 8)

- URL: `https://trcongroup.com.br/novidades/{slug}`
- API: `GET /api/public/news/{slug}`
- SEO: meta description + Open Graph na página
- Feeds: `GET /sitemap.xml`, `GET /feed/news.xml`

## Dev local

Site frontend `assets/env.js` → APIs em `http://localhost:8081`.  
Publicação marketing → site local; home em `http://127.0.0.1:4173`.

## Correções recentes

| Data | Correção |
|------|----------|
| 27/07/2026 | Artigos deixam de duplicar no Radar |
| 27/07/2026 | API highlights exclui itens com link `/novidades/` ou `external_id` `-radar` |
| 27/07/2026 | Frontend: `fetchRadarHighlights` — fallback JSON se API só tiver legado editorial |
| 27/07/2026 | Novidades: layout cards (igual Radar) |
| 27/07/2026 | Flyway V7: `slug`, `body`, `meta_*` em `news_items` |
