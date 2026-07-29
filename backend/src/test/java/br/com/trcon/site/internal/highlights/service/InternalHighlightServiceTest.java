package br.com.trcon.site.internal.highlights.service;

import br.com.trcon.site.highlights.domain.DailyHighlight;
import br.com.trcon.site.highlights.repository.HighlightRepository;
import br.com.trcon.site.internal.highlights.dto.InternalHighlightCreateRequest;
import br.com.trcon.site.internal.highlights.dto.InternalHighlightCreateResponse;
import br.com.trcon.site.news.domain.NewsQueryInvalidaException;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InternalHighlightServiceTest {

    @Mock
    private HighlightRepository highlightRepository;

    @Test
    void rejeitaCategoriaInvalida() {
        InternalHighlightService service = new InternalHighlightService(highlightRepository);
        InternalHighlightCreateRequest request = new InternalHighlightCreateRequest(
                "t", "s", "https://x", "Invalida", Instant.now(), "ext-1", 10);

        assertThatThrownBy(() -> service.criar(request)).isInstanceOf(NewsQueryInvalidaException.class);
        verify(highlightRepository, never()).save(any());
    }

    @Test
    void usaPrioridadeDefaultELinkNuloQuandoEmBranco() {
        InternalHighlightService service = new InternalHighlightService(highlightRepository);
        when(highlightRepository.findByExternalId("ext-2")).thenReturn(Optional.empty());
        when(highlightRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        InternalHighlightCreateRequest request = new InternalHighlightCreateRequest(
                "Titulo", "Resumo", "  ", "IA", Instant.parse("2026-07-01T00:00:00Z"), "ext-2", null);

        InternalHighlightCreateResponse response = service.criar(request);

        assertThat(response.duplicate()).isFalse();
        ArgumentCaptor<DailyHighlight> captor = ArgumentCaptor.forClass(DailyHighlight.class);
        verify(highlightRepository).save(captor.capture());
        assertThat(captor.getValue().getPriority()).isEqualTo(50);
        assertThat(captor.getValue().getLink()).isNull();
    }

    @Test
    void retornaDuplicadoQuandoExternalIdExiste() {
        InternalHighlightService service = new InternalHighlightService(highlightRepository);
        DailyHighlight existing = DailyHighlight.fromMarketing(
                "IA", "t", "s", "https://x", 1, Instant.now(), "ext-3");
        when(highlightRepository.findByExternalId("ext-3")).thenReturn(Optional.of(existing));

        InternalHighlightCreateResponse response = service.criar(new InternalHighlightCreateRequest(
                "t", "s", "https://x", "IA", Instant.now(), "ext-3", 1));

        assertThat(response.duplicate()).isTrue();
        assertThat(response.id()).isEqualTo(existing.getId());
        verify(highlightRepository, never()).save(any());
    }
}
