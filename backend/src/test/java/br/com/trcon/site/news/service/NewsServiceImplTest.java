package br.com.trcon.site.news.service;

import br.com.trcon.site.news.domain.NewsItem;
import br.com.trcon.site.news.domain.NewsQueryInvalidaException;
import br.com.trcon.site.news.dto.response.NewsListResponse;
import br.com.trcon.site.news.mapper.NewsMapper;
import br.com.trcon.site.news.repository.NewsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Limit;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NewsServiceImplTest {

    @Mock
    private NewsRepository newsRepository;

    @Mock
    private NewsMapper newsMapper;

    private NewsServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new NewsServiceImpl(newsRepository, newsMapper);
    }

    @Test
    void deveListarSemFiltroDeCategoriaUsandoLimiteDefault() {
        List<NewsItem> itens = List.of();
        NewsListResponse esperado = NewsListResponse.of(List.of());
        when(newsRepository.findActiveExcludingCategory(eq("Educacao"), any(), any(Limit.class)))
                .thenReturn(itens);
        when(newsMapper.toListResponse(itens)).thenReturn(esperado);

        NewsListResponse resultado = service.listar(null, null);

        assertThat(resultado).isEqualTo(esperado);
    }

    @Test
    void deveFiltrarPorCategoriaValida() {
        List<NewsItem> itens = List.of();
        NewsListResponse esperado = NewsListResponse.of(List.of());
        when(newsRepository.findActiveByCategory(eq("IA"), any(), any(Limit.class))).thenReturn(itens);
        when(newsMapper.toListResponse(itens)).thenReturn(esperado);

        NewsListResponse resultado = service.listar("IA", 10);

        assertThat(resultado).isEqualTo(esperado);
    }

    @Test
    void deveLancarExcecaoQuandoCategoriaNaoSuportada() {
        assertThatThrownBy(() -> service.listar("Categoria-Invalida", null))
                .isInstanceOf(NewsQueryInvalidaException.class);
    }

    @Test
    void deveLancarExcecaoQuandoLimiteMenorQueUm() {
        assertThatThrownBy(() -> service.listar(null, 0))
                .isInstanceOf(NewsQueryInvalidaException.class);
    }

    @Test
    void deveLancarExcecaoQuandoLimiteMaiorQueTeto() {
        assertThatThrownBy(() -> service.listar(null, 51))
                .isInstanceOf(NewsQueryInvalidaException.class);
    }

    @Test
    void listarComSlugUsaLimiteDefaultEClamp() {
        when(newsRepository.findActiveWithSlugExcludingCategory(eq("Educacao"), any(), any(Limit.class)))
                .thenReturn(List.of());
        when(newsMapper.toListResponse(List.of())).thenReturn(NewsListResponse.of(List.of()));

        assertThat(service.listarComSlug(null).items()).isEmpty();
        assertThat(service.listarComSlug(0).items()).isEmpty();
        assertThat(service.listarComSlug(999).items()).isEmpty();
    }

    @Test
    void buscarPorSlugRejeitaVazio() {
        assertThatThrownBy(() -> service.buscarPorSlug("  "))
                .isInstanceOf(NewsQueryInvalidaException.class);
        assertThatThrownBy(() -> service.buscarPorSlug(null))
                .isInstanceOf(NewsQueryInvalidaException.class);
    }
}
