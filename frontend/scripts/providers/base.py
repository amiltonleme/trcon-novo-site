"""Contrato de provedor de conteudo (abstracao para DIP).

Os builders dependem desta interface, nao de uma URL concreta espalhada pelo
codigo. Trocar/adicionar fontes e uma extensao (OCP) e uma implementacao
equivalente pode substituir outra sem quebrar o builder (LSP).
Ver doc/02-ARQUITETURA-CANONICA.md (secao SOLID).
"""

from __future__ import annotations

from typing import Protocol


class ContentProvider(Protocol):
    """Fonte de itens de conteudo.

    `fetch` retorna uma lista de dicts com ao menos: title, url, source,
    published_at. Nunca deve lancar por falha de rede — em caso de erro deve
    registrar em `errors` e retornar o que conseguiu (degradacao elegante).
    """

    name: str
    errors: list[str]

    def fetch(self, limit: int) -> list[dict]:
        ...
