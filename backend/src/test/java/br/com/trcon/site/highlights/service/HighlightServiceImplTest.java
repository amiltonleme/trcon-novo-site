package br.com.trcon.site.highlights.service;

import br.com.trcon.site.highlights.domain.DailyHighlight;
import br.com.trcon.site.highlights.dto.response.HighlightListResponse;
import br.com.trcon.site.highlights.dto.response.HighlightResponse;
import br.com.trcon.site.highlights.mapper.HighlightMapper;
import br.com.trcon.site.highlights.repository.HighlightRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Limit;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HighlightServiceImplTest {

    @Mock
    private HighlightRepository highlightRepository;

    @Mock
    private HighlightMapper highlightMapper;

    @Test
    void deveListarApenasAtivosOrdenadosPorPrioridade() {
        HighlightServiceImpl service = new HighlightServiceImpl(highlightRepository, highlightMapper);
        List<DailyHighlight> ativos = List.of();
        HighlightResponse item = new HighlightResponse(
                UUID.randomUUID(), "IA", "Título", "Resumo", "/link", 1, Instant.now());
        HighlightListResponse esperado = HighlightListResponse.of(List.of(item));

        when(highlightRepository.findByActiveTrueOrderByPriorityAscPublishedAtDesc(any(Limit.class)))
                .thenReturn(ativos);
        when(highlightMapper.toListResponse(ativos)).thenReturn(esperado);

        HighlightListResponse resultado = service.listarAtivos();

        assertThat(resultado).isEqualTo(esperado);
        assertThat(resultado.items()).hasSize(1);
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHaDestaqueAtivo() {
        HighlightServiceImpl service = new HighlightServiceImpl(highlightRepository, highlightMapper);
        when(highlightRepository.findByActiveTrueOrderByPriorityAscPublishedAtDesc(any(Limit.class)))
                .thenReturn(List.of());
        when(highlightMapper.toListResponse(List.of())).thenReturn(HighlightListResponse.of(List.of()));

        HighlightListResponse resultado = service.listarAtivos();

        assertThat(resultado.items()).isEmpty();
    }
}
