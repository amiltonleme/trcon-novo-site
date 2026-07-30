package br.com.trcon.site.internal.news.controller;

import br.com.trcon.site.TestcontainersConfiguration;
import br.com.trcon.site.internal.news.dto.InternalNewsCreateRequest;
import br.com.trcon.site.internal.news.dto.InternalNewsCreateResponse;
import br.com.trcon.site.news.dto.response.NewsArticleResponse;
import br.com.trcon.site.news.dto.response.NewsListResponse;
import br.com.trcon.site.news.repository.NewsRepository;
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

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class InternalNewsControllerIT {

    private static final String API_KEY = "test-internal-key";

    @DynamicPropertySource
    static void apiKey(DynamicPropertyRegistry registry) {
        registry.add("trcon.site.internal-api-key", () -> API_KEY);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private NewsRepository newsRepository;

    @Test
    void deveCriarArtigoComSlugBodyEExporPorSlug() {
        newsRepository.deleteAll();

        InternalNewsCreateRequest request = new InternalNewsCreateRequest(
                "Lancamento Sirius Marketing",
                "Plataforma editorial integrada ao site TRCON.",
                "https://trcongroup.com.br/novidades/lancamento-sirius-marketing",
                "Tecnologia",
                "sirius-marketing",
                Instant.parse("2026-07-22T18:00:00Z"),
                "content-123-v1",
                "Sirius Marketing AI",
                "lancamento-sirius-marketing",
                "Corpo completo do artigo com varios paragrafos.",
                "Lancamento Sirius Marketing",
                "Plataforma editorial integrada ao site TRCON.",
                "https://images.unsplash.com/photo-cover-example");

        ResponseEntity<InternalNewsCreateResponse> createResponse =
                restTemplate.postForEntity("/api/internal/news", entity(request), InternalNewsCreateResponse.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody()).isNotNull();
        assertThat(createResponse.getBody().duplicate()).isFalse();

        ResponseEntity<NewsArticleResponse> articleResponse =
                restTemplate.getForEntity("/api/public/news/lancamento-sirius-marketing", NewsArticleResponse.class);

        assertThat(articleResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(articleResponse.getBody().body()).contains("Corpo completo");
        assertThat(articleResponse.getBody().metaTitle()).isEqualTo("Lancamento Sirius Marketing");
        assertThat(articleResponse.getBody().coverImageUrl())
                .isEqualTo("https://images.unsplash.com/photo-cover-example");

        ResponseEntity<NewsListResponse> publicResponse =
                restTemplate.getForEntity("/api/public/news?category=Tecnologia", NewsListResponse.class);

        assertThat(publicResponse.getBody().items()).hasSize(1);
        assertThat(publicResponse.getBody().items().get(0).slug()).isEqualTo("lancamento-sirius-marketing");
    }

    @Test
    void deveAtualizarArtigoExistentePorExternalId() {
        newsRepository.deleteAll();
        InternalNewsCreateRequest request = new InternalNewsCreateRequest(
                "Titulo v1",
                "Resumo v1",
                "https://trcongroup.com.br/novidades/titulo-v1",
                "IA",
                "trcon",
                Instant.now(),
                "dup-key-1",
                null,
                "titulo-v1",
                "Corpo v1",
                null,
                null,
                null);

        restTemplate.postForEntity("/api/internal/news", entity(request), InternalNewsCreateResponse.class);

        InternalNewsCreateRequest updated = new InternalNewsCreateRequest(
                "Titulo v2",
                "Resumo v2",
                "https://trcongroup.com.br/novidades/titulo-v2",
                "IA",
                "trcon",
                Instant.now(),
                "dup-key-1",
                null,
                "titulo-v2",
                "Corpo v2 atualizado",
                null,
                null,
                null);

        ResponseEntity<InternalNewsCreateResponse> second =
                restTemplate.postForEntity("/api/internal/news", entity(updated), InternalNewsCreateResponse.class);

        assertThat(second.getBody().duplicate()).isTrue();
        assertThat(newsRepository.count()).isEqualTo(1);

        ResponseEntity<NewsArticleResponse> articleResponse =
                restTemplate.getForEntity("/api/public/news/titulo-v2", NewsArticleResponse.class);

        assertThat(articleResponse.getBody().title()).isEqualTo("Titulo v2");
        assertThat(articleResponse.getBody().body()).contains("Corpo v2");
    }

    @Test
    void deveSerIdempotentePorExternalId() {
        newsRepository.deleteAll();
        InternalNewsCreateRequest request = new InternalNewsCreateRequest(
                "Titulo",
                "Resumo",
                "https://example.com/a",
                "IA",
                "trcon",
                Instant.now(),
                "dup-key-2",
                null,
                null,
                null,
                null,
                null,
                null);

        ResponseEntity<InternalNewsCreateResponse> first =
                restTemplate.postForEntity("/api/internal/news", entity(request), InternalNewsCreateResponse.class);
        ResponseEntity<InternalNewsCreateResponse> second =
                restTemplate.postForEntity("/api/internal/news", entity(request), InternalNewsCreateResponse.class);

        assertThat(first.getBody().id()).isEqualTo(second.getBody().id());
        assertThat(second.getBody().duplicate()).isTrue();
        assertThat(newsRepository.count()).isEqualTo(1);
    }

    @Test
    void deveRejeitarSemApiKey() {
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/internal/news",
                new HttpEntity<>(new InternalNewsCreateRequest(
                        "Titulo",
                        "Resumo",
                        "https://example.com/a",
                        "IA",
                        "trcon",
                        Instant.now(),
                        "no-key",
                        null,
                        null,
                        null,
                        null,
                        null,
                        null)),
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private HttpEntity<InternalNewsCreateRequest> entity(InternalNewsCreateRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-Key", API_KEY);
        return new HttpEntity<>(request, headers);
    }
}
