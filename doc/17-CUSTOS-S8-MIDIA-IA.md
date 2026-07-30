# Custos — Sprint 8, mídia visual e IA de imagem

> Atualizado em **27/07/2026**  
> **Documento canônico (completo):** [`../../sirius-marketing/projeto/docs/cursor/11_custos_sprint8_midia_ia.md`](../../sirius-marketing/projeto/docs/cursor/11_custos_sprint8_midia_ia.md)  
> Escopo S8 no site: [`16-PASSO-A-PASSO.md`](16-PASSO-A-PASSO.md) · Gaps: [`15-GAPS-PRODUCAO-SEGURANCA.md`](15-GAPS-PRODUCAO-SEGURANCA.md)

Este arquivo resume o mesmo conteúdo para leitura no monorepo **site**; detalhes e Sprint 9 estão no link acima.

---

## Resumo executivo

| Pacote | Dev | Infra/mês | IA/mês |
|--------|-----|-----------|--------|
| **S8 mínima** (página artigo + SEO + RSS) | 3–5 dias | **~R$ 0** | US$ 0–2 |
| **S8 + capa URL** (Unsplash) | +0,5 dia | **~R$ 0** | R$ 0 |
| **S8 + R2 + capa** | +2 dias | **R$ 0–5** | R$ 0 |
| **S9: IA preview + R2** | +4–6 dias | **R$ 0–10** | **US$ 2–8** |
| **Vídeo embed** | +0,5 dia | R$ 0 | R$ 0 |

**S8 não exige servidor novo** (mesmo Hetzner CX23 + Neon + Cloudflare).

---

## Sprint 8 — infra incremental

| Recurso | Impacto |
|---------|---------|
| CPU/RAM site backend | Desprezível (+1 endpoint leitura, sitemap/RSS) |
| Neon `trcon_site` | +2 MB storage para ~100 artigos com body |
| Cloudflare | R$ 0 (texto/HTML) |
| DeepSeek | S8 mínima não exige; S8.7 SEO texto usa quota existente |

---

## Imagens e vídeo

- **Capa manual (URL):** ✅ Desenho A (30/07) — campo `cover_image_url` + hero + `og:image`
- **Cloudflare R2:** free tier cobre dezenas/centenas de capas (~150 KB cada) — Desenho B
- **IA imagem:** **não** DeepSeek — API separada (~US$ 0,01–0,04/imagem); quota **US$ 5/mês** separada recomendada
- **Vídeo:** embed YouTube/Vimeo = ✅ Desenho A (URL no body); Stream hospedado = R$ 50+/mês — evitar no MVP

---

## IA preview no marketing (Sprint 9)

Hoje **não existe**. Exige:

**Marketing:** `ImageGenerationService`, R2, `POST /api/v1/ai/generate-cover-preview`, campo capa, botão no form, publish com URL.

**Site:** Flyway V7+ (`cover_image_url`), API news estendida, hero + `og:image` na página artigo.

Ver tabelas completas e fluxo em [`11_custos_sprint8_midia_ia.md`](../../sirius-marketing/projeto/docs/cursor/11_custos_sprint8_midia_ia.md).  
Desenho arquitetural A/B: [`19-DESENHO-MIDIA.md`](./19-DESENHO-MIDIA.md) / canônico [`13_desenho_midia_imagens_videos.md`](../../sirius-marketing/projeto/docs/cursor/13_desenho_midia_imagens_videos.md).

---

## Ordem recomendada

1. **S8 mínima** (S8.1–S8.6) — ✅
2. Capa opcional via URL externa + vídeo embed — ✅ Desenho A (30/07)
3. **S9 / Desenho B** — IA visual + R2, se produto validar necessidade
4. Stream só se volume justificar
