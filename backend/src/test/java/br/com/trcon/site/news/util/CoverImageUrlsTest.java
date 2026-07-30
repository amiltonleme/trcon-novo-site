package br.com.trcon.site.news.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class CoverImageUrlsTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t"})
    void blankViraNull(String value) {
        assertThat(CoverImageUrls.normalize(value)).isNull();
    }

    @Test
    void aceitaHttpsDiretoDeCdn() {
        assertThat(CoverImageUrls.normalize(" https://images.unsplash.com/photo-x "))
                .isEqualTo("https://images.unsplash.com/photo-x");
        assertThat(CoverImageUrls.normalize("https://plus.unsplash.com/premium-photo-1"))
                .isEqualTo("https://plus.unsplash.com/premium-photo-1");
        assertThat(CoverImageUrls.normalize("https://images.pexels.com/photos/1.jpeg"))
                .isEqualTo("https://images.pexels.com/photos/1.jpeg");
        assertThat(CoverImageUrls.normalize("https://cdn.pixabay.com/photo/1.jpg"))
                .isEqualTo("https://cdn.pixabay.com/photo/1.jpg");
    }

    @Test
    void aceitaUrlComExtensaoDeImagem() {
        assertThat(CoverImageUrls.normalize("https://cdn.example.com/capa.webp?w=800"))
                .isEqualTo("https://cdn.example.com/capa.webp?w=800");
        assertThat(CoverImageUrls.normalize("https://cdn.example.com/a.JPEG"))
                .isEqualTo("https://cdn.example.com/a.JPEG");
    }

    @Test
    void aceitaUrlHttpsGenericaSemParecerGaleria() {
        assertThat(CoverImageUrls.normalize("https://example.com/static/banner"))
                .isEqualTo("https://example.com/static/banner");
    }

    @Test
    void rejeitaHttp() {
        assertThatThrownBy(() -> CoverImageUrls.normalize("http://example.com/a.jpg"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("HTTPS");
    }

    @Test
    void rejeitaMaisDe500Caracteres() {
        String tooLong = "https://example.com/" + "a".repeat(490);
        assertThatThrownBy(() -> CoverImageUrls.normalize(tooLong))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("500");
    }

    @Test
    void convertePaginaUnsplashPtBrFotografias() {
        String page =
                "https://unsplash.com/pt-br/fotografias/uma-pessoa-segurando-um-telefone-celular-na-frente-de-um-grafico-de-acoes-K5mPtONmpHM";
        assertThat(CoverImageUrls.normalize(page))
                .isEqualTo("https://unsplash.com/photos/K5mPtONmpHM/download?force=true&w=1600");
    }

    @Test
    void convertePaginaUnsplashPhotosComSlashFinal() {
        String page = "https://unsplash.com/photos/abstract-gradient-AbCdEfGhIjK/";
        assertThat(CoverImageUrls.normalize(page))
                .isEqualTo("https://unsplash.com/photos/AbCdEfGhIjK/download?force=true&w=1600");
    }

    @Test
    void convertePaginaUnsplashQuandoSlugEhSoOId() {
        assertThat(CoverImageUrls.normalize("https://unsplash.com/photos/AbCdEfGhIjK"))
                .isEqualTo("https://unsplash.com/photos/AbCdEfGhIjK/download?force=true&w=1600");
    }

    @Test
    void naoConverteQuandoJaEhDownload() {
        String download = "https://unsplash.com/photos/AbCdEfGhIjK/download?force=true&w=1600";
        assertThat(CoverImageUrls.normalize(download)).isEqualTo(download);
    }

    @Test
    void rejeitaPaginaPexelsSemImagemDireta() {
        assertThatThrownBy(() -> CoverImageUrls.normalize("https://www.pexels.com/photo/something-123/"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("endereço direto");
    }

    @Test
    void rejeitaPaginaPixabaySemImagemDireta() {
        assertThatThrownBy(() -> CoverImageUrls.normalize("https://pixabay.com/photos/finance-123456/"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("endereço direto");
    }

    @Test
    void rejeitaPaginaUnsplashComIdInvalido() {
        assertThatThrownBy(() -> CoverImageUrls.normalize("https://unsplash.com/photos/curto"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("endereço direto");
    }

    @Test
    void convertUnsplashIgnoraCdnEDownload() {
        assertThat(CoverImageUrls.convertUnsplashPageToImage("https://images.unsplash.com/x")).isNull();
        assertThat(CoverImageUrls.convertUnsplashPageToImage("https://plus.unsplash.com/x")).isNull();
        assertThat(CoverImageUrls.convertUnsplashPageToImage(
                        "https://unsplash.com/photos/AbCdEfGhIjK/download?force=true"))
                .isNull();
        assertThat(CoverImageUrls.convertUnsplashPageToImage("https://example.com/other")).isNull();
    }
}
