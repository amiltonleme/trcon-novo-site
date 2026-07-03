"""Testes do pipeline de conteudo (stdlib unittest, sem rede).

Cobrem as funcoes puras (text), o parsing de RSS, o provider com fetcher
injetado (sem rede) e os builders. Rodar: `python -m unittest discover -s scripts`.
"""

from __future__ import annotations

import sys
import unittest
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))

from builders.home_builder import build_home_highlights, build_news_log
from builders.radar_builder import RadarConfig, build_radar
from core import text
from core.rss import parse_rss
from providers.rss_provider import RssProvider


SAMPLE_RSS = """<?xml version="1.0"?>
<rss version="2.0"><channel>
  <item>
    <title>Empresa lanca modelo de IA - TechVeiculo</title>
    <link>https://ex.com/ia-1</link>
    <source>TechVeiculo</source>
    <pubDate>Mon, 01 Jul 2026 08:00:00 GMT</pubDate>
  </item>
  <item>
    <title>Startup investe em automacao com inteligencia artificial - Portal</title>
    <link>https://ex.com/ia-2</link>
    <pubDate>Mon, 01 Jul 2026 07:00:00 GMT</pubDate>
  </item>
</channel></rss>"""


class TextTests(unittest.TestCase):
    def test_clean_title_removes_source_suffix(self):
        self.assertEqual(text.clean_title("Titulo bom - Veiculo X"), "Titulo bom")

    def test_clean_title_keeps_plain(self):
        self.assertEqual(text.clean_title("Sem sufixo"), "Sem sufixo")

    def test_normalize_removes_accents(self):
        self.assertEqual(text.normalize("Inteligência"), "inteligencia")

    def test_keyword_score_counts_matches(self):
        score = text.keyword_score("IA e automacao", {"ia", "automacao", "cloud"})
        self.assertEqual(score, 2)

    def test_classify_signal(self):
        self.assertEqual(text.classify_signal("empresa avanca e cresce", {"avanca", "cresce"}, {"queda"}), "up")
        self.assertEqual(text.classify_signal("mercado em queda", {"alta"}, {"queda"}), "down")
        self.assertEqual(text.classify_signal("noticia neutra", {"alta"}, {"queda"}), "flat")

    def test_summarize_truncates(self):
        long = "palavra " * 60
        out = text.summarize(long, limit=50)
        self.assertLessEqual(len(out), 53)
        self.assertTrue(out.endswith("..."))

    def test_dedup_by_title(self):
        items = [{"title": "A"}, {"title": "a"}, {"title": "B"}]
        self.assertEqual(len(text.dedup_by_title(items)), 2)


class RssTests(unittest.TestCase):
    def test_parse_rss_extracts_items(self):
        items = parse_rss(SAMPLE_RSS)
        self.assertEqual(len(items), 2)
        self.assertEqual(items[0]["url"], "https://ex.com/ia-1")
        self.assertEqual(items[0]["source"], "TechVeiculo")

    def test_parse_rss_uses_fallback_source(self):
        items = parse_rss(SAMPLE_RSS, source_fallback="FB")
        self.assertEqual(items[1]["source"], "FB")

    def test_parse_rss_invalid_returns_empty(self):
        self.assertEqual(parse_rss("<not xml"), [])


class ProviderTests(unittest.TestCase):
    def test_provider_aggregates_and_cleans(self):
        provider = RssProvider("Fake", ["feed-a"], fetcher=lambda url: SAMPLE_RSS)
        items = provider.fetch(limit=10)
        self.assertEqual(len(items), 2)
        # titulo teve o sufixo " - Veiculo" removido
        self.assertNotIn(" - ", items[0]["title"])
        self.assertEqual(provider.errors, [])

    def test_provider_records_error_on_failure(self):
        def boom(url):
            raise TimeoutError("timeout")

        provider = RssProvider("Fake", ["feed-a"], fetcher=boom)
        items = provider.fetch(limit=10)
        self.assertEqual(items, [])
        self.assertEqual(len(provider.errors), 1)

    def test_provider_degrades_partial(self):
        calls = {"n": 0}

        def half(url):
            calls["n"] += 1
            if calls["n"] == 1:
                raise ConnectionError("down")
            return SAMPLE_RSS

        provider = RssProvider("Fake", ["a", "b"], fetcher=half)
        items = provider.fetch(limit=10)
        self.assertEqual(len(items), 2)  # segundo feed funcionou
        self.assertEqual(len(provider.errors), 1)


class BuilderTests(unittest.TestCase):
    def _provider(self):
        return RssProvider("Fake", ["feed"], fetcher=lambda url: SAMPLE_RSS)

    def test_build_radar_shape_and_ranking(self):
        cfg = RadarConfig(
            category="IA",
            relevance_keywords={"ia", "inteligencia artificial", "automacao"},
            positive_words={"lanca", "investe"},
            negative_words={"queda"},
            max_items=5,
        )
        payload = build_radar(self._provider(), cfg)
        self.assertEqual(payload["category"], "IA")
        self.assertIn("generated_at", payload)
        self.assertEqual(payload["errors"], [])
        self.assertTrue(payload["items"])
        first = payload["items"][0]
        for key in ("title", "summary", "url", "source", "published_at", "category", "signal"):
            self.assertIn(key, first)

    def test_home_builders_consolidate(self):
        cfg = RadarConfig(category="IA", relevance_keywords={"ia"})
        radar = build_radar(self._provider(), cfg)
        highlights = build_home_highlights([radar])
        news = build_news_log([radar])

        self.assertTrue(highlights["items"])
        self.assertEqual(highlights["items"][0]["priority"], 1)
        self.assertIn("link", highlights["items"][0])
        # shape camelCase espelha os contratos do backend
        self.assertIn("publishedAt", highlights["items"][0])
        self.assertTrue(news["items"])
        self.assertIn("url", news["items"][0])
        self.assertIn("publishedAt", news["items"][0])

    def test_home_builder_propagates_errors(self):
        radar = {"items": [], "errors": ["feed X caiu"], "category": "IA"}
        highlights = build_home_highlights([radar])
        self.assertIn("feed X caiu", highlights["errors"])


if __name__ == "__main__":
    unittest.main()
