#!/usr/bin/env python3
"""Generate daily economy tips and budget recipes for the static site."""

from __future__ import annotations

import json
from datetime import datetime, timezone
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
DATA_DIR = ROOT / "data"


TIPS = [
    {
        "tag": "Estrategia Mensal",
        "tag_class": "tag-green",
        "title": "Use um teto semanal para gastos variaveis",
        "body": "Separe mercado, transporte, delivery e lazer em um limite por semana. O controle fica mais facil porque voce corrige a rota antes do fim do mes.",
        "meta": ["5 min de leitura", "Impacto alto"],
        "url": "https://www.google.com/search?q=como+criar+orcamento+semanal+gastos+variaveis",
        "link_label": "Ver guia",
        "featured": True,
        "chart": [
            {"label": "Contas fixas", "value": 50, "color": "var(--accent)"},
            {"label": "Variaveis", "value": 30, "color": "var(--accent2)"},
            {"label": "Reserva", "value": 20, "color": "var(--gold)"}
        ]
    },
    {
        "tag": "Cotidiano",
        "tag_class": "tag-orange",
        "title": "Revise assinaturas antes de cortar o essencial",
        "body": "Some streaming, apps, armazenamento, clubes e planos esquecidos. Cancelar dois servicos pouco usados costuma gerar economia sem reduzir qualidade de vida.",
        "meta": ["4 min", "Impacto imediato"],
        "url": "https://www.google.com/search?q=como+cancelar+assinaturas+e+economizar+dinheiro",
        "link_label": "Ver checklist"
    },
    {
        "tag": "Mercado",
        "tag_class": "tag-blue",
        "title": "Reserva de emergencia vem antes do risco",
        "body": "Antes de buscar rentabilidade alta, mantenha parte do dinheiro em produto conservador e liquido. Isso evita vender investimentos ruins em uma urgencia.",
        "meta": ["3 min", "Baixo risco"],
        "url": "https://www.google.com/search?q=como+montar+reserva+de+emergencia",
        "link_label": "Ver passo a passo"
    },
    {
        "tag": "Compras",
        "tag_class": "tag-gold",
        "title": "Compare preco por unidade, nao so o preco da embalagem",
        "body": "No supermercado, olhe o preco por quilo, litro ou unidade. Promocoes grandes nem sempre sao mais baratas e podem virar desperdicio.",
        "meta": ["3 min", "Economia recorrente"],
        "url": "https://www.google.com/search?q=como+comparar+preco+por+quilo+litro+unidade",
        "link_label": "Aprender a comparar"
    },
    {
        "tag": "Cartao",
        "tag_class": "tag-orange",
        "title": "Transforme o cartao em meio de pagamento, nao credito",
        "body": "Use o cartao com limite mensal definido e acompanhe a fatura toda semana. Parcelas pequenas somadas reduzem sua renda livre nos meses seguintes.",
        "meta": ["4 min", "Controle alto"],
        "url": "https://www.google.com/search?q=como+usar+cartao+de+credito+sem+se+endividar",
        "link_label": "Ver cuidados"
    },
    {
        "tag": "Renda Fixa",
        "tag_class": "tag-blue",
        "title": "Compare liquidez, imposto e prazo antes do rendimento",
        "body": "Um CDB maior pode render menos se prender seu dinheiro ou tiver prazo ruim. Para reserva, liquidez e seguranca pesam mais que poucos pontos percentuais.",
        "meta": ["5 min", "Decisao melhor"],
        "url": "https://www.google.com/search?q=como+comparar+CDB+liquidez+imposto+prazo",
        "link_label": "Ver comparativo"
    }
]


RECIPES = [
    {
        "emoji": "🍝",
        "title": "Macarrao alho e oleo com legumes",
        "body": "Base barata, rapida e flexivel. Use cenoura, abobrinha ou sobras da geladeira para aumentar rendimento.",
        "meta": ["20min", "4 porcoes", "Facil"],
        "url": "https://www.google.com/search?q=receita+macarrao+alho+e+oleo+com+legumes",
        "link_label": "Ver receita",
        "gradient": "linear-gradient(135deg,#1a2535,#0d1219)"
    },
    {
        "emoji": "🥘",
        "title": "Arroz, feijao e ovo cremoso",
        "body": "Prato completo, proteico e barato. Funciona bem para marmitas e reduz pedidos de delivery durante a semana.",
        "meta": ["25min", "3 porcoes", "Facil"],
        "url": "https://www.google.com/search?q=receita+arroz+feijao+ovo+cremoso",
        "link_label": "Ver receita",
        "gradient": "linear-gradient(135deg,#1f1a10,#0d1219)"
    },
    {
        "emoji": "🥗",
        "title": "Salada reforcada com frango desfiado",
        "body": "Boa para aproveitar frango cozido, arroz ou graos. Leve, nutritiva e mais barata que almoço fora.",
        "meta": ["15min", "2 porcoes", "Facil"],
        "url": "https://www.google.com/search?q=receita+salada+reforcada+frango+desfiado",
        "link_label": "Ver receita",
        "gradient": "linear-gradient(135deg,#101a1a,#0d1219)"
    },
    {
        "emoji": "🍌",
        "title": "Bolo de banana madura",
        "body": "Aproveita bananas que iriam para o lixo e vira lanche da semana. Pode ser feito com aveia para render mais.",
        "meta": ["45min", "8 fatias", "Medio"],
        "url": "https://www.google.com/search?q=receita+bolo+de+banana+madura+com+aveia",
        "link_label": "Ver receita",
        "gradient": "linear-gradient(135deg,#1a1020,#0d1219)"
    },
    {
        "emoji": "🍲",
        "title": "Sopa de legumes com lentilha",
        "body": "Rende bastante, congela bem e substitui refeicoes caras nos dias corridos.",
        "meta": ["35min", "5 porcoes", "Facil"],
        "url": "https://www.google.com/search?q=receita+sopa+de+legumes+com+lentilha",
        "link_label": "Ver receita",
        "gradient": "linear-gradient(135deg,#162015,#0d1219)"
    },
    {
        "emoji": "🌮",
        "title": "Panqueca de frango com sobras",
        "body": "Transforma pequenas sobras em refeicao nova. A massa leva poucos ingredientes e rende varias unidades.",
        "meta": ["40min", "6 unidades", "Medio"],
        "url": "https://www.google.com/search?q=receita+panqueca+de+frango+com+sobras",
        "link_label": "Ver receita",
        "gradient": "linear-gradient(135deg,#201610,#0d1219)"
    }
]


def pick_rotating(items: list[dict], count: int, day_seed: int) -> list[dict]:
    if not items:
        return []
    start = day_seed % len(items)
    rotated = items[start:] + items[:start]
    return rotated[:count]


def write_json(path: Path, payload: dict) -> None:
    path.write_text(json.dumps(payload, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")


def main() -> None:
    DATA_DIR.mkdir(exist_ok=True)
    now = datetime.now(timezone.utc)
    day_seed = int(now.strftime("%Y%j"))

    tips = pick_rotating(TIPS, 4, day_seed)
    if tips:
        tips[0] = {**tips[0], "featured": True}

    write_json(DATA_DIR / "economy-tips.json", {"generated_at": now.isoformat(), "items": tips})
    write_json(DATA_DIR / "recipes.json", {"generated_at": now.isoformat(), "items": pick_rotating(RECIPES, 4, day_seed + 2)})


if __name__ == "__main__":
    main()
