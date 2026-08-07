package br.com.trcon.site.shared.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Prazo padrão de visibilidade de conteúdo editorial no site.
 * {@code 0} = permanente (sem expires_at). Valor de fábrica: 4 dias (configurável).
 */
@ConfigurationProperties(prefix = "trcon.site.content")
public record ContentTtlProperties(int ttlDays) {

    public ContentTtlProperties {
        if (ttlDays < 0) {
            throw new IllegalArgumentException("trcon.site.content.ttl-days deve ser >= 0");
        }
    }
}
