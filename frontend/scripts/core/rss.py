"""Coleta e parsing de feeds RSS usando apenas a biblioteca padrao.

`parse_rss` e puro (recebe o XML como texto) e por isso e testavel sem rede.
`fetch_feed` isola o unico ponto de I/O.
"""

from __future__ import annotations

import ssl
from urllib.request import Request, urlopen
from xml.etree import ElementTree


USER_AGENT = "TRConContentBot/1.0"


def fetch_feed(url: str, timeout: int = 12) -> str:
    """Baixa o XML de um feed RSS. Lanca em caso de falha de rede."""
    request = Request(url, headers={"User-Agent": USER_AGENT})
    context = ssl.create_default_context()
    with urlopen(request, timeout=timeout, context=context) as response:
        return response.read().decode("utf-8", errors="replace")


def parse_rss(xml_text: str, source_fallback: str = "RSS") -> list[dict]:
    """Extrai itens (title, url, source, published_at) de um XML de RSS.

    Tolerante a falhas: XML invalido retorna lista vazia em vez de quebrar.
    """
    try:
        root = ElementTree.fromstring(xml_text)
    except ElementTree.ParseError:
        return []

    items: list[dict] = []
    for item in root.iter("item"):
        title = _text(item, "title")
        if not title:
            continue
        source = _text(item, "source") or source_fallback
        items.append(
            {
                "title": title,
                "url": _text(item, "link"),
                "source": source,
                "published_at": _text(item, "pubDate"),
            }
        )
    return items


def _text(node, tag: str) -> str:
    child = node.find(tag)
    if child is None or child.text is None:
        return ""
    return child.text.strip()
