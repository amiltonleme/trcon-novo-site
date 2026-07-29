package br.com.trcon.site.news.util;

import br.com.trcon.site.news.dto.response.NewsItemResponse;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Formatação XML/RSS do feed de novidades (fora do service). */
public final class NewsFeedXmlSupport {

    private static final DateTimeFormatter RFC822 =
            DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);

    private NewsFeedXmlSupport() {}

    public static String escapeXml(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    public static String articleUrl(String publicBaseUrl, NewsItemResponse item) {
        if (item.slug() != null && !item.slug().isBlank()) {
            return publicBaseUrl + "/novidades/" + item.slug();
        }
        return item.url();
    }

    public static String formatRfc822(Instant instant) {
        return RFC822.format(instant.atZone(ZoneOffset.UTC));
    }

    public static void appendUrl(StringBuilder xml, String loc, Instant lastmod) {
        xml.append("<url><loc>").append(escapeXml(loc)).append("</loc>");
        if (lastmod != null) {
            xml.append("<lastmod>")
                    .append(lastmod.atZone(ZoneOffset.UTC).toLocalDate())
                    .append("</lastmod>");
        }
        xml.append("</url>\n");
    }
}
