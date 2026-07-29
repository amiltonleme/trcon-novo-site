package br.com.trcon.site.shared.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MailPropertiesTest {

    @Test
    void configuradoSomenteComTodosOsCamposEEnabled() {
        assertThat(new MailProperties(true, "key", "from@x.com", "to@x.com").isConfigured()).isTrue();
        assertThat(new MailProperties(false, "key", "from@x.com", "to@x.com").isConfigured()).isFalse();
        assertThat(new MailProperties(true, "", "from@x.com", "to@x.com").isConfigured()).isFalse();
        assertThat(new MailProperties(true, "key", " ", "to@x.com").isConfigured()).isFalse();
        assertThat(new MailProperties(true, "key", "from@x.com", null).isConfigured()).isFalse();
    }
}
