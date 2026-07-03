package br.com.trcon.site.shared.vo;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailTest {

    @Test
    void deveNormalizarParaMinusculoERemoverEspacos() {
        Email email = Email.of("  Fulano@Empresa.COM  ");

        assertThat(email.value()).isEqualTo("fulano@empresa.com");
    }

    @Test
    void deveAceitarEmailValido() {
        Email email = Email.of("contato@trcon.com.br");

        assertThat(email.value()).isEqualTo("contato@trcon.com.br");
    }

    @Test
    void deveRejeitarEmailVazio() {
        assertThatThrownBy(() -> Email.of(" "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("vazio");
    }

    @Test
    void deveRejeitarEmailNulo() {
        assertThatThrownBy(() -> Email.of(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deveRejeitarEmailSemArroba() {
        assertThatThrownBy(() -> Email.of("fulano-empresa.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inválido");
    }

    @Test
    void deveRejeitarEmailSemDominio() {
        assertThatThrownBy(() -> Email.of("fulano@"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
