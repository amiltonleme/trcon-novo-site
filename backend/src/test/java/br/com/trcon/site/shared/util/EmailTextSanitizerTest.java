package br.com.trcon.site.shared.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmailTextSanitizerTest {

    @Test
    void removeQuebrasDeLinhaDoHeader() {
        assertThat(EmailTextSanitizer.header("assunto\r\ninjetado")).isEqualTo("assunto  injetado");
    }

    @Test
    void escapaHtml() {
        assertThat(EmailTextSanitizer.html("<b>&x\"'")).isEqualTo("&lt;b&gt;&amp;x&quot;&#39;");
    }

    @Test
    void htmlVazioViraTraco() {
        assertThat(EmailTextSanitizer.html("  ")).isEqualTo("—");
        assertThat(EmailTextSanitizer.html(null)).isEqualTo("—");
    }
}
