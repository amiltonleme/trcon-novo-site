package br.com.trcon.site.highlights.controller;

import br.com.trcon.site.TestcontainersConfiguration;
import br.com.trcon.site.highlights.domain.DailyHighlight;
import br.com.trcon.site.highlights.dto.response.HighlightListResponse;
import br.com.trcon.site.highlights.repository.HighlightRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class HighlightControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private HighlightRepository highlightRepository;

    @Test
    void deveRetornarApenasHighlightsAtivosOrdenados() {
        highlightRepository.deleteAll();
        salvar("IA", 2, true);
        salvar("Tecnologia", 1, true);
        salvar("Mercado", 1, false);

        ResponseEntity<HighlightListResponse> response =
                restTemplate.getForEntity("/api/public/highlights", HighlightListResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().items()).hasSize(2);
        assertThat(response.getBody().items().get(0).category()).isEqualTo("Tecnologia");
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHaHighlightAtivo() {
        highlightRepository.deleteAll();

        ResponseEntity<HighlightListResponse> response =
                restTemplate.getForEntity("/api/public/highlights", HighlightListResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().items()).isEmpty();
    }

    private void salvar(String category, int priority, boolean active) {
        DailyHighlight highlight = DailyHighlight.novo(
                category, "Título " + category, "Resumo " + category, "/link", priority, active, Instant.now());
        highlightRepository.save(highlight);
    }
}
