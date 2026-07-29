package br.com.trcon.site.shared.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "trcon.site.mail")
public record MailProperties(
        boolean enabled,
        String apiKey,
        String from,
        String notifyTo) {

    public boolean isConfigured() {
        return enabled
                && apiKey != null
                && !apiKey.isBlank()
                && from != null
                && !from.isBlank()
                && notifyTo != null
                && !notifyTo.isBlank();
    }
}
