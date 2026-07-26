"""Catalogo estatico de dicas de educacao financeira (fallback Camada 4).

Usado quando feeds RSS falham ou retornam poucos itens relevantes. Mantem o
site sempre com conteudo util sem depender de rede externa.
"""

from __future__ import annotations

from datetime import datetime, timezone

TIPS: list[dict] = [
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
            {"label": "Reserva", "value": 20, "color": "var(--gold)"},
        ],
    },
    {
        "tag": "Cotidiano",
        "tag_class": "tag-orange",
        "title": "Revise assinaturas antes de cortar o essencial",
        "body": "Some streaming, apps, armazenamento, clubes e planos esquecidos. Cancelar dois servicos pouco usados costuma gerar economia sem reduzir qualidade de vida.",
        "meta": ["4 min", "Impacto imediato"],
        "url": "https://www.google.com/search?q=como+cancelar+assinaturas+e+economizar+dinheiro",
        "link_label": "Ver checklist",
    },
    {
        "tag": "Mercado",
        "tag_class": "tag-blue",
        "title": "Reserva de emergencia vem antes do risco",
        "body": "Antes de buscar rentabilidade alta, mantenha parte do dinheiro em produto conservador e liquido. Isso evita vender investimentos ruins em uma urgencia.",
        "meta": ["3 min", "Baixo risco"],
        "url": "https://www.google.com/search?q=como+montar+reserva+de+emergencia",
        "link_label": "Ver passo a passo",
    },
    {
        "tag": "Compras",
        "tag_class": "tag-gold",
        "title": "Compare preco por unidade, nao so o preco da embalagem",
        "body": "No supermercado, olhe o preco por quilo, litro ou unidade. Promocoes grandes nem sempre sao mais baratas e podem virar desperdicio.",
        "meta": ["3 min", "Economia recorrente"],
        "url": "https://www.google.com/search?q=como+comparar+preco+por+quilo+litro+unidade",
        "link_label": "Aprender a comparar",
    },
    {
        "tag": "Cartao",
        "tag_class": "tag-orange",
        "title": "Transforme o cartao em meio de pagamento, nao credito",
        "body": "Use o cartao com limite mensal definido e acompanhe a fatura toda semana. Parcelas pequenas somadas reduzem sua renda livre nos meses seguintes.",
        "meta": ["4 min", "Controle alto"],
        "url": "https://www.google.com/search?q=como+usar+cartao+de+credito+sem+se+endividar",
        "link_label": "Ver cuidados",
    },
    {
        "tag": "Renda Fixa",
        "tag_class": "tag-blue",
        "title": "Compare liquidez, imposto e prazo antes do rendimento",
        "body": "Um CDB maior pode render menos se prender seu dinheiro ou tiver prazo ruim. Para reserva, liquidez e seguranca pesam mais que poucos pontos percentuais.",
        "meta": ["5 min", "Decisao melhor"],
        "url": "https://www.google.com/search?q=como+comparar+CDB+liquidez+imposto+prazo",
        "link_label": "Ver comparativo",
    },
    {
        "tag": "Impostos",
        "tag_class": "tag-green",
        "title": "Organize comprovantes o ano todo para declarar sem pressa",
        "body": "Guarde recibos medicos, educacao, previdencia privada e informes bancarios em uma pasta digital. Isso evita erro, multa e perda de restituicao na declaracao.",
        "meta": ["6 min", "Planejamento anual"],
        "url": "https://www.google.com/search?q=documentos+declaracao+imposto+de+renda+pessoa+fisica",
        "link_label": "Ver lista",
    },
    {
        "tag": "Metas",
        "tag_class": "tag-green",
        "title": "Defina meta com valor e prazo, nao so intencao",
        "body": "Trocar 'quero economizar' por 'R$ 300 por mes por 12 meses' torna o objetivo mensuravel. Acompanhe no fim de cada mes e ajuste antes de desistir.",
        "meta": ["4 min", "Habitos"],
        "url": "https://www.google.com/search?q=como+definir+metas+financeiras+SMART",
        "link_label": "Montar meta",
    },
    {
        "tag": "Seguranca",
        "tag_class": "tag-orange",
        "title": "Desconfie de promessas de ganho rapido e garantido",
        "body": "Esquemas de alto retorno com pouco risco quase sempre escondem fraude ou falta de transparencia. Prefira produtos regulados e entenda onde seu dinheiro esta.",
        "meta": ["3 min", "Protecao"],
        "url": "https://www.google.com/search?q=golpes+financeiros+como+identificar",
        "link_label": "Ver alertas",
    },
    {
        "tag": "PIX",
        "tag_class": "tag-orange",
        "title": "Confira nome e CPF antes de confirmar qualquer PIX",
        "body": "Golpistas exploram urgencia e links falsos. Transfira apenas depois de validar o destinatario no app do banco e evite usar QR Code de origem desconhecida.",
        "meta": ["2 min", "Seguranca digital"],
        "url": "https://www.google.com/search?q=seguranca+PIX+golpes+como+evitar",
        "link_label": "Ver cuidados",
    },
    {
        "tag": "Mercado",
        "tag_class": "tag-blue",
        "title": "Diversifique antes de buscar o ativo da moda",
        "body": "Concentrar tudo em um unico investimento ou setor aumenta o risco. Mesmo com pouco capital, distribuir entre reserva, renda fixa e variavel reduz surpresas.",
        "meta": ["5 min", "Risco"],
        "url": "https://www.google.com/search?q=diversificacao+de+investimentos+iniciante",
        "link_label": "Entender diversificacao",
    },
    {
        "tag": "Familia",
        "tag_class": "tag-green",
        "title": "Converse sobre dinheiro em casa com clareza e frequencia",
        "body": "Alinhar prioridades entre quem divide contas evita gastos invisiveis. Uma conversa mensal de 20 minutos sobre metas e contas fixas ja melhora muito o controle.",
        "meta": ["4 min", "Comunicacao"],
        "url": "https://www.google.com/search?q=planejamento+financeiro+familiar+como+fazer",
        "link_label": "Ver roteiro",
    },
    {
        "tag": "Trabalho",
        "tag_class": "tag-gold",
        "title": "Separe conta pessoal de receitas extras e freelas",
        "body": "Quando o dinheiro extra cai na mesma conta do salario, some no consumo. Transferir parte imediatamente para reserva ou investimento protege o progresso.",
        "meta": ["3 min", "Organizacao"],
        "url": "https://www.google.com/search?q=como+organizar+renda+extra+freela",
        "link_label": "Ver dica",
    },
    {
        "tag": "Educacao",
        "tag_class": "tag-blue",
        "title": "Aprenda regra basica: juros compostos a seu favor",
        "body": "Pequenos aportes recorrentes por muitos anos tendem a superar aportes grandes esporadicos. Consistencia costuma pesar mais que tentar acertar o timing do mercado.",
        "meta": ["5 min", "Conceito"],
        "url": "https://www.google.com/search?q=juros+compostos+investimentos+longo+prazo",
        "link_label": "Simular efeito",
    },
    {
        "tag": "Contas",
        "tag_class": "tag-orange",
        "title": "Negocie tarifas bancarias antes de trocar de banco",
        "body": "Pacotes de servicos, anuidade e tarifas podem ser reduzidos com uma ligacao ou chat. Compare o custo total anual, nao apenas uma taxa isolada.",
        "meta": ["3 min", "Economia direta"],
        "url": "https://www.google.com/search?q=como+negociar+tarifas+bancarias",
        "link_label": "Ver passos",
    },
    {
        "tag": "Mercado",
        "tag_class": "tag-blue",
        "title": "Entenda a diferenca entre inflacao e taxa Selic",
        "body": "Rentabilidade real so aparece quando o retorno supera a inflacao. Em periodos de juros altos, comparar com o IPCA ajuda a ver se seu dinheiro esta ganhando poder de compra.",
        "meta": ["4 min", "Macro basico"],
        "url": "https://www.google.com/search?q=rentabilidade+real+inflacao+selic",
        "link_label": "Ler resumo",
    },
    {
        "tag": "Cotidiano",
        "tag_class": "tag-orange",
        "title": "Faca lista de compras e evite ir ao mercado com fome",
        "body": "Compras por impulso elevam a conta do mes. Planejar refeicoes da semana e levar lista reduz desperdicio e repeticao de itens caros ja em casa.",
        "meta": ["2 min", "Habito simples"],
        "url": "https://www.google.com/search?q=como+economizar+no+supermercado+lista+de+compras",
        "link_label": "Ver checklist",
    },
    {
        "tag": "Aposentadoria",
        "tag_class": "tag-blue",
        "title": "Comece cedo, mesmo com valores pequenos",
        "body": "Adiar a previdencia privada ou aportes longos encarece muito a meta. O importante e criar o habito cedo; o valor pode crescer conforme a renda aumenta.",
        "meta": ["5 min", "Longo prazo"],
        "url": "https://www.google.com/search?q=previdencia+privada+quando+comecar",
        "link_label": "Ver introducao",
    },
]


def pick_rotating(items: list[dict], count: int, day_seed: int | None = None) -> list[dict]:
    if not items:
        return []
    seed = day_seed if day_seed is not None else int(datetime.now(timezone.utc).strftime("%Y%j"))
    start = seed % len(items)
    rotated = items[start:] + items[:start]
    picked = [dict(item) for item in rotated[:count]]
    if picked:
        picked[0]["featured"] = True
    return picked
