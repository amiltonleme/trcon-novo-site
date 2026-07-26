#!/usr/bin/env python3
"""Gera data/economy-tips.json (Educacao Financeira) via RSS + fallback estatico.

Script fino: provider RSS + builder + writer. Se a rede falhar por completo,
mantem o ultimo artefato valido (mesma politica dos radares).
"""

from __future__ import annotations

import sys
from datetime import datetime, timezone
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))

from builders.economy_tips_builder import EconomyTipsConfig, build_economy_tips
from core.writer import read_json, write_json
from providers.rss_provider import RssProvider

ROOT = Path(__file__).resolve().parents[1]
OUTPUT = ROOT / "data" / "economy-tips.json"

FEEDS = [
    "https://news.google.com/rss/search?q=educacao+financeira+poupanca+investimento+Brasil&hl=pt-BR&gl=BR&ceid=BR:pt-419",
    "https://news.google.com/rss/search?q=financas+pessoais+orcamento+familia+economia&hl=pt-BR&gl=BR&ceid=BR:pt-419",
    "https://news.google.com/rss/search?q=reserva+de+emergencia+CDI+Tesouro+Direto+dicas&hl=pt-BR&gl=BR&ceid=BR:pt-419",
    "https://news.google.com/rss/search?q=cartao+de+credito+endividamento+como+economizar&hl=pt-BR&gl=BR&ceid=BR:pt-419",
]

CONFIG = EconomyTipsConfig(
    relevance_keywords={
        "educacao financeira",
        "financas pessoais",
        "poupanca",
        "investimento",
        "orcamento",
        "economia",
        "reserva",
        "emergencia",
        "juros",
        "selic",
        "cdb",
        "tesouro",
        "cartao",
        "credito",
        "divida",
        "inflacao",
        "renda",
        "aposentadoria",
        "imposto",
        "economizar",
    },
    max_items=4,
    min_rss_items=2,
)


def main() -> int:
    day_seed = int(datetime.now(timezone.utc).strftime("%Y%j"))
    provider = RssProvider(name="Google News (Educacao Financeira)", feeds=FEEDS)
    payload = build_economy_tips(provider, CONFIG, day_seed)

    if not payload["items"]:
        previous = read_json(OUTPUT)
        if previous and previous.get("items"):
            previous.setdefault("errors", []).append(
                "Sem itens nesta execucao; mantido o ultimo artefato valido."
            )
            write_json(OUTPUT, previous)
            print(f"economy-tips: sem itens, mantido artefato anterior ({OUTPUT.name}).")
            return 0

    write_json(OUTPUT, payload)
    print(
        f"economy-tips: {len(payload['items'])} itens "
        f"({payload.get('source_mode', '?')}), {len(payload['errors'])} aviso(s)."
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
