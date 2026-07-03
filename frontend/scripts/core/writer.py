"""Escrita de artefatos JSON com os campos minimos de observabilidade.

Responsabilidade unica: montar o envelope padrao (generated_at, source_note,
errors, items) e persistir. Ver doc/02-ARQUITETURA-CANONICA.md (observabilidade).
"""

from __future__ import annotations

import json
from datetime import datetime, timezone
from pathlib import Path


def build_envelope(
    items: list[dict],
    source_note: str,
    errors: list[str] | None = None,
    extra: dict | None = None,
) -> dict:
    """Monta o envelope canonico de um artefato de conteudo."""
    payload = {
        "generated_at": datetime.now(timezone.utc).isoformat(),
        "source_note": source_note,
        "errors": errors or [],
        "items": items,
    }
    if extra:
        payload.update(extra)
    return payload


def write_json(path: Path, payload: dict) -> None:
    """Escreve JSON UTF-8 identado, criando o diretorio se necessario."""
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(
        json.dumps(payload, ensure_ascii=False, indent=2) + "\n",
        encoding="utf-8",
    )


def read_json(path: Path) -> dict | None:
    """Le um artefato existente (para fallback ao ultimo dado valido)."""
    try:
        return json.loads(path.read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError):
        return None
