package br.com.trcon.site.internal.news.controller;

import br.com.trcon.site.TestcontainersConfiguration;
import br.com.trcon.site.internal.news.dto.InternalNewsCreateRequest;
import br.com.trcon.site.internal.news.dto.InternalNewsCreateResponse;
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
    void deveCriarNoticiaEditorialEExporNoEndpointPublico() {
        newsRepository.deleteAll();

        InternalNewsCreateRequest request = new InternalNewsCreateRequest(
                "Lancamento Sirius Marketing",
                "Plataforma editorial integrada ao site TRCON.",
                "https://trcongroup.com.br/novidades/sirius-marketing",
                "Tecnologia",
                "sirius-marketing",
                Instant.parse("2026-07-22T18:00:00Z"),
                "content-123-v1",
                "Sirius Marketing AI");

        ResponseEntity<InternalNewsCreateResponse> createResponse =
                restTemplate.postForEntity("/api/internal/news", entity(request), InternalNewsCreateResponse.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody()).isNotNull();
        assertThat(createResponse.getBody().duplicate()).isFalse();

        ResponseEntity<NewsListResponse> publicResponse =
                restTemplate.getForEntity("/api/public/news?category=Tecnologia", NewsListResponse.class);

        assertThat(publicResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(publicResponse.getBody().items()).hasSize(1);
        assertThat(publicResponse.getBody().items().get(0).title()).isEqualTo("Lancamento Sirius Marketing");
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
                "dup-key-1",
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
