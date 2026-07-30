package br.com.trcon.site.news.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class CoverImageUrlsTest {

    @Test
    void blankViraNull() {
        assertThat(CoverImageUrls.normalize(null)).isNull();
        assertThat(CoverImageUrls.normalize("  ")).isNull();
    }

    @Test
    void aceitaHttps() {
        assertThat(CoverImageUrls.normalize(" https://images.unsplash.com/x "))
                .isEqualTo("https://images.unsplash.com/x");
    }

    @Test
    void rejeitaHttp() {
        assertThatThrownBy(() -> CoverImageUrls.normalize("http://example.com/a.jpg"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("HTTPS");
    }

    @Test
    void convertePaginaUnsplashParaDownload() {
        String page =
                "https://unsplash.com/pt-br/fotografias/uma-pessoa-segurando-um-telefone-celular-na-frente-de-um-grafico-de-acoes-K5mPtONmpHM";
        assertThat(CoverImageUrls.normalize(page))
                .isEqualTo("https://unsplash.com/photos/K5mPtONmpHM/download?force=true&w=1600");
    }
}
