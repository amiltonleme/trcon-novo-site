package br.com.trcon.site.internal.news.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.trcon.site.internal.news.dto.InternalNewsCreateRequest;
import br.com.trcon.site.internal.news.dto.InternalNewsCreateResponse;
import br.com.trcon.site.news.domain.NewsItem;
import br.com.trcon.site.news.domain.NewsQueryInvalidaException;
import br.com.trcon.site.news.repository.NewsRepository;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InternalNewsServiceTest {

    @Mock
    private NewsRepository newsRepository;

    @Test
    void rejeitaCategoriaInvalida() {
        InternalNewsService service = new InternalNewsService(newsRepository);

        assertThatThrownBy(() -> service.criar(baseRequest("Invalida", "ext-cat", null, null, null, null, null, null)))
                .isInstanceOf(NewsQueryInvalidaException.class)
                .hasMessageContaining("category");
        verify(newsRepository, never()).save(any());
    }

    @Test
    void rejeitaCoverImageUrlInvalida() {
        InternalNewsService service = new InternalNewsService(newsRepository);

        assertThatThrownBy(() -> service.criar(baseRequest(
                        "IA", "ext-cover-bad", null, null, null, null, null, "http://bad.example/a.jpg")))
                .isInstanceOf(NewsQueryInvalidaException.class)
                .hasMessageContaining("HTTPS");
        verify(newsRepository, never()).save(any());
    }

    @Test
    void criaNovoComDefaultsDeSourceBodyEMeta() {
        InternalNewsService service = new InternalNewsService(newsRepository);
        when(newsRepository.findByExternalId("ext-new")).thenReturn(Optional.empty());
        when(newsRepository.findBySlug("titulo-padrao")).thenReturn(Optional.empty());
        when(newsRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        InternalNewsCreateResponse response = service.criar(
                baseRequest("Financas", "ext-new", "  ", null, "  ", "  ", "  ", null));

        assertThat(response.duplicate()).isFalse();
        ArgumentCaptor<NewsItem> captor = ArgumentCaptor.forClass(NewsItem.class);
        verify(newsRepository).save(captor.capture());
        NewsItem saved = captor.getValue();
        assertThat(saved.getSource()).isEqualTo("Sirius Marketing");
        assertThat(saved.getBody()).isEqualTo("Resumo");
        assertThat(saved.getMetaTitle()).isEqualTo("Titulo padrao");
        assertThat(saved.getMetaDescription()).isEqualTo("Resumo");
        assertThat(saved.getCoverImageUrl()).isNull();
        assertThat(saved.getSlug()).isEqualTo("titulo-padrao");
    }

    @Test
    void criaComCoverConvertidaDoUnsplashEMetaExplicitos() {
        InternalNewsService service = new InternalNewsService(newsRepository);
        when(newsRepository.findByExternalId("ext-cover")).thenReturn(Optional.empty());
        when(newsRepository.findBySlug("slug-custom")).thenReturn(Optional.empty());
        when(newsRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        InternalNewsCreateResponse response = service.criar(baseRequest(
                "Tecnologia",
                "ext-cover",
                "Fonte X",
                "slug-custom",
                "Corpo completo",
                "Meta Title",
                "Meta Desc",
                "https://unsplash.com/photos/AbCdEfGhIjK"));

        assertThat(response.duplicate()).isFalse();
        ArgumentCaptor<NewsItem> captor = ArgumentCaptor.forClass(NewsItem.class);
        verify(newsRepository).save(captor.capture());
        assertThat(captor.getValue().getCoverImageUrl())
                .isEqualTo("https://unsplash.com/photos/AbCdEfGhIjK/download?force=true&w=1600");
        assertThat(captor.getValue().getSource()).isEqualTo("Fonte X");
        assertThat(captor.getValue().getBody()).isEqualTo("Corpo completo");
        assertThat(captor.getValue().getMetaTitle()).isEqualTo("Meta Title");
        assertThat(captor.getValue().getMetaDescription()).isEqualTo("Meta Desc");
    }

    @Test
    void atualizaQuandoExternalIdExiste() {
        InternalNewsService service = new InternalNewsService(newsRepository);
        NewsItem existing = NewsItem.fromMarketing(
                "Sirius Marketing",
                "IA",
                "Antigo",
                "Resumo antigo",
                "https://example.com/old",
                Instant.parse("2026-01-01T00:00:00Z"),
                "trcon",
                "ext-dup",
                "antigo",
                "corpo",
                "meta",
                "desc",
                null);
        when(newsRepository.findByExternalId("ext-dup")).thenReturn(Optional.of(existing));
        when(newsRepository.findBySlug("novo-slug")).thenReturn(Optional.of(existing));
        when(newsRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        InternalNewsCreateResponse response = service.criar(baseRequest(
                "Mercado",
                "ext-dup",
                "Fonte",
                "novo-slug",
                "Corpo novo",
                "Meta",
                "Desc",
                "https://images.unsplash.com/photo-x"));

        assertThat(response.duplicate()).isTrue();
        assertThat(response.id()).isEqualTo(existing.getId());
        assertThat(existing.getTitle()).isEqualTo("Titulo padrao");
        assertThat(existing.getCoverImageUrl()).isEqualTo("https://images.unsplash.com/photo-x");
        assertThat(existing.getSlug()).isEqualTo("novo-slug");
    }

    @Test
    void resolveConflitoDeSlugComSufixo() {
        InternalNewsService service = new InternalNewsService(newsRepository);
        NewsItem other = NewsItem.fromMarketing(
                "x",
                "IA",
                "t",
                "s",
                "https://example.com/o",
                Instant.now(),
                "trcon",
                "other",
                "titulo-padrao",
                "b",
                "m",
                "d",
                null);
        when(newsRepository.findByExternalId("ext-slug")).thenReturn(Optional.empty());
        when(newsRepository.findBySlug("titulo-padrao")).thenReturn(Optional.of(other));
        when(newsRepository.findBySlug("titulo-padrao-2")).thenReturn(Optional.empty());
        when(newsRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        InternalNewsCreateResponse response =
                service.criar(baseRequest("IA", "ext-slug", null, null, null, null, null, null));

        assertThat(response.duplicate()).isFalse();
        ArgumentCaptor<NewsItem> captor = ArgumentCaptor.forClass(NewsItem.class);
        verify(newsRepository).save(captor.capture());
        assertThat(captor.getValue().getSlug()).isEqualTo("titulo-padrao-2");
    }

    @Test
    void fallbackDeSlugAposMuitosConflitos() {
        InternalNewsService service = new InternalNewsService(newsRepository);
        NewsItem other = NewsItem.fromMarketing(
                "x",
                "IA",
                "t",
                "s",
                "https://example.com/o",
                Instant.now(),
                "trcon",
                "other",
                "titulo-padrao",
                "b",
                "m",
                "d",
                null);
        when(newsRepository.findByExternalId("ext-many")).thenReturn(Optional.empty());
        when(newsRepository.findBySlug(any())).thenReturn(Optional.of(other));
        when(newsRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        InternalNewsCreateResponse response =
                service.criar(baseRequest("IA", "ext-many", null, null, null, null, null, null));

        ArgumentCaptor<NewsItem> captor = ArgumentCaptor.forClass(NewsItem.class);
        verify(newsRepository).save(captor.capture());
        assertThat(captor.getValue().getSlug())
                .isEqualTo("titulo-padrao-" + Math.abs("ext-many".hashCode()));
        assertThat(response.duplicate()).isFalse();
    }

    private static InternalNewsCreateRequest baseRequest(
            String category,
            String externalId,
            String source,
            String slug,
            String body,
            String metaTitle,
            String metaDescription,
            String coverImageUrl) {
        return new InternalNewsCreateRequest(
                "Titulo padrao",
                "Resumo",
                "https://example.com/a",
                category,
                "trcon",
                Instant.parse("2026-07-01T00:00:00Z"),
                externalId,
                source,
                slug,
                body,
                metaTitle,
                metaDescription,
                coverImageUrl);
    }
}
