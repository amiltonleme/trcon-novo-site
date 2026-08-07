package br.com.trcon.site.shared.expiry;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Component;

/**
 * Resolve {@code expires_at} a partir de expiresAt explícito, ttlDays da peça ou default do site.
 * {@code ttlDays == 0} → permanente (null).
 */
@Component
public class ExpiryInstantCalculator {

    public Instant resolve(Instant publishedAt, Integer ttlDays, Instant expiresAt, int defaultTtlDays) {
        if (publishedAt == null) {
            throw new IllegalArgumentException("publishedAt é obrigatório");
        }
        if (expiresAt != null) {
            return expiresAt;
        }
        if (ttlDays != null) {
            return fromTtlDays(publishedAt, ttlDays);
        }
        return fromTtlDays(publishedAt, defaultTtlDays);
    }

    public Instant fromTtlDays(Instant publishedAt, int ttlDays) {
        if (ttlDays < 0) {
            throw new IllegalArgumentException("ttlDays deve ser >= 0");
        }
        if (ttlDays == 0) {
            return null;
        }
        return publishedAt.plus(ttlDays, ChronoUnit.DAYS);
    }
}
