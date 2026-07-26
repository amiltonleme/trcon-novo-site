"""Constroi economy-tips.json a partir de RSS + catalogo estatico (fallback).

Shape compativel com renderTips() em assets/app.js:
  tag, tag_class, title, body, meta[], url, link_label, featured?, chart?
"""

from __future__ import annotations

from dataclasses import dataclass, field

from catalog.economy_tips_fallback import TIPS, pick_rotating
from core.text import keyword_score, normalize, strip_html, summarize
from core.writer import build_envelope


@dataclass
class EconomyTipsConfig:
    relevance_keywords: set[str]
    max_items: int = 4
    min_rss_items: int = 2
    source_note: str = (
        "Curadoria automatica de educacao financeira a partir de feeds RSS publicos, "
        "com complemento do catalogo estatico TRCon quando necessario."
    )
    tag_rules: list[tuple[set[str], str, str]] = field(default_factory=list)


ECONOMY_TIPS_DISCLAIMER = (
    "Conteudo educacional. Nao constitui recomendacao individual de investimento."
)


DEFAULT_TAG_RULES: list[tuple[set[str], str, str]] = [
    ({"juros", "selic", "cdi", "cdb", "tesouro", "renda fixa", "ibovespa", "investimento"}, "Mercado", "tag-blue"),
    ({"cartao", "credito", "fatura", "parcela", "divida", "endivid"}, "Cartao", "tag-orange"),
    ({"supermercado", "compras", "preco", "inflacao", "economizar"}, "Compras", "tag-gold"),
    ({"orcamento", "gastos", "reserva", "emergencia", "poupanca"}, "Estrategia Mensal", "tag-green"),
    ({"assinatura", "casa", "familia", "salario"}, "Cotidiano", "tag-orange"),
]


def classify_tip_tag(title: str, rules: list[tuple[set[str], str, str]]) -> tuple[str, str]:
    best_score = 0
    best_tag = "Educacao"
    best_class = "tag-blue"
    for keywords, tag, tag_class in rules:
        score = keyword_score(title, keywords)
        if score > best_score:
            best_score = score
            best_tag = tag
            best_class = tag_class
    return best_tag, best_class


def rss_item_to_tip(entry: dict, rules: list[tuple[set[str], str, str]]) -> dict:
    title = entry.get("title", "").strip()
    tag, tag_class = classify_tip_tag(title, rules)
    source = entry.get("source", "RSS")
    raw_body = strip_html(entry.get("description", ""))
    body = summarize(raw_body, limit=320) if raw_body else summarize(title, limit=220)
    return {
        "tag": tag,
        "tag_class": tag_class,
        "title": title,
        "body": body,
        "meta": ["Leitura rapida", f"Fonte: {source}"],
        "url": entry.get("url", ""),
        "link_label": "Ler mais",
    }


def _dedup_against_titles(items: list[dict], seen: set[str]) -> list[dict]:
    unique: list[dict] = []
    for item in items:
        key = normalize(item.get("title", ""))
        if not key or key in seen:
            continue
        seen.add(key)
        unique.append(item)
    return unique


def build_economy_tips(provider, config: EconomyTipsConfig, day_seed: int) -> dict:
    """Monta payload com RSS ranqueado; completa com catalogo estatico se preciso."""
    rules = config.tag_rules or DEFAULT_TAG_RULES
    raw = provider.fetch(limit=config.max_items * 6)

    ranked = sorted(
        raw,
        key=lambda item: keyword_score(item.get("title", ""), config.relevance_keywords),
        reverse=True,
    )

    rss_tips = [rss_item_to_tip(entry, rules) for entry in ranked[: config.max_items]]
    seen = {normalize(item["title"]) for item in rss_tips if item.get("title")}

    items: list[dict] = list(rss_tips)
    errors = list(getattr(provider, "errors", []))

    if len(items) < config.max_items:
        needed = config.max_items - len(items)
        fallback_pool = pick_rotating(TIPS, len(TIPS), day_seed)
        supplemental = _dedup_against_titles(fallback_pool, seen)[:needed]
        if supplemental:
            items.extend(supplemental)
            errors.append(
                f"Complementado com {len(supplemental)} dica(s) do catalogo estatico "
                f"(RSS retornou {len(rss_tips)} item(ns) relevante(s))."
            )

    if not items:
        items = pick_rotating(TIPS, config.max_items, day_seed)
        errors.append("Feeds RSS indisponiveis; usando apenas catalogo estatico.")

    if items:
        featured = dict(items[0])
        featured["featured"] = True
        items[0] = featured

    source_mode = "rss+catalogo" if rss_tips else "catalogo"
    return build_envelope(
        items=items[: config.max_items],
        source_note=config.source_note,
        errors=errors,
        extra={
            "source_mode": source_mode,
            "rss_items": len(rss_tips),
            "disclaimer": ECONOMY_TIPS_DISCLAIMER,
        },
    )
