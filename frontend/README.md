# TRCon Site — Frontend

Frontend público do site TRCon (HTML/CSS/JS vanilla, sem framework de SPA).
Stack e regras completas em `../doc/03-FRONTEND-STACK-CANONICA.md`.

## Status

- **Fase 3–5:** migração, conteúdo institucional e leads → backend concluídos.
- **Sprint 8 (jul/2026):** página `/novidades/{slug}`, artigo completo, OG tags, home com cards editoriais.
- **Radar ≠ Novidades (27/07):** feeds separados; layout unificado em **grid de cards**.

## Estrutura

```text
frontend/
  index.html            # home (SPA por show/hide)
  novidades.html        # template página de artigo
  style.css
  assets/
    app.js              # orquestrador
    env.js              # URLs API por ambiente (dev → :8081)
    modules/
      config.js         # resolveApiConfig
      content.js        # radar/novidades: fetch, render cards
      article.js        # página /novidades/{slug}
      lead-form.js
      sanitize.js
  data/                 # JSON fallback (pipeline)
  tests/modules/        # Vitest
```

## Consumo de conteúdo (Radar / Novidades)

| Seção | API | Fallback JSON | Layout |
|-------|-----|---------------|--------|
| **Radar TRCon** | `GET /api/public/highlights` | `data/home-highlights.json` | `cards-grid` |
| **Novidades TRCon** | `GET /api/public/news` | `data/news-log.json` | `cards-grid` |

Implementação em `assets/modules/content.js`:

- **`fetchWithFallback`** — API → JSON se falha ou lista vazia (news).
- **`fetchRadarHighlights`** — exclui highlights editoriais legados; se API só tiver artigos marketing, cai no JSON do pipeline.
- **`buildHighlightsHtml` / `buildNewsHtml`** — grid de cards compartilhado (`buildCardItemHtml`).
- Novidades com `slug` → link interno `/novidades/{slug}` (mesma aba).

Variáveis: `window.TRCON_HIGHLIGHTS_API_URL`, `TRCON_NEWS_API_URL` (via `env.js`).

## Desenvolvimento

```bash
npm install
npm run dev       # http://127.0.0.1:4173
npm test          # Vitest (58 testes)
```

**Smoke local:** site backend `:8081` + `env.js` apontando localhost. Hard refresh após mudanças.

## Referências

- [`../doc/13-AMBIENTE-LOCAL-TESTES.md`](../doc/13-AMBIENTE-LOCAL-TESTES.md)
- [`../doc/16-PASSO-A-PASSO.md`](../doc/16-PASSO-A-PASSO.md)
- [`../../sirius-marketing/projeto/docs/cursor/12_manual_usuario_marketing.md`](../../sirius-marketing/projeto/docs/cursor/12_manual_usuario_marketing.md)
