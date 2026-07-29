package br.com.trcon.site.news.service;

import br.com.trcon.site.news.dto.response.NewsItemResponse;
import br.com.trcon.site.news.dto.response.NewsListResponse;
import br.com.trcon.site.news.util.NewsFeedXmlSupport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NewsFeedService {

    private final NewsService newsService;
    private final String publicBaseUrl;

    public NewsFeedService(
            NewsService newsService,
            @Value("${trcon.site.public-base-url:https://trcongroup.com.br}") String publicBaseUrl) {
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
        xml.append("<link>").append(NewsFeedXmlSupport.escapeXml(publicBaseUrl)).append("</link>\n");
        xml.append("<description>Noticias e artigos publicados pela TRCon Group.</description>\n");
        xml.append("<language>pt-BR</language>\n");
        xml.append("<atom:link href=\"")
                .append(NewsFeedXmlSupport.escapeXml(publicBaseUrl + "/feed/news.xml"))
                .append("\" rel=\"self\" type=\"application/rss+xml\"/>\n");

        for (NewsItemResponse item : list.items()) {
            String link = NewsFeedXmlSupport.articleUrl(publicBaseUrl, item);
            xml.append("<item>\n");
            xml.append("<title>").append(NewsFeedXmlSupport.escapeXml(item.title())).append("</title>\n");
            xml.append("<link>").append(NewsFeedXmlSupport.escapeXml(link)).append("</link>\n");
            xml.append("<guid isPermaLink=\"true\">")
                    .append(NewsFeedXmlSupport.escapeXml(link))
                    .append("</guid>\n");
            if (item.publishedAt() != null) {
                xml.append("<pubDate>")
                        .append(NewsFeedXmlSupport.formatRfc822(item.publishedAt()))
                        .append("</pubDate>\n");
            }
            xml.append("<description>")
                    .append(NewsFeedXmlSupport.escapeXml(item.summary()))
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
        NewsFeedXmlSupport.appendUrl(xml, publicBaseUrl + "/", null);
        for (NewsItemResponse item : list.items()) {
            NewsFeedXmlSupport.appendUrl(
                    xml, NewsFeedXmlSupport.articleUrl(publicBaseUrl, item), item.publishedAt());
        }
        xml.append("</urlset>");
        return xml.toString();
    }
}
