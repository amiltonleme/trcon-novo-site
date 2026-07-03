#!/usr/bin/env python3
"""Gera data/tech-radar.json a partir de feeds publicos de tecnologia."""

from __future__ import annotations

import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))

from builders.radar_builder import RadarConfig, build_radar
from core.writer import read_json, write_json
from providers.rss_provider import RssProvider

ROOT = Path(__file__).resolve().parents[1]
OUTPUT = ROOT / "data" / "tech-radar.json"

FEEDS = [
    "https://news.google.com/rss/search?q=tecnologia+software+desenvolvimento&hl=pt-BR&gl=BR&ceid=BR:pt-419",
    "https://news.google.com/rss/search?q=software+engineering+cloud+developers&hl=en-US&gl=US&ceid=US:en",
]

CONFIG = RadarConfig(
    category="Tecnologia",
    relevance_keywords={
        "tecnologia", "software", "desenvolvimento", "cloud", "nuvem", "dados",
        "seguranca", "devops", "programacao", "startup", "plataforma", "api",
    },
    positive_words={"lanca", "cresce", "acelera", "investe", "adota", "expande"},
    negative_words={"falha", "vazamento", "ataque", "demissao", "queda", "processo"},
    max_items=6,
    source_note="Curadoria automatica por regras a partir de feeds RSS publicos de tecnologia.",
)


def main() -> int:
    provider = RssProvider(name="Google News (Tecnologia)", feeds=FEEDS)
    payload = build_radar(provider, CONFIG)

    if not payload["items"]:
        previous = read_json(OUTPUT)
        if previous and previous.get("items"):
            previous.setdefault("errors", []).append(
                "Sem itens novos nesta execucao; mantido o ultimo artefato valido."
            )
            write_json(OUTPUT, previous)
            print(f"tech-radar: sem itens novos, mantido artefato anterior ({OUTPUT.name}).")
            return 0

    write_json(OUTPUT, payload)
    print(f"tech-radar: {len(payload['items'])} itens, {len(payload['errors'])} erro(s).")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
