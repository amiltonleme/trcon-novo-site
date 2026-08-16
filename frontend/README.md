# TRCon Site — Frontend

Frontend público do site TRCon (HTML/CSS/JS vanilla, sem framework de SPA).
Stack e regras completas em `../doc/03-FRONTEND-STACK-CANONICA.md`.

**Versão:** `0.8.0` (`package.json` / rodapé).

## Status

- **Fase 3–5:** migração, conteúdo institucional e leads → backend concluídos.
- **Sprint 8:** página `/novidades/{slug}`, artigo completo, OG + **JSON-LD**, home com cards editoriais.
- **0.8.0 (16/08/2026):** HTML SSR via proxy `/novidades/` → API; seções editoriais ocultas sem itens; `robots.txt`.
- **Radar ≠ Novidades (27/07):** feeds separados; layout unificado em **grid de cards**.

## Estrutura

```text
frontend/
  index.html            # home (SPA por show/hide)
  novidades.html        # fallback CSR da página de artigo
  robots.txt
  style.css
  nginx.conf.template   # proxy /novidades/ → SITE_API_UPSTREAM
  docker-entrypoint.sh
  assets/
    app.js              # orquestrador (esconde seções vazias)
    env.js              # URLs API por ambiente (dev → :8081)
    modules/
      config.js         # resolveApiConfig
      content.js        # radar/novidades: fetch, render cards
      article.js        # CSR + JSON-LD
      lead-form.js
      sanitize.js
  data/                 # JSON fallback (pipeline)
  tests/modules/        # Vitest
```

## Consumo de conteúdo (Radar / Novidades)

| Seção | API | Fallback JSON | Layout |
|-------|-----|---------------|--------|
| **Radar TRCon** | `GET /api/public/highlights` | `data/home-highlights.json` | `cards-grid` (bloco oculto se vazio) |
| **Novidades TRCon** | `GET /api/public/news` | `data/news-log.json` | `cards-grid` (bloco oculto se vazio) |

Implementação em `assets/modules/content.js`:

- **`fetchWithFallback`** — API → JSON se falha ou lista vazia (news).
- **`fetchRadarHighlights`** — exclui highlights editoriais legados; se API só tiver artigos marketing, cai no JSON do pipeline.
- **`buildHighlightsHtml` / `buildNewsHtml`** — grid de cards; lista vazia → string vazia (seção some).
- Novidades com `slug` → link interno `/novidades/{slug}` (mesma aba).

Artigo: preferir HTML SSR do backend (`GET /novidades/{slug}`); `novidades.html` só como fallback.

Variáveis: `window.TRCON_HIGHLIGHTS_API_URL`, `TRCON_NEWS_API_URL` (via `env.js`).  
Deploy: `SITE_API_UPSTREAM` no Coolify/nginx.

## Desenvolvimento

```bash
npm install
npm run dev       # http://127.0.0.1:4173 (proxy /novidades → :8081)
npm test          # Vitest
npm run build     # carimba versão no rodapé do index.html
```

**Smoke local:** site backend `:8081` + `env.js` apontando localhost. View Source em `/novidades/{slug}`. Hard refresh após mudanças.

## Referências

- [`../doc/13-AMBIENTE-LOCAL-TESTES.md`](../doc/13-AMBIENTE-LOCAL-TESTES.md)
- [`../doc/14-STATUS-IMPLEMENTACAO.md`](../doc/14-STATUS-IMPLEMENTACAO.md)
- [`../doc/16-PASSO-A-PASSO.md`](../doc/16-PASSO-A-PASSO.md)
- [`../doc/18-MANUAL-MARKETING-EDITORIAL.md`](../doc/18-MANUAL-MARKETING-EDITORIAL.md)
- [`../../sirius-marketing/projeto/docs/cursor/12_manual_usuario_marketing.md`](../../sirius-marketing/projeto/docs/cursor/12_manual_usuario_marketing.md)
