#!/usr/bin/env python3
"""Consolida os radares em data/home-highlights.json e data/news-log.json.

Le os artefatos ja gerados (ai-radar, tech-radar) e produz os payloads que a
home consome. Se um radar nao existir, degrada usando os que houver.
"""

from __future__ import annotations

import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))

from builders.home_builder import build_home_highlights, build_news_log
from core.writer import read_json, write_json

ROOT = Path(__file__).resolve().parents[1]
DATA_DIR = ROOT / "data"

RADAR_FILES = ["ai-radar.json", "tech-radar.json"]


def main() -> int:
    radars: list[dict] = []
    missing: list[str] = []
    for name in RADAR_FILES:
        payload = read_json(DATA_DIR / name)
        if payload:
            radars.append(payload)
        else:
            missing.append(name)

    highlights = build_home_highlights(radars)
    news = build_news_log(radars)

    for artifact in (highlights, news):
        for name in missing:
            artifact.setdefault("errors", []).append(f"Radar ausente: {name}")

    write_json(DATA_DIR / "home-highlights.json", highlights)
    write_json(DATA_DIR / "news-log.json", news)

    print(
        f"home-highlights: {len(highlights['items'])} itens; "
        f"news-log: {len(news['items'])} itens; radares usados: {len(radars)}."
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
