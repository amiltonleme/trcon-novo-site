#!/usr/bin/env python3
"""Update market quotes and market mood JSON for the static site.

The script intentionally uses only Python's standard library so it can run in
simple CI/hosting environments. It fetches public endpoints when available and
keeps a conservative fallback when a source is offline.
"""

from __future__ import annotations

import time
import json
import math
import re
import ssl
import sys
from datetime import datetime, timezone
from pathlib import Path
from urllib.parse import quote
from urllib.error import URLError
from urllib.request import Request, urlopen
from xml.etree import ElementTree


ROOT = Path(__file__).resolve().parents[1]
DATA_DIR = ROOT / "data"
DISCLAIMER = "*** Conteudo educacional. Nao constitui recomendacao individual de investimento. ***"


ASSETS = [
    {"id": "usdbrl", "name": "Dolar (USD)", "icon": "💵", "kind": "fx", "pair": "USDBRL"},
    {"id": "eurbrl", "name": "Euro (EUR)", "icon": "💶", "kind": "fx", "pair": "EURBRL"},
    {"id": "ibov", "name": "Ibovespa", "icon": "📊", "kind": "index", "symbol": "^BVSP"},
    {"id": "btc", "name": "Bitcoin", "icon": "₿", "kind": "crypto", "coin": "bitcoin"},
    {"id": "eth", "name": "Ethereum", "icon": "Ξ", "kind": "crypto", "coin": "ethereum"},
    {"id": "gold", "name": "Ouro", "icon": "🥇", "kind": "gold", "pair": "XAUUSD"},
    {"id": "selic", "name": "Tesouro Selic", "icon": "🏦", "kind": "rate"},
    {"id": "cdi", "name": "CDI", "icon": "🏛️", "kind": "cdi"},
]


RSS_FEEDS = [
    "https://news.google.com/rss/search?q=mercado+financeiro+Brasil+juros+dolar+bolsa&hl=pt-BR&gl=BR&ceid=BR:pt-419",
    "https://news.google.com/rss/search?q=Ibovespa+dolar+Selic+Bitcoin&hl=pt-BR&gl=BR&ceid=BR:pt-419",
]


POSITIVE_WORDS = {
    "alta", "sobe", "sobem", "avança", "avanço", "valorização", "recorde",
    "otimismo", "crescimento", "recuperação", "corte de juros", "queda de juros"
}
NEGATIVE_WORDS = {
    "queda", "cai", "caem", "recuo", "risco", "pressão", "inflação",
    "juros altos", "incerteza", "tensão", "crise", "desacelera"
}


def fetch_json(url: str, timeout: int = 12) -> dict | list:
    request = Request(url, headers={"User-Agent": "TRConMarketBot/1.0"})
    context = ssl.create_default_context()
    with urlopen(request, timeout=timeout, context=context) as response:
        return json.loads(response.read().decode("utf-8"))


def fetch_text(url: str, timeout: int = 12) -> str:
    request = Request(url, headers={"User-Agent": "TRConMarketBot/1.0"})
    context = ssl.create_default_context()
    with urlopen(request, timeout=timeout, context=context) as response:
        return response.read().decode("utf-8", errors="replace")


def brl(value: float) -> str:
    return f"R$ {value:,.2f}".replace(",", "X").replace(".", ",").replace("X", ".")


def usd(value: float) -> str:
    return f"US$ {value:,.0f}".replace(",", ".")


def points(value: float) -> str:
    return f"{value:,.0f} pts".replace(",", ".")


def pct(value: float | None) -> str:
    if value is None or math.isnan(value):
        return "estavel"
    sign = "+" if value > 0 else ""
    return f"{sign}{value:.2f}%".replace(".", ",")


def _quote_fx_bcb() -> dict[str, dict]:
    """Fallback usando PTAX do BCB + cruzamento EUR/USD do BCE."""
    from datetime import date, timedelta

    def bcb_ptax(dataCotacao: str) -> list:
        url = (
            "https://olinda.bcb.gov.br/olinda/servico/PTAX/versao/v1/odata/"
            f"CotacaoDolarDia(dataCotacao=@d)?@d='{dataCotacao}'&$top=1&$format=json"
        )
        data = fetch_json(url)
        return data.get("value", [])

    # Tenta hoje e recua até 5 dias úteis (fins de semana/feriados não têm PTAX)
    usd_brl = None
    for days_back in range(5):
        day = (date.today() - timedelta(days=days_back)).strftime("%m-%d-%Y")
        rows = bcb_ptax(day)
        if rows:
            usd_brl = float(rows[0].get("cotacaoVenda", 0))
            break

    if not usd_brl:
        raise ValueError("BCB PTAX indisponivel")

    # EUR/USD do BCE via Frankfurter (sem BRL, mas cruzamos)
    eur_data = fetch_json("https://api.frankfurter.app/latest?from=EUR&to=USD")
    eur_usd = float(eur_data["rates"]["USD"])

    # Cruzamento: EUR/BRL = EUR/USD × USD/BRL
    eur_brl = round(eur_usd * usd_brl, 4)

    return {
        "USDBRL": {"bid": usd_brl, "pctChange": 0},
        "EURBRL": {"bid": eur_brl, "pctChange": 0},
    }


def quote_fx() -> dict[str, dict]:
    """AwesomeAPI com fallback para BCB + BCE quando der 429."""
    for attempt in range(3):
        try:
            data = fetch_json("https://economia.awesomeapi.com.br/json/last/USD-BRL,EUR-BRL")
            return {
                "USDBRL": data.get("USDBRL", {}),
                "EURBRL": data.get("EURBRL", {}),
            }
        except Exception as exc:
            if "429" in str(exc) and attempt < 2:
                time.sleep(10 * (attempt + 1))  # 10s na 1ª, 20s na 2ª
                continue
            if "429" in str(exc):
                # Esgotou retries — usa fallback
                return _quote_fx_bcb()
            raise
    return _quote_fx_bcb()


def quote_crypto() -> dict:
    ids = "bitcoin,ethereum"
    url = f"https://api.coingecko.com/api/v3/simple/price?ids={ids}&vs_currencies=usd&include_24hr_change=true"
    return fetch_json(url)


def quote_selic() -> float | None:
    url = "https://api.bcb.gov.br/dados/serie/bcdata.sgs.432/dados/ultimos/1?formato=json"
    data = fetch_json(url)
    if isinstance(data, list) and data:
        return float(str(data[0].get("valor", "")).replace(",", "."))
    return None


def quote_cdi_annualized() -> float | None:
    url = "https://api.bcb.gov.br/dados/serie/bcdata.sgs.12/dados/ultimos/1?formato=json"
    data = fetch_json(url)
    if isinstance(data, list) and data:
        daily = float(str(data[0].get("valor", "")).replace(",", "."))
        return ((1 + daily / 100) ** 252 - 1) * 100
    return None


def quote_yahoo(symbol: str) -> tuple[float | None, float | None]:
    encoded = quote(symbol, safe="")
    url = f"https://query1.finance.yahoo.com/v8/finance/chart/{encoded}?range=5d&interval=1d"
    data = fetch_json(url)
    result = data.get("chart", {}).get("result", [{}])[0]
    meta = result.get("meta", {})
    value = meta.get("regularMarketPrice") or meta.get("previousClose")
    previous = meta.get("previousClose")
    change = None
    if value and previous:
        change = ((float(value) - float(previous)) / float(previous)) * 100
    return (float(value) if value else None, change)


def parse_news() -> list[dict]:
    news: list[dict] = []
    seen: set[str] = set()
    for feed in RSS_FEEDS:
        try:
            xml_text = fetch_text(feed)
            root = ElementTree.fromstring(xml_text)
        except Exception:
            continue
        for item in root.findall(".//item"):
            title = (item.findtext("title") or "").strip()
            link = (item.findtext("link") or "").strip()
            if not title or title in seen:
                continue
            seen.add(title)
            news.append({"title": clean_title(title), "link": link})
            if len(news) >= 8:
                return news
    return news


def clean_title(title: str) -> str:
    title = re.sub(r"\s+-\s+[^-]+$", "", title)
    return title.strip()


def sentiment_score(news: list[dict]) -> int:
    score = 0
    text = " ".join(item["title"].lower() for item in news)
    for word in POSITIVE_WORDS:
        if word in text:
            score += 1
    for word in NEGATIVE_WORDS:
        if word in text:
            score -= 1
    return score


def recommendation(asset_id: str, change: float | None, mood_score: int) -> tuple[str, str, str]:
    change = 0.0 if change is None else change
    if asset_id in {"btc", "eth"}:
        if change > 4:
            return "rec-watch", "Aguardar", "Alta forte aumenta risco de comprar no impulso; entrada gradual e cautelosa faz mais sentido."
        if change < -4:
            return "rec-avoid", "Risco alto", "Queda forte indica volatilidade elevada; preserve caixa e evite concentrar recursos."
        return "rec-watch", "Moderado", "Cripto segue volatil; use apenas parcela pequena e compatível com seu perfil de risco."
    if asset_id in {"usdbrl", "eurbrl"}:
        if abs(change) < 0.35:
            return "rec-watch", "Neutro", "Cambio perto da estabilidade; compras parceladas reduzem o risco de acertar o pior momento."
        if change > 0:
            return "rec-watch", "Aguardar", "Moeda em alta encarece compras externas; vale acompanhar antes de comprar volume grande."
        return "rec-buy", "Oportunidade pontual", "Recuo do cambio pode favorecer compras planejadas, sem concentrar tudo em um dia."
    if asset_id == "selic":
        return "rec-buy", "Reserva", "Produto conservador e liquido costuma ser adequado para reserva de emergencia."
    if mood_score > 1:
        return "rec-buy", "Oportunidade", "Noticias recentes indicam humor mais positivo, mas a decisao ainda depende do seu prazo."
    if mood_score < -1:
        return "rec-avoid", "Cautela", "Noticias recentes mostram mais risco; reduzir pressa ajuda a evitar decisoes emocionais."
    return "rec-watch", "Neutro", "Cenario misto pede acompanhamento e aportes graduais."


def build_market() -> dict:
    errors: list[str] = []
    fx: dict[str, dict] = {}
    crypto: dict = {}
    selic: float | None = None
    cdi: float | None = None
    ibov: tuple[float | None, float | None] = (None, None)
    gold: tuple[float | None, float | None] = (None, None)
    news: list[dict] = []

    for label, func in (
        ("cambio", quote_fx),
        ("cripto", quote_crypto),
        ("selic", quote_selic),
        ("cdi", quote_cdi_annualized),
        ("ibovespa", lambda: quote_yahoo("^BVSP")),
        ("ouro", lambda: quote_yahoo("GC=F")),
        ("noticias", parse_news),
    ):
        try:
            value = func()
            if label == "cambio":
                fx = value
            elif label == "cripto":
                crypto = value
            elif label == "selic":
                selic = value
            elif label == "cdi":
                cdi = value
            elif label == "ibovespa":
                ibov = value
            elif label == "ouro":
                gold = value
            else:
                news = value
        except (URLError, TimeoutError, ValueError, KeyError, json.JSONDecodeError) as exc:
            errors.append(f"{label}: {exc}")

    mood_score = sentiment_score(news)
    mood_label = "otimista" if mood_score > 1 else "cauteloso" if mood_score < -1 else "neutro"
    assets = []

    for asset in ASSETS:
        value_label = "Atualizando"
        change = None
        if asset["kind"] == "fx":
            quote = fx.get(asset["pair"], {})
            if quote:
                value_label = brl(float(quote.get("bid", 0)))
                change = float(quote.get("pctChange", 0))
        elif asset["kind"] == "crypto":
            quote = crypto.get(asset["coin"], {})
            if quote:
                value_label = usd(float(quote.get("usd", 0)))
                change = float(quote.get("usd_24h_change", 0))
        elif asset["kind"] == "index":
            value, change = ibov
            if value is not None:
                value_label = points(value)
        elif asset["kind"] == "gold":
            value, change = gold
            if value is not None:
                value_label = usd(value)
        elif asset["kind"] == "rate" and selic is not None:
            value_label = f"{str(round(selic, 2)).replace('.', ',')}% a.a."
            change = None
        elif asset["kind"] == "cdi" and cdi is not None:
            value_label = f"{str(round(cdi, 2)).replace('.', ',')}% a.a."
            change = None

        rec_class, rec_label, reason = recommendation(asset["id"], change, mood_score)
        assets.append({
            "id": asset["id"],
            "name": asset["name"],
            "icon": asset["icon"],
            "quote": value_label,
            "change": pct(change),
            "direction": "up" if change and change > 0 else "down" if change and change < 0 else "flat",
            "recommendation": rec_label,
            "recommendation_class": rec_class,
            "reason": reason,
        })

    return {
        "generated_at": datetime.now(timezone.utc).isoformat(),
        "source_note": "Fontes publicas: AwesomeAPI, CoinGecko, Banco Central do Brasil e RSS de noticias.",
        "disclaimer": DISCLAIMER,
        "ticker": [
            {"symbol": "USD/BRL", "value": by_id(assets, "usdbrl")["quote"], "change": by_id(assets, "usdbrl")["change"], "direction": by_id(assets, "usdbrl")["direction"]},
            {"symbol": "EUR/BRL", "value": by_id(assets, "eurbrl")["quote"], "change": by_id(assets, "eurbrl")["change"], "direction": by_id(assets, "eurbrl")["direction"]},
            {"symbol": "IBOVESPA", "value": by_id(assets, "ibov")["quote"], "change": by_id(assets, "ibov")["change"], "direction": by_id(assets, "ibov")["direction"]},
            {"symbol": "BTC/USD", "value": by_id(assets, "btc")["quote"], "change": by_id(assets, "btc")["change"], "direction": by_id(assets, "btc")["direction"]},
            {"symbol": "ETH/USD", "value": by_id(assets, "eth")["quote"], "change": by_id(assets, "eth")["change"], "direction": by_id(assets, "eth")["direction"]},
            {"symbol": "OURO", "value": by_id(assets, "gold")["quote"], "change": by_id(assets, "gold")["change"], "direction": by_id(assets, "gold")["direction"]},
            {"symbol": "SELIC", "value": by_id(assets, "selic")["quote"], "change": "estavel", "direction": "flat"},
            {"symbol": "CDI", "value": by_id(assets, "cdi")["quote"], "change": "estavel", "direction": "flat"},
        ],
        "assets": assets,
        "news": news,
        "market_mood": {
            "label": mood_label.capitalize(),
            "summary": market_summary(mood_label, news),
            "score": mood_score,
        },
        "errors": errors,
    }


def by_id(assets: list[dict], asset_id: str) -> dict:
    return next(asset for asset in assets if asset["id"] == asset_id)


def market_summary(label: str, news: list[dict]) -> str:
    if not news:
        return "Sem noticias suficientes no momento; as recomendacoes ficam conservadoras ate a proxima atualizacao."
    if label == "otimista":
        return "Noticias recentes indicam melhora de apetite por risco, mas com necessidade de entrada gradual."
    if label == "cauteloso":
        return "O noticiario aponta mais incerteza; preservar liquidez e evitar concentracao ganha prioridade."
    return "O mercado mostra sinais mistos; diversificacao e compras parceladas reduzem o risco de timing."


def main() -> int:
    DATA_DIR.mkdir(exist_ok=True)
    payload = build_market()
    (DATA_DIR / "market.json").write_text(json.dumps(payload, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    if payload.get("errors"):
        print("Atualizado com avisos:", "; ".join(payload["errors"]), file=sys.stderr)
    else:
        print("market.json atualizado com sucesso.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
