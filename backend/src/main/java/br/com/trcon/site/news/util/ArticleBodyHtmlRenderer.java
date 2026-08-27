package br.com.trcon.site.news.util;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Converte o corpo editorial (markdown-lite ou HTML já pronto) em HTML seguro para a página pública. */
public final class ArticleBodyHtmlRenderer {

    private static final Pattern BOLD = Pattern.compile("\\*\\*([^*\\n][\\s\\S]*?[^*\\n])\\*\\*");
    /** Marcador de lista; o conteúdo após o marcador pode ser vazio (item ignorado). */
    private static final Pattern LIST_ITEM = Pattern.compile("^[-*](?:\\s+(.*))?$");

    // Tags que abrem um bloco no início do texto — sinal de que o corpo já
    // vem como HTML pronto (ex.: pipeline do Sirius Marketing), e não como o
    // markdown-lite que o restante desta classe espera.
    private static final Pattern HTML_BODY_START =
            Pattern.compile("^<(h[1-6]|p|ul|ol|blockquote|div|figure|table|section|article)\\b", Pattern.CASE_INSENSITIVE);

    // Tags/atributos que nunca devem sobreviver à sanitização, mesmo com seu
    // conteúdo — removidos por completo (tag + conteúdo).
    private static final List<String> STRIP_WITH_CONTENT_TAGS = List.of(
            "script", "style", "iframe", "object", "embed", "noscript", "form", "textarea", "select", "svg", "math");
    private static final Pattern STRAY_DANGEROUS_TAGS = Pattern.compile(
            "(?i)</?(?:" + String.join("|", STRIP_WITH_CONTENT_TAGS) + ")\\b[^>]*>");
    // Tags "vazias" (sem conteúdo de risco) removidas isoladamente, sem afetar o texto ao redor.
    private static final Pattern STRIP_STANDALONE_TAGS =
            Pattern.compile("(?i)</?(?:input|button|link|meta|base)\\b[^>]*>");

    // Allowlist do corpo do artigo: só estes elementos sobrevivem à sanitização.
    private static final Set<String> ALLOWED_TAGS = Set.of(
            "p", "h2", "h3", "h4", "h5", "h6", "ul", "ol", "li", "strong", "em", "b", "i", "u",
            "br", "hr", "blockquote", "a", "img", "span", "figure", "figcaption", "pre", "code");
    private static final Set<String> VOID_TAGS = Set.of("br", "hr", "img");
    private static final Pattern TAG_RE = Pattern.compile("<(/?)([a-zA-Z][a-zA-Z0-9]*)((?:\\s+[^<>]*)?)/?>");

    private ArticleBodyHtmlRenderer() {}

    public static String render(String body) {
        if (body == null || body.isBlank()) {
            return "<p class=\"article-empty\">Conteúdo indisponível.</p>";
        }
        String trimmed = body.trim();

        // Alguns artigos (ex.: pipeline do Sirius Marketing) chegam com o
        // corpo já em HTML pronto em vez do markdown-lite abaixo. Nesse caso,
        // sanitiza e renderiza as tags em vez de escapá-las como texto.
        if (isHtmlArticleBody(trimmed)) {
            return sanitizeArticleHtml(trimmed);
        }

        StringBuilder html = new StringBuilder();
        for (String block : trimmed.split("\\n\\s*\\n")) {
            if (isListBlock(block)) {
                html.append(renderList(block));
            } else {
                html.append(renderParagraph(block));
            }
        }
        return html.toString();
    }

    /** true quando {@code value} parece um corpo de artigo já em HTML (não markdown-lite). */
    public static boolean isHtmlArticleBody(String value) {
        return value != null && HTML_BODY_START.matcher(value.trim()).find();
    }

    /**
     * Sanitiza HTML de corpo de artigo com uma allowlist de tags/atributos —
     * usado quando isHtmlArticleBody() indica que o conteúdo já vem
     * formatado. Espelha sanitizeArticleHtml() do frontend
     * (assets/modules/sanitize.js) para manter o mesmo comportamento entre a
     * página renderizada no servidor (SSR) e a hidratação no cliente.
     */
    public static String sanitizeArticleHtml(String html) {
        String out = html == null ? "" : html;

        for (String tag : STRIP_WITH_CONTENT_TAGS) {
            Pattern withContent = Pattern.compile("(?is)<" + tag + "\\b[^>]*>.*?</" + tag + "\\s*>");
            out = withContent.matcher(out).replaceAll("");
        }
        out = STRAY_DANGEROUS_TAGS.matcher(out).replaceAll("");
        out = STRIP_STANDALONE_TAGS.matcher(out).replaceAll("");

        Matcher matcher = TAG_RE.matcher(out);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String replacement = sanitizedTagReplacement(matcher.group(1), matcher.group(2), matcher.group(3));
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private static String sanitizedTagReplacement(String closing, String tagNameRaw, String attrs) {
        String tagName = tagNameRaw.toLowerCase(Locale.ROOT);
        if (!ALLOWED_TAGS.contains(tagName)) {
            return "";
        }
        if (!closing.isEmpty()) {
            return VOID_TAGS.contains(tagName) ? "" : "</" + tagName + ">";
        }

        String attrHtml = "";
        if ("a".equals(tagName)) {
            String href = safeHref(extractAttr(attrs, "href"));
            if (href == null) {
                return "";
            }
            attrHtml = " href=\"" + escapeHtml(href) + "\" rel=\"noopener noreferrer\"";
        } else if ("img".equals(tagName)) {
            String src = safeImgSrc(extractAttr(attrs, "src"));
            if (src == null) {
                return "";
            }
            String alt = escapeHtml(extractAttr(attrs, "alt"));
            attrHtml = " src=\"" + escapeHtml(src) + "\" alt=\"" + alt + "\" loading=\"lazy\"";
        }
        return "<" + tagName + attrHtml + (VOID_TAGS.contains(tagName) ? " /" : "") + ">";
    }

    private static String extractAttr(String attrString, String name) {
        if (attrString == null) {
            return "";
        }
        Pattern pattern = Pattern.compile(
                name + "\\s*=\\s*(\"([^\"]*)\"|'([^']*)'|([^\\s>]+))", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(attrString);
        if (!matcher.find()) {
            return "";
        }
        if (matcher.group(2) != null) {
            return matcher.group(2);
        }
        if (matcher.group(3) != null) {
            return matcher.group(3);
        }
        return matcher.group(4) != null ? matcher.group(4) : "";
    }

    /** Aceita http(s)/mailto absolutos ou caminhos internos começando com "/". */
    private static String safeHref(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.startsWith("/") && !trimmed.startsWith("//")) {
            return trimmed;
        }
        try {
            URI uri = URI.create(trimmed);
            String scheme = uri.getScheme();
            if (scheme == null) {
                return null;
            }
            String schemeLower = scheme.toLowerCase(Locale.ROOT);
            if (!"http".equals(schemeLower) && !"https".equals(schemeLower) && !"mailto".equals(schemeLower)) {
                return null;
            }
            return trimmed;
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    /** Só aceita imagens absolutas em HTTPS. */
    private static String safeImgSrc(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String trimmed = value.trim();
        if (!trimmed.startsWith("https://")) {
            return null;
        }
        try {
            URI.create(trimmed);
        } catch (IllegalArgumentException ex) {
            return null;
        }
        return trimmed;
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
                String raw = matcher.group(1);
                String item = raw == null ? "" : raw.trim();
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
