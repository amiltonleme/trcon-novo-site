package br.com.trcon.site.news.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ArticleBodyHtmlRendererTest {

    @Test
    void deveEscaparHtmlERenderizarParagrafos() {
        String html = ArticleBodyHtmlRenderer.render("Linha 1\n\nLinha <b>2</b>");
        assertThat(html).contains("<p>Linha 1</p>");
        assertThat(html).contains("&lt;b&gt;");
    }

    @Test
    void deveRenderizarNegritoELista() {
        String html = ArticleBodyHtmlRenderer.render(
                "O desafio\n\n**Estratégias práticas:**\n\n- Definir prioridades claras\n- Medir resultados");
        assertThat(html).contains("<strong>Estratégias práticas:</strong>");
        assertThat(html).contains("<ul>");
        assertThat(html).contains("<li>Definir prioridades claras</li>");
        assertThat(html).contains("<li>Medir resultados</li>");
    }

    @Test
    void deveAceitarListaComAsterisco() {
        String html = ArticleBodyHtmlRenderer.render("* Item A\n* Item B");
        assertThat(html).contains("<ul>");
        assertThat(html).contains("<li>Item A</li>");
    }

    @Test
    void listaSoComMarcadoresVaziosNaoGeraUl() {
        assertThat(ArticleBodyHtmlRenderer.render("-\n*")).isEmpty();
    }

    @Test
    void listaComItemVazioAposMarcadorNaoGeraLiVazio() {
        String html = ArticleBodyHtmlRenderer.render("-\n- Ok\n-   ");
        assertThat(html).contains("<ul>");
        assertThat(html).contains("<li>Ok</li>");
        assertThat(html).doesNotContain("<li></li>");
    }

    @Test
    void blocoMistoNaoELista() {
        String html = ArticleBodyHtmlRenderer.render("- item\ntexto solto");
        assertThat(html).doesNotContain("<ul>");
        assertThat(html).contains("<p>");
    }

    @Test
    void paragrafosComLinhaDeVideoNoMeio() {
        String html = ArticleBodyHtmlRenderer.render(
                "Intro\nhttps://www.youtube.com/watch?v=dQw4w9WgXcQ\nFim");
        assertThat(html).contains("youtube.com/embed/dQw4w9WgXcQ");
        assertThat(html).contains("Intro");
        assertThat(html).contains("Fim");
    }

    @Test
    void linhaUnicaSoVideoViraIframe() {
        String html = ArticleBodyHtmlRenderer.render("https://youtu.be/abc123XYZ");
        assertThat(html).startsWith("<div class=\"article-video\">");
        assertThat(html).contains("youtube.com/embed/abc123XYZ");
    }

    @Test
    void linhasEmBrancoDentroDoBlocoSaoIgnoradas() {
        String html = ArticleBodyHtmlRenderer.render("A\n\n\nB");
        // dois blocos separados por linhas em branco
        assertThat(html).contains("<p>A</p>");
        assertThat(html).contains("<p>B</p>");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\n\t"})
    void corpoVazioUsaPlaceholder(String body) {
        assertThat(ArticleBodyHtmlRenderer.render(body)).contains("Conteúdo indisponível");
    }

    @Test
    void escapeHtmlNullECaracteresEspeciais() {
        assertThat(ArticleBodyHtmlRenderer.escapeHtml(null)).isEmpty();
        assertThat(ArticleBodyHtmlRenderer.escapeHtml("&<>\"'"))
                .isEqualTo("&amp;&lt;&gt;&quot;&#39;");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "ftp://x", "not-a-url", "https://example.com/video"})
    void videoEmbedSrcRejeitaInvalidos(String value) {
        assertThat(ArticleBodyHtmlRenderer.videoEmbedSrc(value)).isNull();
    }

    @ParameterizedTest
    @CsvSource({
        "https://youtu.be/abc_12-3, https://www.youtube.com/embed/abc_12-3",
        "https://www.youtube.com/watch?v=dQw4w9WgXcQ, https://www.youtube.com/embed/dQw4w9WgXcQ",
        "http://m.youtube.com/watch?v=abc123, https://www.youtube.com/embed/abc123",
        "https://youtube-nocookie.com/embed/xyz789, https://www.youtube.com/embed/xyz789",
        "https://www.youtube.com/shorts/shortId1, https://www.youtube.com/embed/shortId1",
        "https://vimeo.com/123456789, https://player.vimeo.com/video/123456789",
        "https://player.vimeo.com/video/987654321, https://player.vimeo.com/video/987654321"
    })
    void videoEmbedSrcAceitaHostsConhecidos(String input, String expected) {
        assertThat(ArticleBodyHtmlRenderer.videoEmbedSrc(input)).isEqualTo(expected);
    }

    @Test
    void youtubeSemParametroVRetornaNull() {
        assertThat(ArticleBodyHtmlRenderer.videoEmbedSrc("https://www.youtube.com/watch?foo=bar"))
                .isNull();
    }

    @Test
    void youtuBeSemIdRetornaNull() {
        assertThat(ArticleBodyHtmlRenderer.videoEmbedSrc("https://youtu.be/")).isNull();
    }

    @Test
    void vimeoNaoNumericoRetornaNull() {
        assertThat(ArticleBodyHtmlRenderer.videoEmbedSrc("https://vimeo.com/abc")).isNull();
        assertThat(ArticleBodyHtmlRenderer.videoEmbedSrc("https://player.vimeo.com/video/abc")).isNull();
    }

    @Test
    void playerVimeoSemVideoPathRetornaNull() {
        assertThat(ArticleBodyHtmlRenderer.videoEmbedSrc("https://player.vimeo.com/other/1")).isNull();
    }

    @Test
    void youtubeEmbedPathIncompletoRetornaNull() {
        assertThat(ArticleBodyHtmlRenderer.videoEmbedSrc("https://www.youtube.com/embed/")).isNull();
        assertThat(ArticleBodyHtmlRenderer.videoEmbedSrc("https://www.youtube.com/shorts/")).isNull();
    }

    @Test
    void uriInvalidaRetornaNull() {
        assertThat(ArticleBodyHtmlRenderer.videoEmbedSrc("https://[invalid")).isNull();
    }

    @Test
    void mailtoNaoEVideo() {
        assertThat(ArticleBodyHtmlRenderer.videoEmbedSrc("mailto:a@b.com")).isNull();
    }

    @Test
    void deveConverterYoutubeEmIframeNoCorpo() {
        String html = ArticleBodyHtmlRenderer.render(
                "Intro\n\nhttps://www.youtube.com/watch?v=dQw4w9WgXcQ\n\nFim");
        assertThat(html).contains("youtube.com/embed/dQw4w9WgXcQ");
        assertThat(html).contains("iframe");
    }

    @Test
    void deveRenderizarCorpoQueJaVemEmHtmlSemEscapar() {
        String html = ArticleBodyHtmlRenderer.render(
                "<h2>Introdução</h2><p>Texto <strong>em negrito</strong>.</p><ul><li>Item 1</li><li>Item 2</li></ul>");
        assertThat(html).contains("<h2>Introdução</h2>");
        assertThat(html).contains("<p>Texto <strong>em negrito</strong>.</p>");
        assertThat(html).contains("<ul><li>Item 1</li><li>Item 2</li></ul>");
        assertThat(html).doesNotContain("&lt;h2&gt;");
    }

    @Test
    void deveSanitizarCorpoHtmlRemovendoScriptsETagsForaDaAllowlist() {
        String html = ArticleBodyHtmlRenderer.render(
                "<h2>Título</h2><script>alert(1)</script><p onclick=\"alert(1)\">Texto</p><div>Bloco</div>");
        assertThat(html).doesNotContain("<script");
        assertThat(html).doesNotContain("alert(1)");
        assertThat(html).doesNotContain("onclick");
        assertThat(html).contains("<p>Texto</p>");
        assertThat(html).contains("Bloco");
        assertThat(html).doesNotContain("<div>");
    }

    @Test
    void isHtmlArticleBodyDetectaCorpoIniciadoPorTagDeBloco() {
        assertThat(ArticleBodyHtmlRenderer.isHtmlArticleBody("<h2>Título</h2><p>Texto</p>")).isTrue();
        assertThat(ArticleBodyHtmlRenderer.isHtmlArticleBody("  <p>Texto</p>")).isTrue();
        assertThat(ArticleBodyHtmlRenderer.isHtmlArticleBody("Linha 1\n\nLinha <b>2</b>")).isFalse();
        assertThat(ArticleBodyHtmlRenderer.isHtmlArticleBody("**negrito** e texto")).isFalse();
        assertThat(ArticleBodyHtmlRenderer.isHtmlArticleBody(null)).isFalse();
    }

    @Test
    void sanitizeArticleHtmlMantemTagsDaAllowlistEAtributosSeguros() {
        String html = ArticleBodyHtmlRenderer.sanitizeArticleHtml(
                "<h2>Título</h2><p>Texto <strong>forte</strong> e <a href=\"https://x.com/a\">link</a>.</p>");
        assertThat(html).contains("<h2>Título</h2>");
        assertThat(html).contains("<strong>forte</strong>");
        assertThat(html).contains("<a href=\"https://x.com/a\" rel=\"noopener noreferrer\">link</a>");
    }

    @Test
    void sanitizeArticleHtmlRemoveScriptEStyleComConteudo() {
        String html = ArticleBodyHtmlRenderer.sanitizeArticleHtml(
                "<p>a</p><script>alert(1)</script><style>*{}</style><p>b</p>");
        assertThat(html).isEqualTo("<p>a</p><p>b</p>");
    }

    @Test
    void sanitizeArticleHtmlRemoveTagsForaDaAllowlistPreservandoTexto() {
        assertThat(ArticleBodyHtmlRenderer.sanitizeArticleHtml("<div class=\"x\">Bloco</div>")).isEqualTo("Bloco");
    }

    @Test
    void sanitizeArticleHtmlDescartaHrefJavascriptEAtributosPerigosos() {
        String html = ArticleBodyHtmlRenderer.sanitizeArticleHtml(
                "<p onclick=\"alert(1)\">Texto</p><a href=\"javascript:alert(1)\">x</a>");
        assertThat(html).doesNotContain("onclick");
        assertThat(html).doesNotContain("javascript:");
        assertThat(html).contains("<p>Texto</p>");
    }

    @Test
    void sanitizeArticleHtmlRemoveImgSemHttpsEMantemImgHttpsComAltEscapado() {
        assertThat(ArticleBodyHtmlRenderer.sanitizeArticleHtml("<img src=\"http://x.com/a.jpg\" alt=\"a\">"))
                .isEmpty();
        String html = ArticleBodyHtmlRenderer.sanitizeArticleHtml(
                "<img src=\"https://images.unsplash.com/a.jpg\" alt=\"Foo & Bar\">");
        assertThat(html).contains("src=\"https://images.unsplash.com/a.jpg\"");
        assertThat(html).contains("alt=\"Foo &amp; Bar\"");
    }

    @Test
    void sanitizeArticleHtmlNuloRetornaVazio() {
        assertThat(ArticleBodyHtmlRenderer.sanitizeArticleHtml(null)).isEmpty();
    }

    @Test
    void sanitizeArticleHtmlAceitaAspaSimplesEValorSemAspas() {
        String html = ArticleBodyHtmlRenderer.sanitizeArticleHtml(
                "<a href='https://x.com/a'>link1</a><img src=https://images.unsplash.com/a.jpg>");
        assertThat(html).contains("<a href=\"https://x.com/a\" rel=\"noopener noreferrer\">link1</a>");
        assertThat(html).contains("src=\"https://images.unsplash.com/a.jpg\"");
        assertThat(html).contains("alt=\"\"");
    }

    @Test
    void sanitizeArticleHtmlRemoveAncoraSemHref() {
        // a tag de abertura sem href válido é descartada; a de fechamento órfã
        // é inofensiva (navegador ignora), mas permanece no texto.
        assertThat(ArticleBodyHtmlRenderer.sanitizeArticleHtml("<a>sem href</a>")).isEqualTo("sem href</a>");
    }

    @Test
    void sanitizeArticleHtmlAceitaLinkInternoRelativoEMailto() {
        String html = ArticleBodyHtmlRenderer.sanitizeArticleHtml(
                "<a href=\"/novidades/outro\">interno</a><a href=\"mailto:a@b.com\">mail</a>");
        assertThat(html).contains("<a href=\"/novidades/outro\" rel=\"noopener noreferrer\">interno</a>");
        assertThat(html).contains("<a href=\"mailto:a@b.com\" rel=\"noopener noreferrer\">mail</a>");
    }

    @Test
    void sanitizeArticleHtmlRejeitaUrlProtocolRelativaSemEsquemaEUriInvalida() {
        String html = ArticleBodyHtmlRenderer.sanitizeArticleHtml(
                "<a href=\"//evil.com/x\">a</a><a href=\"not-a-url\">b</a><a href=\"https://[invalid\">c</a>");
        assertThat(html).doesNotContain("evil.com");
        assertThat(html).isEqualTo("a</a>b</a>c</a>");
    }

    @Test
    void sanitizeArticleHtmlRejeitaImgSemSrcEComUriInvalida() {
        assertThat(ArticleBodyHtmlRenderer.sanitizeArticleHtml("<img alt=\"x\">")).isEmpty();
        assertThat(ArticleBodyHtmlRenderer.sanitizeArticleHtml("<img src=\"https://[invalid\">"))
                .isEmpty();
    }
}
