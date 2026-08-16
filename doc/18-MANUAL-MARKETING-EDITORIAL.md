# Manual editorial — Sirius Marketing (resumo site)

> Manual completo: [`../../sirius-marketing/projeto/docs/cursor/12_manual_usuario_marketing.md`](../../sirius-marketing/projeto/docs/cursor/12_manual_usuario_marketing.md)  
> **Guia capa / demos / apresentação:** §4.1 do manual completo.  
> Atualizado em **16/08/2026**

## O que vai para onde

| Tipo marketing | Seção no site `trcongroup.com.br` |
|----------------|-----------------------------------|
| **Artigo** | **Novidades TRCon** apenas (grid de cards + `/novidades/{slug}`) |
| **Newsletter** / **Landing page** | Educação Financeira (card) + leitura `/novidades/{slug}` (fora do grid Novidades) |
| **Post social** | LinkedIn (não site) |

**Radar TRCon** (Sinais de IA e tecnologia) **não** recebe artigos do marketing — só pipeline automático de sinais externos.

No marketing, o editor vê **pré-visualização** do artigo (layout ≈ `/novidades/…`) e do post LinkedIn **antes** de aprovar.

## TTL — visibilidade no site (L0)

Artigos e dicas editoriais **somem da home** após N dias (soft-hide). Default de fábrica: **4 dias**.

| Config | Valor |
|--------|--------|
| Site | `SITE_CONTENT_TTL_DAYS` (env) / `trcon.site.content.ttl-days` |
| Marketing (publish) | `APP_SITE_CONTENT_TTL_DAYS` → payload `ttlDays` |
| Permanente | `ttlDays=0` → `expires_at` null |
| Override por peça | body `ttlDays` / `expiresAt` no `POST /api/internal/news` e economy-tips |

Slug expirado → **404**. Sitemap/RSS usam as mesmas listagens filtradas.

### Smoke TTL

```text
1. POST /api/internal/news com ttlDays=2 e publishedAt antigo → GET /api/public/news não lista; slug 404
2. POST com ttlDays=0 → permanece em listagens
3. Marketing: APP_SITE_CONTENT_TTL_DAYS=4 (default) no publish
```

## Layout na home (ago/2026)

As seções **Radar**, **Novidades** e **Educação Financeira** usam grid de cards e **só aparecem se houver itens**. Sem conteúdo, o bloco inteiro fica oculto — não há “Carregando…” / “Sem novidades…” no HTML inicial (higiene de indexação).

Novidades: link interno `/novidades/{slug}` (mesma aba). Radar: links externos (Google News, etc.).

## Página do artigo (Sprint 8 + Desenho A + SEO SSR)

- URL: `https://trcongroup.com.br/novidades/{slug}`
- **HTML SSR:** `GET /novidades/{slug}` no backend (meta, Open Graph, JSON-LD `NewsArticle`, corpo) — first paint rastreável
- API JSON: `GET /api/public/news/{slug}` (inclui `coverImageUrl` opcional)
- Proxy: frontend nginx/`npm run dev` encaminha `/novidades/` para a API (`SITE_API_UPSTREAM`; local default `:8081`)
- Fallback CSR: `novidades.html` + `article.js` se a API estiver indisponível
- Capa: hero + `og:image` quando o marketing envia URL HTTPS
- Vídeo: link YouTube/Vimeo em linha do body → iframe
- Feeds: `GET /sitemap.xml`, `GET /feed/news.xml`
- `robots.txt` no site institucional

## Capa, imagens e vídeos (resumo editorial)

| Item | O que é | Como obter |
|------|---------|------------|
| **Capa** | Imagem do topo do artigo + preview no share (`og:image`) | Unsplash / Pexels / Pixabay — copiar URL `https://…` no form do marketing |
| **Vídeo demo** | Telas / uso dos apps TRCON | Gravar tela (OBS, Game Bar, Loom) → YouTube **não listado** → colar link em linha isolada no corpo |
| **Melhor artigo** | Capa + problema + vídeo do app + bullets + CTA | Ver receita completa no manual marketing §4.1 |

Não há upload nesta fase. R2 / IA visual = Desenho B ([`19-DESENHO-MIDIA.md`](./19-DESENHO-MIDIA.md)).

## Dev local

Site frontend `assets/env.js` → APIs em `http://localhost:8081`.  
`npm run dev` sobe em `:4173` e faz proxy de `/novidades/*` para `SITE_API_UPSTREAM` (default `http://127.0.0.1:8081`).  
Publicação marketing → site local; home em `http://127.0.0.1:4173` (**não** abrir `trcongroup.com.br` para testar publish local).

Marketing: `APP_SITE_DEFAULT_URL=http://127.0.0.1:4173` para os links gravados no publish. O frontend local reescreve URLs absolutas de `trcongroup.com.br` para path relativo.

## Correções recentes

| Data | Correção |
|------|----------|
| 16/08/2026 | Site **0.8.0**: SSR `/novidades/{slug}` + JSON-LD; home sem placeholders operacionais; `robots.txt`; proxy `SITE_API_UPSTREAM` |
| 06/08/2026 | L0 TTL: `expires_at` em news/economy-tips; env `SITE_CONTENT_TTL_DAYS`; soft-hide home |
| 05/08/2026 | Newsletter/landing: leitura completa em `/novidades/{slug}`; excluídas do grid Novidades (categoria Educacao); upsert economy-tips |
| 30/07/2026 | Desenho A: capa URL + `og:image` + embed YouTube/Vimeo; guia editorial §4.1 |
| 27/07/2026 | Artigos deixam de duplicar no Radar |
| 27/07/2026 | API highlights exclui itens com link `/novidades/` ou `external_id` `-radar` |
| 27/07/2026 | Frontend: `fetchRadarHighlights` — fallback JSON se API só tiver legado editorial |
| 27/07/2026 | Novidades: layout cards (igual Radar) |
| 27/07/2026 | Flyway V7: `slug`, `body`, `meta_*` em `news_items` |
