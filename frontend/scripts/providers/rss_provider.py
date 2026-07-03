"""Provedor de conteudo baseado em RSS (implementa ContentProvider).

Agrega multiplos feeds, tolera feeds offline (registra em `errors`) e nunca
lanca por falha de rede — devolve o que conseguiu coletar.
"""

from __future__ import annotations

from typing import Callable

from core.rss import fetch_feed, parse_rss
from core.text import clean_title, dedup_by_title


class RssProvider:
    def __init__(
        self,
        name: str,
        feeds: list[str],
        fetcher: Callable[[str], str] = fetch_feed,
    ) -> None:
        # `fetcher` injetavel permite testar sem rede (DIP).
        self.name = name
        self.feeds = feeds
        self._fetcher = fetcher
        self.errors: list[str] = []

    def fetch(self, limit: int) -> list[dict]:
        collected: list[dict] = []
        for url in self.feeds:
            try:
                xml = self._fetcher(url)
            except Exception as exc:  # rede/timeout/etc: degrada, nao quebra
                self.errors.append(f"{self.name}: falha ao ler feed ({exc.__class__.__name__})")
                continue
            for item in parse_rss(xml, source_fallback=self.name):
                item["title"] = clean_title(item["title"])
                collected.append(item)

        unique = dedup_by_title(collected)
        return unique[:limit]
