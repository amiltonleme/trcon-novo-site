package br.com.trcon.site.news.util;

import br.com.trcon.site.news.dto.response.NewsItemResponse;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NewsFeedXmlSupportTest {

    @Test
    void escapeXmlCobreNuloECaracteresEspeciais() {
        assertThat(NewsFeedXmlSupport.escapeXml(null)).isEmpty();
        assertThat(NewsFeedXmlSupport.escapeXml("&<>\"'")).isEqualTo("&amp;&lt;&gt;&quot;&apos;");
    }

    @Test
    void articleUrlPrefereSlug() {
        NewsItemResponse comSlug =
                new NewsItemResponse(UUID.randomUUID(), "s", "IA", "t", "r", "https://x", "abc", null, Instant.now());
        assertThat(NewsFeedXmlSupport.articleUrl("https://trcongroup.com.br", comSlug))
                .isEqualTo("https://trcongroup.com.br/novidades/abc");

        NewsItemResponse semSlug =
                new NewsItemResponse(UUID.randomUUID(), "s", "IA", "t", "r", "https://x.com", null, null, Instant.now());
        assertThat(NewsFeedXmlSupport.articleUrl("https://trcongroup.com.br", semSlug)).isEqualTo("https://x.com");
    }

    @Test
    void appendUrlOpcionalLastmod() {
        StringBuilder with = new StringBuilder();
        NewsFeedXmlSupport.appendUrl(with, "https://a", Instant.parse("2026-07-01T12:00:00Z"));
        assertThat(with).contains("<lastmod>2026-07-01</lastmod>");

        StringBuilder without = new StringBuilder();
        NewsFeedXmlSupport.appendUrl(without, "https://a", null);
        assertThat(without.toString()).doesNotContain("lastmod");
    }
}
