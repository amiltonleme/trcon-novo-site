# Manual editorial — Sirius Marketing (resumo site)

> Manual completo: [`../../sirius-marketing/projeto/docs/cursor/12_manual_usuario_marketing.md`](../../sirius-marketing/projeto/docs/cursor/12_manual_usuario_marketing.md)  
> **Guia capa / demos / apresentação:** §4.1 do manual completo.  
> Atualizado em **30/07/2026**

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

## Página do artigo (Sprint 8 + Desenho A)

- URL: `https://trcongroup.com.br/novidades/{slug}`
- API: `GET /api/public/news/{slug}` (inclui `coverImageUrl` opcional)
- Capa: hero + `og:image` quando o marketing envia URL HTTPS
- Vídeo: link YouTube/Vimeo em linha do body → iframe
- SEO: meta description + Open Graph na página
- Feeds: `GET /sitemap.xml`, `GET /feed/news.xml`

## Capa, imagens e vídeos (resumo editorial)

| Item | O que é | Como obter |
|------|---------|------------|
| **Capa** | Imagem do topo do artigo + preview no share (`og:image`) | Unsplash / Pexels / Pixabay — copiar URL `https://…` no form do marketing |
| **Vídeo demo** | Telas / uso dos apps TRCON | Gravar tela (OBS, Game Bar, Loom) → YouTube **não listado** → colar link em linha isolada no corpo |
| **Melhor artigo** | Capa + problema + vídeo do app + bullets + CTA | Ver receita completa no manual marketing §4.1 |

Não há upload nesta fase. R2 / IA visual = Desenho B ([`19-DESENHO-MIDIA.md`](./19-DESENHO-MIDIA.md)).

## Dev local

Site frontend `assets/env.js` → APIs em `http://localhost:8081`.  
Publicação marketing → site local; home em `http://127.0.0.1:4173` (**não** abrir `trcongroup.com.br` para testar publish local).

Marketing: `APP_SITE_DEFAULT_URL=http://127.0.0.1:4173` para os links gravados no publish. O frontend local reescreve URLs absolutas de `trcongroup.com.br` para path relativo.

## Correções recentes

| Data | Correção |
|------|----------|
| 30/07/2026 | Desenho A: capa URL + `og:image` + embed YouTube/Vimeo; guia editorial §4.1 |
| 27/07/2026 | Artigos deixam de duplicar no Radar |
| 27/07/2026 | API highlights exclui itens com link `/novidades/` ou `external_id` `-radar` |
| 27/07/2026 | Frontend: `fetchRadarHighlights` — fallback JSON se API só tiver legado editorial |
| 27/07/2026 | Novidades: layout cards (igual Radar) |
| 27/07/2026 | Flyway V7: `slug`, `body`, `meta_*` em `news_items` |
