# Pipeline de conteúdo — TRCon Site

Geração offline (Camada 4 da arquitetura canônica) dos JSONs consumidos pela
home. Usa **apenas a biblioteca padrão do Python** (roda em CI simples, sem
`pip install`).

## Estrutura (SOLID)

```text
scripts/
  core/
    text.py       # funções puras: clean_title, normalize, classify_signal, dedup...
    rss.py        # fetch_feed (I/O) + parse_rss (puro)
    writer.py     # envelope canônico (generated_at, source_note, errors, items) + read/write
  providers/
    base.py       # ContentProvider (Protocol) — abstração para DIP
    rss_provider.py  # RssProvider: agrega feeds, tolera falhas, dedup
  builders/
    radar_builder.py # build_radar(provider, config) -> payload do radar
    home_builder.py  # build_home_highlights / build_news_log (shape = contratos do backend)
  update_ai_radar.py       # -> data/ai-radar.json
  update_tech_radar.py     # -> data/tech-radar.json
  build_home_payload.py    # -> data/home-highlights.json, data/news-log.json
  update_market.py         # (existente) -> data/market.json
  update_daily_content.py  # (existente) -> data/economy-tips.json, data/recipes.json
  tests/
    test_pipeline.py       # unittest (stdlib), sem rede
```

Cada script de entrada é **fino**: apenas fia provider + builder + writer. A
lógica é testável isoladamente porque as funções puras não fazem I/O e o
provider recebe o `fetcher` por injeção.

## Princípios aplicados

- **SRP**: um script por responsabilidade (um radar, uma consolidação).
- **OCP**: nova fonte = novo feed/novo provider, sem alterar o builder.
- **LSP**: qualquer `ContentProvider` equivalente substitui outro.
- **DIP**: o builder depende de `ContentProvider`, não de uma URL concreta.

## Contrato de saída

Todo artefato tem o envelope: `generated_at`, `source_note`, `errors`, `items`.
`home-highlights.json` e `news-log.json` usam o **mesmo shape (camelCase) dos
contratos do backend** (`HighlightResponse` / `NewsItemResponse`), para que a
home consuma JSON ou API sem diferença (Fase 7 — doc/07-MIGRACAO-PARALELA.md).

## Rodar localmente

```bash
python scripts/update_ai_radar.py
python scripts/update_tech_radar.py
python scripts/build_home_payload.py
```

## Testes

```bash
python -m unittest discover -s scripts -p "test_*.py"
```

## Degradação elegante

Se uma fonte cair, o provider registra em `errors` e segue com as demais. Se um
radar ficar sem itens, o script mantém o último artefato válido em vez de
publicar vazio. O site nunca quebra por falha de feed externo
(doc/02-ARQUITETURA-CANONICA.md, política de falha).

## Automação

`update-content.yml` (em `../.github/workflows/`) roda o pipeline **2x/dia**
(08:00 e 20:00 UTC) e commita apenas `frontend/data/*.json`.
