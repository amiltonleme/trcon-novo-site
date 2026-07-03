"""Funcoes puras de tratamento de texto para o pipeline de conteudo.

Sem I/O e sem rede: tudo aqui e deterministico e testavel isoladamente
(ver scripts/tests). Segue o principio de responsabilidade unica.
"""

from __future__ import annotations

import re
import unicodedata


def clean_title(title: str) -> str:
    """Remove o sufixo de fonte que o Google News anexa ao titulo.

    Ex.: "Titulo da noticia - Nome do Veiculo" -> "Titulo da noticia".
    """
    text = (title or "").strip()
    if " - " in text:
        text = text.rsplit(" - ", 1)[0].strip()
    return text


def normalize(text: str) -> str:
    """Minusculo e sem acentos, para comparacao por palavra-chave."""
    lowered = (text or "").lower()
    decomposed = unicodedata.normalize("NFKD", lowered)
    return "".join(ch for ch in decomposed if not unicodedata.combining(ch))


def keyword_score(text: str, keywords: set[str]) -> int:
    """Conta quantas palavras-chave (normalizadas) aparecem no texto."""
    haystack = normalize(text)
    return sum(1 for kw in keywords if normalize(kw) in haystack)


def classify_signal(text: str, positive: set[str], negative: set[str]) -> str:
    """Classifica um item como up/down/flat por heuristica de palavras."""
    pos = keyword_score(text, positive)
    neg = keyword_score(text, negative)
    if pos > neg:
        return "up"
    if neg > pos:
        return "down"
    return "flat"


def summarize(text: str, limit: int = 180) -> str:
    """Resumo conservador: normaliza espacos e corta em limite de caracteres."""
    collapsed = re.sub(r"\s+", " ", (text or "").strip())
    if len(collapsed) <= limit:
        return collapsed
    corte = collapsed[:limit].rsplit(" ", 1)[0]
    return corte.rstrip(".,;:") + "..."


def dedup_by_title(items: list[dict]) -> list[dict]:
    """Remove itens com titulo normalizado repetido, preservando a ordem."""
    seen: set[str] = set()
    result: list[dict] = []
    for item in items:
        key = normalize(item.get("title", ""))
        if not key or key in seen:
            continue
        seen.add(key)
        result.append(item)
    return result
