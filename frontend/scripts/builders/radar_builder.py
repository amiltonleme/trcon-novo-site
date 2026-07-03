"""Constroi o payload de um radar (IA, Tecnologia, ...) a partir de um provider.

Depende da abstracao ContentProvider, nao de uma fonte concreta (DIP).
Aplica curadoria por regras (Nivel 1, sem IA paga) conforme
doc/02-ARQUITETURA-CANONICA.md.
"""

from __future__ import annotations

from dataclasses import dataclass, field

from core.text import classify_signal, keyword_score, summarize
from core.writer import build_envelope


@dataclass
class RadarConfig:
    category: str  # ex.: "IA", "Tecnologia"
    relevance_keywords: set[str]
    positive_words: set[str] = field(default_factory=set)
    negative_words: set[str] = field(default_factory=set)
    max_items: int = 6
    source_note: str = "Curadoria automatica por regras a partir de feeds RSS publicos."


def build_radar(provider, config: RadarConfig) -> dict:
    """Coleta, ranqueia por relevancia e monta o envelope do radar."""
    raw = provider.fetch(limit=config.max_items * 4)

    ranked = sorted(
        raw,
        key=lambda item: keyword_score(item.get("title", ""), config.relevance_keywords),
        reverse=True,
    )

    items = []
    for entry in ranked[: config.max_items]:
        title = entry.get("title", "")
        items.append(
            {
                "title": title,
                "summary": summarize(title),
                "url": entry.get("url", ""),
                "source": entry.get("source", config.category),
                "published_at": entry.get("published_at", ""),
                "category": config.category,
                "signal": classify_signal(title, config.positive_words, config.negative_words),
            }
        )

    return build_envelope(
        items=items,
        source_note=config.source_note,
        errors=list(getattr(provider, "errors", [])),
        extra={"category": config.category},
    )
