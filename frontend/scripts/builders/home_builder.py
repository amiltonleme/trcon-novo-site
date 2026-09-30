"""Consolida os radares em home-highlights.json.

O formato de saida espelha o contrato publico HighlightResponse do backend,
para que a home possa consumir tanto o JSON estatico quanto a API sem mudar o
shape.
"""

from __future__ import annotations

from core.text import dedup_by_title
from core.writer import build_envelope


def build_home_highlights(radars: list[dict], max_items: int = 6) -> dict:
    """Seleciona os destaques principais entre os radares fornecidos.

    Alterna categorias para dar variedade e limita a quantidade.
    """
    pools = [list(radar.get("items", [])) for radar in radars]
    interleaved: list[dict] = []
    idx = 0
    while any(idx < len(pool) for pool in pools):
        for pool in pools:
            if idx < len(pool):
                interleaved.append(pool[idx])
        idx += 1

    unique = dedup_by_title(interleaved)[:max_items]

    items = []
    for priority, entry in enumerate(unique, start=1):
        # Shape identico ao HighlightResponse do backend (camelCase), para que a
        # home consuma JSON ou API sem diferenca (doc/07-MIGRACAO-PARALELA.md).
        items.append(
            {
                "category": entry.get("category", ""),
                "title": entry.get("title", ""),
                "summary": entry.get("summary", ""),
                "link": entry.get("url", ""),
                "priority": priority,
                "publishedAt": entry.get("published_at", ""),
            }
        )

    errors = [e for radar in radars for e in radar.get("errors", [])]
    return build_envelope(
        items=items,
        source_note="Destaques consolidados dos radares TRCONGROUP (curadoria por regras).",
        errors=errors,
    )
