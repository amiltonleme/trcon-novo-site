package br.com.trcon.site.internal.economytips.controller;

import br.com.trcon.site.TestcontainersConfiguration;
import br.com.trcon.site.economytips.dto.response.EconomyTipListResponse;
import br.com.trcon.site.economytips.repository.EconomyTipRepository;
import br.com.trcon.site.internal.economytips.dto.InternalEconomyTipCreateRequest;
import br.com.trcon.site.internal.economytips.dto.InternalEconomyTipCreateResponse;
import java.time.Instant;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class InternalEconomyTipControllerIT {

    private static final String API_KEY = "test-internal-key";

    @DynamicPropertySource
    static void apiKey(DynamicPropertyRegistry registry) {
        registry.add("trcon.site.internal-api-key", () -> API_KEY);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EconomyTipRepository economyTipRepository;

    @Test
    void deveCriarDicaEExporNoEndpointPublico() {
        economyTipRepository.deleteAll();

        InternalEconomyTipCreateRequest request = new InternalEconomyTipCreateRequest(
                "Educacao",
                "tag-blue",
                "Guia TRCONGROUP: reserva de emergencia em 5 passos",
                "Monte sua reserva com produtos liquidos antes de buscar rentabilidade alta.",
                "https://trcongroup.com.br",
                "Ler guia",
                true,
                5,
                Instant.now(),
                "content-edu-1-v1",
                "sirius-marketing",
                "Sirius Marketing",
                null,
                null);

        ResponseEntity<InternalEconomyTipCreateResponse> createResponse = restTemplate.postForEntity(
                "/api/internal/economy-tips", entity(request), InternalEconomyTipCreateResponse.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody()).isNotNull();
        assertThat(createResponse.getBody().duplicate()).isFalse();

        ResponseEntity<EconomyTipListResponse> publicResponse =
                restTemplate.getForEntity("/api/public/economy-tips", EconomyTipListResponse.class);

        assertThat(publicResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(publicResponse.getBody().items()).hasSize(1);
        assertThat(publicResponse.getBody().disclaimer()).contains("educacional");
        assertThat(publicResponse.getBody().items().get(0).title())
                .isEqualTo("Guia TRCONGROUP: reserva de emergencia em 5 passos");
    }

    @Test
    void deveSerIdempotentePorExternalId() {
        economyTipRepository.deleteAll();
        InternalEconomyTipCreateRequest request = new InternalEconomyTipCreateRequest(
                "Mercado",
                "tag-blue",
                "Titulo",
                "Corpo educacional",
                "https://example.com/a",
                null,
                false,
                null,
                Instant.now(),
                "dup-edu-1",
                null,
                null,
                null,
                null);

        ResponseEntity<InternalEconomyTipCreateResponse> first =
                restTemplate.postForEntity("/api/internal/economy-tips", entity(request), InternalEconomyTipCreateResponse.class);
        ResponseEntity<InternalEconomyTipCreateResponse> second =
                restTemplate.postForEntity("/api/internal/economy-tips", entity(request), InternalEconomyTipCreateResponse.class);

        assertThat(first.getBody().id()).isEqualTo(second.getBody().id());
        assertThat(second.getBody().duplicate()).isTrue();
        assertThat(economyTipRepository.count()).isEqualTo(1);
    }

    @Test
    void deveAtualizarTipExistenteAoRepublicar() {
        economyTipRepository.deleteAll();
        InternalEconomyTipCreateRequest first = new InternalEconomyTipCreateRequest(
                "Mercado",
                "tag-blue",
                "Titulo antigo",
                "Resumo antigo",
                "https://trcongroup.com.br",
                "Ler mais",
                true,
                5,
                Instant.now().minusSeconds(3600),
                "republish-edu-1",
                "trcon",
                "TRCON",
                null,
                null);

        restTemplate.postForEntity("/api/internal/economy-tips", entity(first), InternalEconomyTipCreateResponse.class);

        InternalEconomyTipCreateRequest updated = new InternalEconomyTipCreateRequest(
                "Mercado",
                "tag-blue",
                "Titulo novo",
                "Resumo novo",
                "https://trcongroup.com.br/novidades/titulo-novo",
                "Ler mais",
                true,
                5,
                Instant.now(),
                "republish-edu-1",
                "trcon",
                "TRCON",
                null,
                null);

        ResponseEntity<InternalEconomyTipCreateResponse> response = restTemplate.postForEntity(
                "/api/internal/economy-tips", entity(updated), InternalEconomyTipCreateResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().duplicate()).isTrue();
        assertThat(economyTipRepository.count()).isEqualTo(1);

        ResponseEntity<EconomyTipListResponse> publicResponse =
                restTemplate.getForEntity("/api/public/economy-tips", EconomyTipListResponse.class);
        assertThat(publicResponse.getBody().items()).hasSize(1);
        assertThat(publicResponse.getBody().items().get(0).title()).isEqualTo("Titulo novo");
        assertThat(publicResponse.getBody().items().get(0).url())
                .isEqualTo("https://trcongroup.com.br/novidades/titulo-novo");
    }

    private HttpEntity<InternalEconomyTipCreateRequest> entity(InternalEconomyTipCreateRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-Key", API_KEY);
        return new HttpEntity<>(request, headers);
    }
}
