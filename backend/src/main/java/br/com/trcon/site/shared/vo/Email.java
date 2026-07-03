package br.com.trcon.site.shared.vo;

import java.util.regex.Pattern;

/**
 * Value Object de e-mail: valida e normaliza (trim + lowercase) em um único
 * lugar, para não repetir a regra em múltiplos services (ver doc/05-BACKEND-ARQUITETURA-MVC.md).
 */
public record Email(String value) {

    private static final Pattern SIMPLE_EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)*\\.[a-zA-Z]{2,}$");

    public Email {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("email não pode ser vazio");
        }
        value = value.trim().toLowerCase();
        if (!SIMPLE_EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("email inválido: " + value);
        }
    }

    public static Email of(String raw) {
        return new Email(raw);
    }

    @Override
    public String toString() {
        return value;
    }
}
