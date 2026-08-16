package br.com.trcon.site.news.util;

import br.com.trcon.site.news.dto.response.NewsArticleResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Monta o HTML completo da página `/novidades/{slug}` (meta, OG, JSON-LD e corpo). */
public final class ArticlePageHtmlBuilder {

    private static final DateTimeFormatter DATE_PT =
            DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", Locale.forLanguageTag("pt-BR"));
    private static final ObjectMapper JSON = new ObjectMapper();

    private ArticlePageHtmlBuilder() {}

    public static String build(NewsArticleResponse article, String publicBaseUrl) {
        String base = publicBaseUrl == null ? "https://trcongroup.com.br" : publicBaseUrl.replaceAll("/+$", "");
        String title = firstNonBlank(article.metaTitle(), article.title(), "TRCon Novidades");
        String description = firstNonBlank(article.metaDescription(), article.summary(), "");
        String canonical = base + "/novidades/" + article.slug();
        String cover = safeCover(article.coverImageUrl());
        String category = firstNonBlank(article.category(), "TRCon");
        String dateLabel = formatDate(article.publishedAt());
        String bodyHtml = ArticleBodyHtmlRenderer.render(article.body());
        String jsonLd = buildJsonLd(article, title, description, canonical, cover, base);

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html lang=\"pt-BR\">\n<head>\n");
        html.append("  <meta charset=\"UTF-8\" />\n");
        html.append("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n");
        html.append("  <title>")
                .append(ArticleBodyHtmlRenderer.escapeHtml(title))
                .append(" — TRCon Group</title>\n");
        if (!description.isBlank()) {
            html.append("  <meta name=\"description\" content=\"")
                    .append(ArticleBodyHtmlRenderer.escapeHtml(description))
                    .append("\" />\n");
        }
        html.append("  <link rel=\"canonical\" href=\"")
                .append(ArticleBodyHtmlRenderer.escapeHtml(canonical))
                .append("\" />\n");
        html.append("  <meta property=\"og:site_name\" content=\"TRCon Group\" />\n");
        html.append("  <meta property=\"og:type\" content=\"article\" />\n");
        html.append("  <meta property=\"og:title\" content=\"")
                .append(ArticleBodyHtmlRenderer.escapeHtml(title))
                .append("\" />\n");
        if (!description.isBlank()) {
            html.append("  <meta property=\"og:description\" content=\"")
                    .append(ArticleBodyHtmlRenderer.escapeHtml(description))
                    .append("\" />\n");
        }
        html.append("  <meta property=\"og:url\" content=\"")
                .append(ArticleBodyHtmlRenderer.escapeHtml(canonical))
                .append("\" />\n");
        if (cover != null) {
            html.append("  <meta property=\"og:image\" content=\"")
                    .append(ArticleBodyHtmlRenderer.escapeHtml(cover))
                    .append("\" />\n");
        }
        html.append("  <script type=\"application/ld+json\">")
                .append(jsonLd)
                .append("</script>\n");
        html.append("  <link rel=\"preconnect\" href=\"https://fonts.googleapis.com\">\n");
        html.append("  <link href=\"https://fonts.googleapis.com/css2?family=Syne:wght@400;600;700;800")
                .append("&family=DM+Sans:ital,wght@0,300;0,400;0,500;1,300")
                .append("&family=Orbitron:wght@400;700;900&display=swap\" rel=\"stylesheet\">\n");
        html.append("  <link rel=\"icon\" href=\"/assets/brand/trcon-mark.svg\" type=\"image/svg+xml\">\n");
        html.append("  <link rel=\"stylesheet\" href=\"/style.css\">\n");
        html.append("</head>\n");
        html.append("<body class=\"article-page\" data-article-ssr=\"true\">\n");
        html.append("<nav>\n");
        html.append("  <a href=\"/\" class=\"logo\">");
        html.append("<span class=\"logo-text\"><span class=\"logo-text-tr\">TR</span>");
        html.append("<span class=\"logo-text-rest\">CONGROUP</span></span></a>\n");
        html.append("  <ul class=\"nav-links\"><li><a href=\"/\">Home</a></li></ul>\n");
        html.append("</nav>\n");
        html.append("<main class=\"article-shell\">\n");
        html.append("  <div class=\"article-root\">\n");
        html.append("    <nav class=\"article-breadcrumb\" aria-label=\"Breadcrumb\">");
        html.append("<a href=\"/\">Home</a><span aria-hidden=\"true\">→</span><span>Novidades</span></nav>\n");
        if (cover != null) {
            html.append("    <figure class=\"article-cover\"><img src=\"")
                    .append(ArticleBodyHtmlRenderer.escapeHtml(cover))
                    .append("\" alt=\"\" loading=\"eager\" decoding=\"async\" /></figure>\n");
        }
        html.append("    <header class=\"article-header\">\n");
        html.append("      <span class=\"article-tag\">")
                .append(ArticleBodyHtmlRenderer.escapeHtml(category))
                .append("</span>\n");
        html.append("      <h1>")
                .append(ArticleBodyHtmlRenderer.escapeHtml(nullToEmpty(article.title())))
                .append("</h1>\n");
        if (article.summary() != null && !article.summary().isBlank()) {
            html.append("      <p class=\"article-summary\">")
                    .append(ArticleBodyHtmlRenderer.escapeHtml(article.summary()))
                    .append("</p>\n");
        }
        html.append("      <div class=\"article-meta\">\n");
        if (!dateLabel.isBlank()) {
            html.append("        <span>")
                    .append(ArticleBodyHtmlRenderer.escapeHtml(dateLabel))
                    .append("</span>\n");
        }
        if (article.source() != null && !article.source().isBlank()) {
            html.append("        <span>")
                    .append(ArticleBodyHtmlRenderer.escapeHtml(article.source()))
                    .append("</span>\n");
        }
        if (article.brandSlug() != null && !article.brandSlug().isBlank()) {
            html.append("        <span>")
                    .append(ArticleBodyHtmlRenderer.escapeHtml(article.brandSlug()))
                    .append("</span>\n");
        }
        html.append("      </div>\n");
        html.append("    </header>\n");
        html.append("    <article class=\"article-body\">").append(bodyHtml).append("</article>\n");
        html.append("  </div>\n");
        html.append("</main>\n");
        html.append("</body>\n</html>\n");
        return html.toString();
    }

    public static String notFound(String publicBaseUrl) {
        String base = publicBaseUrl == null ? "https://trcongroup.com.br" : publicBaseUrl.replaceAll("/+$", "");
        return "<!DOCTYPE html>\n<html lang=\"pt-BR\"><head>"
                + "<meta charset=\"UTF-8\" />"
                + "<meta name=\"robots\" content=\"noindex\" />"
                + "<title>Artigo não encontrado — TRCon Group</title>"
                + "<link rel=\"stylesheet\" href=\"/style.css\">"
                + "</head><body class=\"article-page\"><main class=\"article-shell\">"
                + "<p class=\"article-error\">Não foi possível carregar este artigo.</p>"
                + "<p><a href=\"" + ArticleBodyHtmlRenderer.escapeHtml(base) + "/\">Voltar à home</a></p>"
                + "</main></body></html>";
    }

    private static String buildJsonLd(
            NewsArticleResponse article,
            String title,
            String description,
            String canonical,
            String cover,
            String base) {
        try {
            ObjectNode root = JSON.createObjectNode();
            root.put("@context", "https://schema.org");
            root.put("@type", "NewsArticle");
            root.put("headline", title);
            if (!description.isBlank()) {
                root.put("description", description);
            }
            if (cover != null) {
                root.putArray("image").add(cover);
            }
            if (article.publishedAt() != null) {
                root.put("datePublished", article.publishedAt().toString());
                root.put("dateModified", article.publishedAt().toString());
            }
            ObjectNode author = root.putObject("author");
            author.put("@type", "Organization");
            author.put("name", "TRCon Group");
            ObjectNode publisher = root.putObject("publisher");
            publisher.put("@type", "Organization");
            publisher.put("name", "TRCon Group");
            publisher.put("url", base);
            ObjectNode main = root.putObject("mainEntityOfPage");
            main.put("@type", "WebPage");
            main.put("@id", canonical);
            return JSON.writeValueAsString(root);
        } catch (JsonProcessingException ex) {
            return "{}";
        }
    }

    private static String safeCover(String coverImageUrl) {
        if (coverImageUrl == null || coverImageUrl.isBlank()) {
            return null;
        }
        String trimmed = coverImageUrl.trim();
        if (!trimmed.startsWith("https://")) {
            return null;
        }
        try {
            return CoverImageUrls.normalize(trimmed);
        } catch (RuntimeException ex) {
            return null;
        }
    }

    private static String formatDate(Instant publishedAt) {
        if (publishedAt == null) {
            return "";
        }
        return DATE_PT.format(publishedAt.atZone(ZoneId.of("America/Sao_Paulo")));
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return "";
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
