package br.com.trcon.site.internal.highlights.controller;

import br.com.trcon.site.TestcontainersConfiguration;
import br.com.trcon.site.highlights.dto.response.HighlightListResponse;
import br.com.trcon.site.highlights.repository.HighlightRepository;
import br.com.trcon.site.internal.highlights.dto.InternalHighlightCreateRequest;
import br.com.trcon.site.internal.highlights.dto.InternalHighlightCreateResponse;
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
class InternalHighlightControllerIT {

    private static final String API_KEY = "test-internal-key";

    @DynamicPropertySource
    static void apiKey(DynamicPropertyRegistry registry) {
        registry.add("trcon.site.internal-api-key", () -> API_KEY);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private HighlightRepository highlightRepository;

    @Test
    void deveCriarHighlightEExporNoEndpointPublico() {
        highlightRepository.deleteAll();

        InternalHighlightCreateRequest request = new InternalHighlightCreateRequest(
                "Sirius Marketing integrado ao Radar",
                "Artigo editorial publicado via API interna.",
                "https://trcongroup.com.br/novidades/sirius-marketing",
                "Tecnologia",
                Instant.parse("2026-07-23T18:00:00Z"),
                "content-456-v1-radar",
                10);

        ResponseEntity<InternalHighlightCreateResponse> createResponse =
                restTemplate.postForEntity("/api/internal/highlights", entity(request), InternalHighlightCreateResponse.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody()).isNotNull();
        assertThat(createResponse.getBody().duplicate()).isFalse();

        ResponseEntity<HighlightListResponse> publicResponse =
                restTemplate.getForEntity("/api/public/highlights", HighlightListResponse.class);

        assertThat(publicResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(publicResponse.getBody().items()).hasSize(1);
        assertThat(publicResponse.getBody().items().get(0).title()).isEqualTo("Sirius Marketing integrado ao Radar");
    }

    @Test
    void deveSerIdempotentePorExternalId() {
        highlightRepository.deleteAll();
        InternalHighlightCreateRequest request = new InternalHighlightCreateRequest(
                "Titulo",
                "Resumo",
                "https://example.com/a",
                "IA",
                Instant.now(),
                "dup-highlight-1",
                null);

        ResponseEntity<InternalHighlightCreateResponse> first =
                restTemplate.postForEntity("/api/internal/highlights", entity(request), InternalHighlightCreateResponse.class);
        ResponseEntity<InternalHighlightCreateResponse> second =
                restTemplate.postForEntity("/api/internal/highlights", entity(request), InternalHighlightCreateResponse.class);

        assertThat(first.getBody().id()).isEqualTo(second.getBody().id());
        assertThat(second.getBody().duplicate()).isTrue();
        assertThat(highlightRepository.count()).isEqualTo(1);
    }

    @Test
    void deveRejeitarSemApiKey() {
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/internal/highlights",
                new HttpEntity<>(new InternalHighlightCreateRequest(
                        "Titulo",
                        "Resumo",
                        "https://example.com/a",
                        "IA",
                        Instant.now(),
                        "no-key",
                        null)),
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private HttpEntity<InternalHighlightCreateRequest> entity(InternalHighlightCreateRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-Key", API_KEY);
        return new HttpEntity<>(request, headers);
    }
}
