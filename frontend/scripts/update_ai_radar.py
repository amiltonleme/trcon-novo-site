#!/usr/bin/env python3
"""Gera data/ai-radar.json a partir de feeds publicos de IA.

Script fino: apenas fia o provider (RSS) + o builder + o writer. Toda a logica
mora em core/, providers/ e builders/ (SRP). Degrada de forma elegante: se as
fontes cairem, mantem o ultimo artefato valido.
"""

from __future__ import annotations

import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))

from builders.radar_builder import RadarConfig, build_radar
from core.writer import read_json, write_json
from providers.rss_provider import RssProvider

ROOT = Path(__file__).resolve().parents[1]
OUTPUT = ROOT / "data" / "ai-radar.json"

FEEDS = [
    "https://news.google.com/rss/search?q=intelig%C3%AAncia+artificial+IA+empresas&hl=pt-BR&gl=BR&ceid=BR:pt-419",
    "https://news.google.com/rss/search?q=artificial+intelligence+enterprise+AI&hl=en-US&gl=US&ceid=US:en",
]

CONFIG = RadarConfig(
    category="IA",
    relevance_keywords={
        "ia", "inteligencia artificial", "artificial intelligence", "ai",
        "modelo", "llm", "copilot", "automacao", "machine learning", "generativa",
    },
    positive_words={"avanco", "lanca", "cresce", "acelera", "recorde", "investe"},
    negative_words={"risco", "processo", "banimento", "falha", "demissao", "queda"},
    max_items=6,
    source_note="Curadoria automatica por regras a partir de feeds RSS publicos de IA.",
)


def main() -> int:
    provider = RssProvider(name="Google News (IA)", feeds=FEEDS)
    payload = build_radar(provider, CONFIG)

    if not payload["items"]:
        previous = read_json(OUTPUT)
        if previous and previous.get("items"):
            previous.setdefault("errors", []).append(
                "Sem itens novos nesta execucao; mantido o ultimo artefato valido."
            )
            write_json(OUTPUT, previous)
            print(f"ai-radar: sem itens novos, mantido artefato anterior ({OUTPUT.name}).")
            return 0

    write_json(OUTPUT, payload)
    print(f"ai-radar: {len(payload['items'])} itens, {len(payload['errors'])} erro(s).")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
