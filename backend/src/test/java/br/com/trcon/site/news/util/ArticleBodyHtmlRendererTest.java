package br.com.trcon.site.news.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

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
    }

    @Test
    void deveConverterYoutubeEmIframe() {
        String html = ArticleBodyHtmlRenderer.render(
                "Intro\n\nhttps://www.youtube.com/watch?v=dQw4w9WgXcQ\n\nFim");
        assertThat(html).contains("youtube.com/embed/dQw4w9WgXcQ");
        assertThat(html).contains("iframe");
    }

    @Test
    void corpoVazioUsaPlaceholder() {
        assertThat(ArticleBodyHtmlRenderer.render(" ")).contains("Conteúdo indisponível");
    }
}
