package br.com.trcon.site.news.util;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.trcon.site.news.dto.response.NewsArticleResponse;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ArticlePageHtmlBuilderTest {

    @Test
    void deveIncluirMetaOgJsonLdECorpoNoHtmlInicial() {
        NewsArticleResponse article = fullArticle(
                "Como IA ajuda PMEs",
                "Resumo prático",
                "Parágrafo um.\n\nParágrafo dois.",
                "Como IA ajuda PMEs | TRCONGROUP",
                "Resumo prático para indexação.",
                "https://images.unsplash.com/photo-example",
                "Sirius Marketing",
                "trcon",
                Instant.parse("2026-08-16T12:00:00Z"));

        String html = ArticlePageHtmlBuilder.build(article, "https://trcongroup.com.br/");

        assertThat(html).contains("<title>Como IA ajuda PMEs | TRCONGROUP — TRCONGROUP</title>");
        assertThat(html).contains("name=\"description\"");
        assertThat(html).contains("Resumo prático para indexação.");
        assertThat(html).contains("rel=\"canonical\"");
        assertThat(html).contains("https://trcongroup.com.br/novidades/como-ia-ajuda-pmes");
        assertThat(html).contains("property=\"og:image\"");
        assertThat(html).contains("application/ld+json");
        assertThat(html).contains("\"@type\":\"NewsArticle\"");
        assertThat(html).contains("\"description\":\"Resumo prático para indexação.\"");
        assertThat(html).contains("datePublished");
        assertThat(html).contains("data-article-ssr=\"true\"");
        assertThat(html).contains("<h1>Como IA ajuda PMEs</h1>");
        assertThat(html).contains("<p class=\"article-summary\">Resumo prático</p>");
        assertThat(html).contains("Sirius Marketing");
        assertThat(html).contains("trcon");
        assertThat(html).contains("<p>Parágrafo um.</p>");
        assertThat(html).doesNotContain("Carregando");
    }

    @Test
    void baseNullUsaDefaultInstitucional() {
        NewsArticleResponse article = fullArticle(
                "T", null, "B", null, null, null, null, null, Instant.parse("2026-01-01T00:00:00Z"));
        String html = ArticlePageHtmlBuilder.build(article, null);
        assertThat(html).contains("https://trcongroup.com.br/novidades/como-ia-ajuda-pmes");
        assertThat(html).contains("<title>T — TRCONGROUP</title>");
        assertThat(html).doesNotContain("name=\"description\"");
        assertThat(html).doesNotContain("og:image");
        assertThat(html).doesNotContain("article-summary");
    }

    @Test
    void fallbacksDeTituloDescricaoECategoria() {
        NewsArticleResponse article = new NewsArticleResponse(
                UUID.randomUUID(),
                "  ",
                null,
                "Titulo H1",
                "  ",
                "https://trcongroup.com.br/novidades/x",
                "x",
                "Corpo",
                null,
                null,
                null,
                "   ",
                null);

        String html = ArticlePageHtmlBuilder.build(article, "https://trcongroup.com.br");
        assertThat(html).contains("<title>Titulo H1 — TRCONGROUP</title>");
        assertThat(html).contains("article-tag\">TRCONGROUP</span>");
        assertThat(html).doesNotContain("name=\"description\"");
        assertThat(html).doesNotContain("datePublished");
    }

    @Test
    void tituloNullUsaFallbackEditorial() {
        NewsArticleResponse article = new NewsArticleResponse(
                UUID.randomUUID(),
                null,
                "  ",
                null,
                null,
                "https://trcongroup.com.br/novidades/y",
                "y",
                null,
                "  ",
                "  ",
                null,
                null,
                Instant.parse("2026-08-01T15:00:00Z"));

        String html = ArticlePageHtmlBuilder.build(article, "https://trcongroup.com.br");
        assertThat(html).contains("<title>TRCONGROUP Novidades — TRCONGROUP</title>");
        assertThat(html).contains("<h1></h1>");
        assertThat(html).contains("Conteúdo indisponível");
        assertThat(html).contains("de 2026");
    }

    @Test
    void capaHttpEEmBrancoSaoIgnoradas() {
        NewsArticleResponse httpCover = fullArticle(
                "T", "S", "B", "T", "D", "http://example.com/a.jpg", null, null, Instant.now());
        assertThat(ArticlePageHtmlBuilder.build(httpCover, "https://trcongroup.com.br"))
                .doesNotContain("og:image");

        NewsArticleResponse blankCover = fullArticle(
                "T", "S", "B", "T", "D", "   ", null, null, Instant.now());
        assertThat(ArticlePageHtmlBuilder.build(blankCover, "https://trcongroup.com.br"))
                .doesNotContain("og:image");
    }

    @Test
    void capaUnsplashPaginaEConvertidaParaDownload() {
        NewsArticleResponse galleryCover = fullArticle(
                "T",
                "S",
                "B",
                "T",
                "D",
                "https://unsplash.com/photos/abc-ABC1234xyz",
                null,
                null,
                Instant.now());
        assertThat(ArticlePageHtmlBuilder.build(galleryCover, "https://trcongroup.com.br"))
                .contains("og:image")
                .contains("unsplash.com/photos/ABC1234xyz/download");
    }

    @Test
    void capaComExcecaoDeNormalizacaoEIgnorada() {
        // > 500 chars triggers IllegalArgumentException in CoverImageUrls.normalize
        String tooLong = "https://images.unsplash.com/" + "a".repeat(500);
        NewsArticleResponse article =
                fullArticle("T", "S", "B", "T", "D", tooLong, null, null, Instant.now());
        assertThat(ArticlePageHtmlBuilder.build(article, "https://trcongroup.com.br"))
                .doesNotContain("og:image");
    }

    @Test
    void notFoundDeveSerNoindex() {
        String html = ArticlePageHtmlBuilder.notFound("https://trcongroup.com.br/");
        assertThat(html).contains("noindex");
        assertThat(html).contains("Artigo não encontrado");
        assertThat(html).contains("https://trcongroup.com.br/");
    }

    @Test
    void notFoundComBaseNullUsaDefault() {
        String html = ArticlePageHtmlBuilder.notFound(null);
        assertThat(html).contains("https://trcongroup.com.br/");
        assertThat(html).contains("noindex");
    }

    private static NewsArticleResponse fullArticle(
            String title,
            String summary,
            String body,
            String metaTitle,
            String metaDescription,
            String cover,
            String source,
            String brandSlug,
            Instant publishedAt) {
        return new NewsArticleResponse(
                UUID.randomUUID(),
                source,
                "Tecnologia",
                title,
                summary,
                "https://trcongroup.com.br/novidades/como-ia-ajuda-pmes",
                "como-ia-ajuda-pmes",
                body,
                metaTitle,
                metaDescription,
                cover,
                brandSlug,
                publishedAt);
    }
}
