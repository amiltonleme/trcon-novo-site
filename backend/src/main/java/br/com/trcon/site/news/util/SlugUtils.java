package br.com.trcon.site.news.util;

import java.text.Normalizer;
import java.util.Locale;

public final class SlugUtils {

    private SlugUtils() {}

    public static String slugify(String value) {
        if (value == null || value.isBlank()) {
            return "artigo";
        }
        String normalized = Normalizer.normalize(value.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
        normalized = normalized.replaceAll("[^a-z0-9]+", "-").replaceAll("^-+|-+$", "");
        if (normalized.isBlank()) {
            return "artigo";
        }
        if (normalized.length() > 100) {
            normalized = normalized.substring(0, 100).replaceAll("-+$", "");
        }
        return normalized;
    }
}
