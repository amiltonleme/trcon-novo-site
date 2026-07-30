package br.com.trcon.site.news.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CoverImageUrls {

    private static final Pattern UNSPLASH_PHOTO_PATH = Pattern.compile(
            "(?i)unsplash\\.com/(?:[a-z]{2}(?:-[a-z]+)?/)?(?:photos|fotografias)/([^?#]+)");

    private CoverImageUrls() {}

    /**
     * Blank → null; exige https:// e no máximo 500 caracteres.
     * Converte página do Unsplash (fotografias/photos) em URL de download direta usável em {@code <img>}.
     */
    public static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.length() > 500) {
            throw new IllegalArgumentException("coverImageUrl deve ter no máximo 500 caracteres");
        }
        if (!trimmed.startsWith("https://")) {
            throw new IllegalArgumentException("coverImageUrl deve usar HTTPS");
        }
        String converted = convertUnsplashPageToImage(trimmed);
        if (converted != null) {
            if (converted.length() > 500) {
                throw new IllegalArgumentException("coverImageUrl deve ter no máximo 500 caracteres");
            }
            return converted;
        }
        if (looksLikeHtmlGalleryPage(trimmed)) {
            throw new IllegalArgumentException(
                    "Use o endereço direto da imagem (botão direito → Copiar endereço da imagem). "
                            + "Ex.: https://images.unsplash.com/... — não a página da foto.");
        }
        return trimmed;
    }

    static String convertUnsplashPageToImage(String url) {
        if (url.contains("images.unsplash.com") || url.contains("plus.unsplash.com")) {
            return null;
        }
        if (url.contains("/download")) {
            return null;
        }
        Matcher matcher = UNSPLASH_PHOTO_PATH.matcher(url);
        if (!matcher.find()) {
            return null;
        }
        String slug = matcher.group(1).replaceAll("/$", "");
        String photoId = slug.contains("-") ? slug.substring(slug.lastIndexOf('-') + 1) : slug;
        if (!photoId.matches("[A-Za-z0-9_-]{7,15}")) {
            return null;
        }
        return "https://unsplash.com/photos/" + photoId + "/download?force=true&w=1600";
    }

    private static boolean looksLikeHtmlGalleryPage(String url) {
        String lower = url.toLowerCase();
        if (lower.contains("images.unsplash.com")
                || lower.contains("plus.unsplash.com")
                || lower.contains("images.pexels.com")
                || lower.contains("cdn.pixabay.com")
                || lower.contains("/download")) {
            return false;
        }
        if (lower.matches(".*\\.(jpg|jpeg|png|webp|gif)(\\?.*)?$")) {
            return false;
        }
        return lower.contains("unsplash.com/")
                || lower.contains("pexels.com/photo")
                || lower.contains("pixabay.com/");
    }
}
