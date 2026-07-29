package br.com.trcon.site.shared.util;

/**
 * Sanitiza textos de usuário para e-mail (HTML e headers).
 */
public final class EmailTextSanitizer {

    private EmailTextSanitizer() {}

    /** Remove CR/LF para evitar injeção de cabeçalho em subject/reply-to. */
    public static String header(String raw) {
        if (raw == null) {
            return "";
        }
        return raw.replace('\r', ' ').replace('\n', ' ').trim();
    }

    /** Escapa HTML para corpo do e-mail. */
    public static String html(String raw) {
        if (raw == null || raw.isBlank()) {
            return "—";
        }
        return raw.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
