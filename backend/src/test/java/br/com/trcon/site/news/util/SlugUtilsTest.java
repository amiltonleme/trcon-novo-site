package br.com.trcon.site.news.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SlugUtilsTest {

    @Test
    void slugifyPadrao() {
        assertThat(SlugUtils.slugify("Olá Mundo!")).isEqualTo("ola-mundo");
    }

    @Test
    void slugifyVazioOuNuloViraArtigo() {
        assertThat(SlugUtils.slugify(null)).isEqualTo("artigo");
        assertThat(SlugUtils.slugify("   ")).isEqualTo("artigo");
        assertThat(SlugUtils.slugify("@@@")).isEqualTo("artigo");
    }

    @Test
    void slugifyTruncaEm100() {
        String longTitle = "a".repeat(150);
        String slug = SlugUtils.slugify(longTitle);
        assertThat(slug.length()).isLessThanOrEqualTo(100);
        assertThat(slug).doesNotEndWith("-");
    }
}
