# Desenho — mídia (imagens e vídeos)

> Atualizado em **30/07/2026**  
> **Documento canônico (completo, com diagramas):**  
> [`../../sirius-marketing/projeto/docs/cursor/13_desenho_midia_imagens_videos.md`](../../sirius-marketing/projeto/docs/cursor/13_desenho_midia_imagens_videos.md)

Custos resumidos: [`17-CUSTOS-S8-MIDIA-IA.md`](./17-CUSTOS-S8-MIDIA-IA.md).

---

## Dois desenhos

| | **A — mínimo (~R$ 0)** | **B — produção** |
|--|------------------------|------------------|
| Status | ✅ **30/07/2026** | ❌ Pendente |
| Capa | URL externa (Unsplash…) | Upload/IA → **Cloudflare R2** |
| Vídeo | Embed YouTube/Vimeo no `body` | Embed (padrão) ou Stream (fase 2) |
| Postgres site | `cover_image_url` (V8) | Idem + opcional `video_embed_url` |
| Site frontend | Hero + `og:image` + iframe | Idem + player se embed dedicado |

## Impacto no site (Desenho A — feito)

1. Flyway **V8**: `news_items.cover_image_url`
2. `InternalNewsCreateRequest.coverImageUrl`
3. Hero em `/novidades/{slug}` + meta `og:image`
4. Sanitize: `safeHttpsImageUrl` + `videoEmbedSrc` (YouTube/Vimeo)

Marketing envia a URL no approve; o site **não** armazena o arquivo.

**Guia editorial (capa, Unsplash, demos dos apps):**  
[`18-MANUAL-MARKETING-EDITORIAL.md`](./18-MANUAL-MARKETING-EDITORIAL.md) e manual completo §4.1.
