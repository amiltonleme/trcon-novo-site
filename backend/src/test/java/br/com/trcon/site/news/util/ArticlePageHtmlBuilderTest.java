package br.com.trcon.site.news.util;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.trcon.site.news.dto.response.NewsArticleResponse;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ArticlePageHtmlBuilderTest {

    @Test
    void deveIncluirMetaOgJsonLdECorpoNoHtmlInicial() {
        NewsArticleResponse article = new NewsArticleResponse(
                UUID.randomUUID(),
                "Sirius Marketing",
                "Tecnologia",
                "Como IA ajuda PMEs",
                "Resumo prático",
                "https://trcongroup.com.br/novidades/como-ia-ajuda-pmes",
                "como-ia-ajuda-pmes",
                "Parágrafo um.\n\nParágrafo dois.",
                "Como IA ajuda PMEs | TRCon",
                "Resumo prático para indexação.",
                "https://images.unsplash.com/photo-example",
                "trcon",
                Instant.parse("2026-08-16T12:00:00Z"));

        String html = ArticlePageHtmlBuilder.build(article, "https://trcongroup.com.br");

        assertThat(html).contains("<title>Como IA ajuda PMEs | TRCon — TRCon Group</title>");
        assertThat(html).contains("name=\"description\"");
        assertThat(html).contains("Resumo prático para indexação.");
        assertThat(html).contains("rel=\"canonical\"");
        assertThat(html).contains("https://trcongroup.com.br/novidades/como-ia-ajuda-pmes");
        assertThat(html).contains("property=\"og:image\"");
        assertThat(html).contains("application/ld+json");
        assertThat(html).contains("\"@type\":\"NewsArticle\"");
        assertThat(html).contains("data-article-ssr=\"true\"");
        assertThat(html).contains("<h1>Como IA ajuda PMEs</h1>");
        assertThat(html).contains("<p>Parágrafo um.</p>");
        assertThat(html).doesNotContain("Carregando");
    }

    @Test
    void notFoundDeveSerNoindex() {
        String html = ArticlePageHtmlBuilder.notFound("https://trcongroup.com.br");
        assertThat(html).contains("noindex");
        assertThat(html).contains("Artigo não encontrado");
    }
}
