package br.com.trcon.site.news.service;

import br.com.trcon.site.news.dto.response.NewsItemResponse;
import br.com.trcon.site.news.dto.response.NewsListResponse;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NewsFeedService {

    private static final DateTimeFormatter RFC822 =
            DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);

    private final NewsService newsService;
    private final String publicBaseUrl;

    public NewsFeedService(
            NewsService newsService, @Value("${trcon.site.public-base-url:https://trcongroup.com.br}") String publicBaseUrl) {
        this.newsService = newsService;
        this.publicBaseUrl = publicBaseUrl.replaceAll("/+$", "");
    }

    public String gerarRss() {
        NewsListResponse list = newsService.listarComSlug(50);
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<rss version=\"2.0\" xmlns:atom=\"http://www.w3.org/2005/Atom\">\n");
        xml.append("<channel>\n");
        xml.append("<title>TRCon Novidades</title>\n");
        xml.append("<link>").append(escapeXml(publicBaseUrl)).append("</link>\n");
        xml.append("<description>Noticias e artigos publicados pela TRCon Group.</description>\n");
        xml.append("<language>pt-BR</language>\n");
        xml.append("<atom:link href=\"")
                .append(escapeXml(publicBaseUrl + "/feed/news.xml"))
                .append("\" rel=\"self\" type=\"application/rss+xml\"/>\n");

        for (NewsItemResponse item : list.items()) {
            String link = articleUrl(item);
            xml.append("<item>\n");
            xml.append("<title>").append(escapeXml(item.title())).append("</title>\n");
            xml.append("<link>").append(escapeXml(link)).append("</link>\n");
            xml.append("<guid isPermaLink=\"true\">").append(escapeXml(link)).append("</guid>\n");
            if (item.publishedAt() != null) {
                xml.append("<pubDate>")
                        .append(formatRfc822(item.publishedAt()))
                        .append("</pubDate>\n");
            }
            xml.append("<description>")
                    .append(escapeXml(item.summary()))
                    .append("</description>\n");
            xml.append("</item>\n");
        }

        xml.append("</channel>\n</rss>");
        return xml.toString();
    }

    public String gerarSitemap() {
        NewsListResponse list = newsService.listarComSlug(200);
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");
        appendUrl(xml, publicBaseUrl + "/", null);
        for (NewsItemResponse item : list.items()) {
            appendUrl(xml, articleUrl(item), item.publishedAt());
        }
        xml.append("</urlset>");
        return xml.toString();
    }

    private void appendUrl(StringBuilder xml, String loc, Instant lastmod) {
        xml.append("<url><loc>").append(escapeXml(loc)).append("</loc>");
        if (lastmod != null) {
            xml.append("<lastmod>")
                    .append(lastmod.atZone(ZoneOffset.UTC).toLocalDate())
                    .append("</lastmod>");
        }
        xml.append("</url>\n");
    }

    private String articleUrl(NewsItemResponse item) {
        if (item.slug() != null && !item.slug().isBlank()) {
            return publicBaseUrl + "/novidades/" + item.slug();
        }
        return item.url();
    }

    private String formatRfc822(Instant instant) {
        return RFC822.format(instant.atZone(ZoneOffset.UTC));
    }

    private String escapeXml(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
