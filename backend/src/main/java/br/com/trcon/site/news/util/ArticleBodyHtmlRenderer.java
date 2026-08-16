package br.com.trcon.site.news.util;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Converte o corpo editorial (markdown-lite) em HTML escapado para a página pública. */
public final class ArticleBodyHtmlRenderer {

    private static final Pattern BOLD = Pattern.compile("\\*\\*([^*\\n][\\s\\S]*?[^*\\n])\\*\\*");
    private static final Pattern LIST_ITEM = Pattern.compile("^[-*]\\s+(.+)$");

    private ArticleBodyHtmlRenderer() {}

    public static String render(String body) {
        if (body == null || body.isBlank()) {
            return "<p class=\"article-empty\">Conteúdo indisponível.</p>";
        }
        StringBuilder html = new StringBuilder();
        for (String block : body.trim().split("\\n\\s*\\n")) {
            if (isListBlock(block)) {
                html.append(renderList(block));
            } else {
                html.append(renderParagraph(block));
            }
        }
        return html.toString();
    }

    public static String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    public static String videoEmbedSrc(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            URI uri = URI.create(value.trim());
            String scheme = uri.getScheme();
            if (scheme == null) {
                return null;
            }
            String schemeLower = scheme.toLowerCase();
            if (!"https".equals(schemeLower) && !"http".equals(schemeLower)) {
                return null;
            }
            String host = uri.getHost();
            if (host == null) {
                return null;
            }
            host = host.replaceFirst("^www\\.", "").toLowerCase();
            String path = uri.getPath() == null ? "" : uri.getPath();

            if ("youtu.be".equals(host)) {
                String id = firstPathSegment(path);
                return sanitizeVideoId(id) == null
                        ? null
                        : "https://www.youtube.com/embed/" + sanitizeVideoId(id);
            }
            if ("youtube.com".equals(host)
                    || "m.youtube.com".equals(host)
                    || "youtube-nocookie.com".equals(host)) {
                if (path.startsWith("/embed/") || path.startsWith("/shorts/")) {
                    String id = sanitizeVideoId(pathSegment(path, 2));
                    return id == null ? null : "https://www.youtube.com/embed/" + id;
                }
                String id = sanitizeVideoId(queryParam(uri.getQuery(), "v"));
                return id == null ? null : "https://www.youtube.com/embed/" + id;
            }
            if ("vimeo.com".equals(host)) {
                String id = firstPathSegment(path);
                return id != null && id.matches("\\d+") ? "https://player.vimeo.com/video/" + id : null;
            }
            if ("player.vimeo.com".equals(host) && path.startsWith("/video/")) {
                String id = pathSegment(path, 2);
                return id != null && id.matches("\\d+") ? "https://player.vimeo.com/video/" + id : null;
            }
            return null;
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private static String renderParagraph(String block) {
        String trimmed = block == null ? "" : block.trim();
        if (trimmed.isEmpty()) {
            return "";
        }
        if (!trimmed.contains("\n")) {
            String embed = videoEmbedSrc(trimmed);
            if (embed != null) {
                return videoHtml(embed);
            }
        }

        List<String> parts = new ArrayList<>();
        for (String rawLine : trimmed.split("\\n")) {
            String line = rawLine.trim();
            if (line.isEmpty()) {
                continue;
            }
            String embed = videoEmbedSrc(line);
            if (embed != null) {
                parts.add("</p>" + videoHtml(embed) + "<p>");
            } else {
                parts.add(renderInlineMarkup(line));
            }
        }
        return "<p>" + String.join("<br />", parts) + "</p>";
    }

    private static String renderList(String block) {
        List<String> items = new ArrayList<>();
        for (String line : block.split("\\n")) {
            Matcher matcher = LIST_ITEM.matcher(line.trim());
            if (matcher.matches()) {
                String item = matcher.group(1).trim();
                if (!item.isEmpty()) {
                    items.add(item);
                }
            }
        }
        if (items.isEmpty()) {
            return "";
        }
        StringBuilder html = new StringBuilder("<ul>");
        for (String item : items) {
            html.append("<li>").append(renderInlineMarkup(item)).append("</li>");
        }
        return html.append("</ul>").toString();
    }

    private static boolean isListBlock(String block) {
        boolean any = false;
        for (String line : block.split("\\n")) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            any = true;
            if (!LIST_ITEM.matcher(trimmed).matches()) {
                return false;
            }
        }
        return any;
    }

    private static String renderInlineMarkup(String value) {
        String escaped = escapeHtml(value);
        Matcher matcher = BOLD.matcher(escaped);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(
                    sb, Matcher.quoteReplacement("<strong>" + matcher.group(1) + "</strong>"));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private static String videoHtml(String embedSrc) {
        return "<div class=\"article-video\"><iframe src=\""
                + escapeHtml(embedSrc)
                + "\" title=\"Vídeo\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media;"
                + " gyroscope; picture-in-picture\" allowfullscreen loading=\"lazy\""
                + " referrerpolicy=\"strict-origin-when-cross-origin\"></iframe></div>";
    }

    private static String firstPathSegment(String path) {
        if (path == null || path.isBlank()) {
            return null;
        }
        for (String part : path.split("/")) {
            if (!part.isBlank()) {
                return part;
            }
        }
        return null;
    }

    private static String pathSegment(String path, int index1Based) {
        String[] parts = path.split("/");
        return parts.length > index1Based && !parts[index1Based].isBlank() ? parts[index1Based] : null;
    }

    private static String queryParam(String query, String name) {
        if (query == null || query.isBlank()) {
            return null;
        }
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2 && name.equals(kv[0])) {
                return kv[1];
            }
        }
        return null;
    }

    private static String sanitizeVideoId(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        String cleaned = id.replaceAll("[^A-Za-z0-9_-]", "");
        return cleaned.isEmpty() ? null : cleaned;
    }
}
