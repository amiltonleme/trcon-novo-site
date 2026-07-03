package br.com.trcon.site.news.controller;

import br.com.trcon.site.TestcontainersConfiguration;
import br.com.trcon.site.news.domain.NewsItem;
import br.com.trcon.site.news.dto.response.NewsListResponse;
import br.com.trcon.site.news.repository.NewsRepository;
import br.com.trcon.site.shared.exception.ErrorResponse;
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
class NewsControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private NewsRepository newsRepository;

    @Test
    void deveListarNoticiasOrdenadasPorDataDecrescente() {
        newsRepository.deleteAll();
        salvar("IA", Instant.parse("2026-07-01T08:00:00Z"));
        salvar("IA", Instant.parse("2026-07-02T08:00:00Z"));

        ResponseEntity<NewsListResponse> response =
                restTemplate.getForEntity("/api/public/news", NewsListResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().items()).hasSize(2);
        assertThat(response.getBody().items().get(0).publishedAt()).isEqualTo(Instant.parse("2026-07-02T08:00:00Z"));
    }

    @Test
    void deveFiltrarPorCategoria() {
        newsRepository.deleteAll();
        salvar("IA", Instant.now());
        salvar("Mercado", Instant.now());

        ResponseEntity<NewsListResponse> response =
                restTemplate.getForEntity("/api/public/news?category=Mercado", NewsListResponse.class);

        assertThat(response.getBody().items()).hasSize(1);
        assertThat(response.getBody().items().get(0).category()).isEqualTo("Mercado");
    }

    @Test
    void deveResponder400QuandoCategoriaInvalida() {
        ResponseEntity<ErrorResponse> response =
                restTemplate.getForEntity("/api/public/news?category=Invalida", ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().code()).isEqualTo("VALIDATION_ERROR");
    }

    private void salvar(String category, Instant publishedAt) {
        NewsItem item = NewsItem.novo("Google News RSS", category, "Título", "Resumo",
                "https://example.com/noticia", publishedAt, "batch-1");
        newsRepository.save(item);
    }
}
