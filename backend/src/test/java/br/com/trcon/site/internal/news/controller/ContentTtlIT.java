package br.com.trcon.site.internal.news.controller;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.trcon.site.TestcontainersConfiguration;
import br.com.trcon.site.internal.news.dto.InternalNewsCreateRequest;
import br.com.trcon.site.internal.news.dto.InternalNewsCreateResponse;
import br.com.trcon.site.news.dto.response.NewsArticleResponse;
import br.com.trcon.site.news.dto.response.NewsListResponse;
import br.com.trcon.site.news.repository.NewsRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class ContentTtlIT {

    private static final String API_KEY = "test-internal-key";

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("trcon.site.internal-api-key", () -> API_KEY);
        registry.add("trcon.site.content.ttl-days", () -> "4");
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private NewsRepository newsRepository;

    @Test
    void artigoExpiradoSomeDaListaESlugRetorna404() {
        newsRepository.deleteAll();
        Instant published = Instant.now().minus(10, ChronoUnit.DAYS);

        InternalNewsCreateRequest request = new InternalNewsCreateRequest(
                "Artigo antigo",
                "Resumo",
                "https://trcongroup.com.br/novidades/artigo-antigo",
                "Tecnologia",
                "trcon",
                published,
                "ttl-expired-1",
                null,
                "artigo-antigo",
                "Corpo",
                null,
                null,
                null,
                2,
                null);

        restTemplate.postForEntity("/api/internal/news", entity(request), InternalNewsCreateResponse.class);

        ResponseEntity<NewsListResponse> list =
                restTemplate.getForEntity("/api/public/news?category=Tecnologia", NewsListResponse.class);
        assertThat(list.getBody().items()).isEmpty();

        ResponseEntity<NewsArticleResponse> article =
                restTemplate.getForEntity("/api/public/news/artigo-antigo", NewsArticleResponse.class);
        assertThat(article.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void ttlZeroPermaneceVisivel() {
        newsRepository.deleteAll();

        InternalNewsCreateRequest request = new InternalNewsCreateRequest(
                "Evergreen",
                "Resumo",
                "https://trcongroup.com.br/novidades/evergreen",
                "Mercado",
                "trcon",
                Instant.now().minus(30, ChronoUnit.DAYS),
                "ttl-ever-1",
                null,
                "evergreen",
                "Corpo",
                null,
                null,
                null,
                0,
                null);

        restTemplate.postForEntity("/api/internal/news", entity(request), InternalNewsCreateResponse.class);

        ResponseEntity<NewsListResponse> list =
                restTemplate.getForEntity("/api/public/news?category=Mercado", NewsListResponse.class);
        assertThat(list.getBody().items()).hasSize(1);

        ResponseEntity<NewsArticleResponse> article =
                restTemplate.getForEntity("/api/public/news/evergreen", NewsArticleResponse.class);
        assertThat(article.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private HttpEntity<InternalNewsCreateRequest> entity(InternalNewsCreateRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-Key", API_KEY);
        return new HttpEntity<>(request, headers);
    }
}
