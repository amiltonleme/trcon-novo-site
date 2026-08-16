package br.com.trcon.site.news.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import br.com.trcon.site.news.dto.response.NewsArticleResponse;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ArticlePageServiceImplTest {

    @Mock
    private NewsService newsService;

    @Test
    void deveDelegarRenderizacaoComBasePublica() {
        ArticlePageServiceImpl service =
                new ArticlePageServiceImpl(newsService, "https://trcongroup.com.br/");
        NewsArticleResponse article = new NewsArticleResponse(
                UUID.randomUUID(),
                "Sirius",
                "Tecnologia",
                "Titulo",
                "Resumo",
                "https://trcongroup.com.br/novidades/titulo",
                "titulo",
                "Corpo",
                "Titulo SEO",
                "Desc SEO",
                null,
                "trcon",
                Instant.parse("2026-08-16T12:00:00Z"));
        when(newsService.buscarPorSlug("titulo")).thenReturn(article);

        String html = service.renderHtml("titulo");

        assertThat(html).contains("Titulo SEO — TRCon Group");
        assertThat(html).contains("application/ld+json");
        assertThat(html).contains("<p>Corpo</p>");
    }

    @Test
    void notFoundHtmlUsaBaseConfigurada() {
        ArticlePageServiceImpl service =
                new ArticlePageServiceImpl(newsService, "https://trcongroup.com.br/");
        assertThat(service.notFoundHtml())
                .contains("noindex")
                .contains("https://trcongroup.com.br/");
    }
}
