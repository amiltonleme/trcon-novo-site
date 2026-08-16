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
}
