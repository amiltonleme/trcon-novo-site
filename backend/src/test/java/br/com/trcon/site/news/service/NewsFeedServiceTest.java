package br.com.trcon.site.news.service;

import br.com.trcon.site.news.dto.response.NewsItemResponse;
import br.com.trcon.site.news.dto.response.NewsListResponse;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NewsFeedServiceTest {

    @Mock
    private NewsService newsService;

    @Test
    void gerarRssIncluiItemComSlugEPubDate() {
        NewsFeedService service = new NewsFeedService(newsService, "https://trcongroup.com.br/");
        NewsItemResponse item = new NewsItemResponse(
                UUID.randomUUID(),
                "Sirius Marketing",
                "IA",
                "Titulo <x>",
                "Resumo & co",
                "https://ignored.example/a",
                "meu-slug",
                null,
                Instant.parse("2026-07-23T18:00:00Z"));
        when(newsService.listarComSlug(50)).thenReturn(NewsListResponse.of(List.of(item)));

        String rss = service.gerarRss();

        assertThat(rss).contains("<title>TRCon Novidades</title>");
        assertThat(rss).contains("<link>https://trcongroup.com.br</link>");
        assertThat(rss).contains("/novidades/meu-slug");
        assertThat(rss).contains("Titulo &lt;x&gt;");
        assertThat(rss).contains("Resumo &amp; co");
        assertThat(rss).contains("<pubDate>");
    }

    @Test
    void gerarRssUsaUrlQuandoSlugAusenteEOmitePubDateNulo() {
        NewsFeedService service = new NewsFeedService(newsService, "https://trcongroup.com.br");
        NewsItemResponse item = new NewsItemResponse(
                UUID.randomUUID(),
                "src",
                "IA",
                "Titulo",
                "Resumo",
                "https://example.com/ext",
                "  ",
                null,
                null);
        when(newsService.listarComSlug(50)).thenReturn(NewsListResponse.of(List.of(item)));

        String rss = service.gerarRss();

        assertThat(rss).contains("https://example.com/ext");
        assertThat(rss).doesNotContain("<pubDate>");
    }

    @Test
    void gerarSitemapIncluiHomeEArtigos() {
        NewsFeedService service = new NewsFeedService(newsService, "https://trcongroup.com.br");
        NewsItemResponse comSlug = new NewsItemResponse(
                UUID.randomUUID(), "s", "IA", "A", "r", "https://x", "slug-a", null, Instant.parse("2026-01-02T00:00:00Z"));
        NewsItemResponse semSlug =
                new NewsItemResponse(UUID.randomUUID(), "s", "IA", "B", "r", "https://y.com/b", null, null, null);
        when(newsService.listarComSlug(200)).thenReturn(NewsListResponse.of(List.of(comSlug, semSlug)));

        String sitemap = service.gerarSitemap();

        assertThat(sitemap).contains("<loc>https://trcongroup.com.br/</loc>");
        assertThat(sitemap).contains("<loc>https://trcongroup.com.br/novidades/slug-a</loc>");
        assertThat(sitemap).contains("<lastmod>2026-01-02</lastmod>");
        assertThat(sitemap).contains("<loc>https://y.com/b</loc>");
    }
}
