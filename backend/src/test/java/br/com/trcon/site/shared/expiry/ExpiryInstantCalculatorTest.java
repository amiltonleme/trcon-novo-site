package br.com.trcon.site.shared.expiry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;

class ExpiryInstantCalculatorTest {

    private final ExpiryInstantCalculator calculator = new ExpiryInstantCalculator();
    private final Instant published = Instant.parse("2026-08-01T12:00:00Z");

    @Test
    void ttlDaysDoisTresSeis() {
        assertThat(calculator.fromTtlDays(published, 2))
                .isEqualTo(published.plus(2, ChronoUnit.DAYS));
        assertThat(calculator.fromTtlDays(published, 4))
                .isEqualTo(published.plus(4, ChronoUnit.DAYS));
        assertThat(calculator.fromTtlDays(published, 6))
                .isEqualTo(published.plus(6, ChronoUnit.DAYS));
    }

    @Test
    void ttlZeroEPermanente() {
        assertThat(calculator.fromTtlDays(published, 0)).isNull();
        assertThat(calculator.resolve(published, 0, null, 4)).isNull();
    }

    @Test
    void expiresAtExplicitoPrevalece() {
        Instant explicit = Instant.parse("2026-09-01T00:00:00Z");
        assertThat(calculator.resolve(published, 2, explicit, 4)).isEqualTo(explicit);
    }

    @Test
    void usaDefaultQuandoTtlOmitido() {
        assertThat(calculator.resolve(published, null, null, 4))
                .isEqualTo(published.plus(4, ChronoUnit.DAYS));
    }

    @Test
    void rejeitaTtlNegativo() {
        assertThatThrownBy(() -> calculator.fromTtlDays(published, -1))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
