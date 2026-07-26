#!/usr/bin/env python3
"""Generate daily budget recipes for the static site.

Dicas de educacao financeira: ver update_economy_tips.py (RSS + fallback).
"""

from __future__ import annotations

import json
import sys
from datetime import datetime, timezone
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))

from catalog.economy_tips_fallback import pick_rotating

ROOT = Path(__file__).resolve().parents[1]
DATA_DIR = ROOT / "data"

RECIPES = [
    {
        "emoji": "🍝",
        "title": "Macarrao alho e oleo com legumes",
        "body": "Base barata, rapida e flexivel. Use cenoura, abobrinha ou sobras da geladeira para aumentar rendimento.",
        "meta": ["20min", "4 porcoes", "Facil"],
        "url": "https://www.google.com/search?q=receita+macarrao+alho+e+oleo+com+legumes",
        "link_label": "Ver receita",
        "gradient": "linear-gradient(135deg,#1a2535,#0d1219)",
    },
    {
        "emoji": "🥘",
        "title": "Arroz, feijao e ovo cremoso",
        "body": "Prato completo, proteico e barato. Funciona bem para marmitas e reduz pedidos de delivery durante a semana.",
        "meta": ["25min", "3 porcoes", "Facil"],
        "url": "https://www.google.com/search?q=receita+arroz+feijao+ovo+cremoso",
        "link_label": "Ver receita",
        "gradient": "linear-gradient(135deg,#1f1a10,#0d1219)",
    },
    {
        "emoji": "🥗",
        "title": "Salada reforcada com frango desfiado",
        "body": "Boa para aproveitar frango cozido, arroz ou graos. Leve, nutritiva e mais barata que almoco fora.",
        "meta": ["15min", "2 porcoes", "Facil"],
        "url": "https://www.google.com/search?q=receita+salada+reforcada+frango+desfiado",
        "link_label": "Ver receita",
        "gradient": "linear-gradient(135deg,#101a1a,#0d1219)",
    },
    {
        "emoji": "🍌",
        "title": "Bolo de banana madura",
        "body": "Aproveita bananas que iriam para o lixo e vira lanche da semana. Pode ser feito com aveia para render mais.",
        "meta": ["45min", "8 fatias", "Medio"],
        "url": "https://www.google.com/search?q=receita+bolo+de+banana+madura+com+aveia",
        "link_label": "Ver receita",
        "gradient": "linear-gradient(135deg,#1a1020,#0d1219)",
    },
    {
        "emoji": "🍲",
        "title": "Sopa de legumes com lentilha",
        "body": "Rende bastante, congela bem e substitui refeicoes caras nos dias corridos.",
        "meta": ["35min", "5 porcoes", "Facil"],
        "url": "https://www.google.com/search?q=receita+sopa+de+legumes+com+lentilha",
        "link_label": "Ver receita",
        "gradient": "linear-gradient(135deg,#162015,#0d1219)",
    },
    {
        "emoji": "🌮",
        "title": "Panqueca de frango com sobras",
        "body": "Transforma pequenas sobras em refeicao nova. A massa leva poucos ingredientes e rende varias unidades.",
        "meta": ["40min", "6 unidades", "Medio"],
        "url": "https://www.google.com/search?q=receita+panqueca+de+frango+com+sobras",
        "link_label": "Ver receita",
        "gradient": "linear-gradient(135deg,#201610,#0d1219)",
    },
]


def write_json(path: Path, payload: dict) -> None:
    path.write_text(json.dumps(payload, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")


def main() -> None:
    DATA_DIR.mkdir(exist_ok=True)
    now = datetime.now(timezone.utc)
    day_seed = int(now.strftime("%Y%j"))
    write_json(
        DATA_DIR / "recipes.json",
        {"generated_at": now.isoformat(), "items": pick_rotating(RECIPES, 4, day_seed + 2)},
    )


if __name__ == "__main__":
    main()
