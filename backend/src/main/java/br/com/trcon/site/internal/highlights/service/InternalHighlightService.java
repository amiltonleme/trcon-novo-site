package br.com.trcon.site.internal.highlights.service;

import br.com.trcon.site.highlights.domain.DailyHighlight;
import br.com.trcon.site.highlights.repository.HighlightRepository;
import br.com.trcon.site.internal.highlights.dto.InternalHighlightCreateRequest;
import br.com.trcon.site.internal.highlights.dto.InternalHighlightCreateResponse;
import br.com.trcon.site.news.domain.NewsQueryInvalidaException;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InternalHighlightService {

    private static final Set<String> ALLOWED_CATEGORIES = Set.of("IA", "Tecnologia", "Financas", "Mercado");
    private static final int DEFAULT_PRIORITY = 50;

    private final HighlightRepository highlightRepository;

    public InternalHighlightService(HighlightRepository highlightRepository) {
        this.highlightRepository = highlightRepository;
    }

    @Transactional
    public InternalHighlightCreateResponse criar(InternalHighlightCreateRequest request) {
        if (!ALLOWED_CATEGORIES.contains(request.category())) {
            throw new NewsQueryInvalidaException(
                    "category deve ser um dos valores suportados: " + ALLOWED_CATEGORIES);
        }

        return highlightRepository
                .findByExternalId(request.externalId().trim())
                .map(existing -> new InternalHighlightCreateResponse(existing.getId(), true))
                .orElseGet(() -> {
                    int priority = request.priority() != null ? request.priority() : DEFAULT_PRIORITY;
                    String link = request.link() == null || request.link().isBlank() ? null : request.link().trim();
                    DailyHighlight highlight = DailyHighlight.fromMarketing(
                            request.category(),
                            request.title().trim(),
                            request.summary().trim(),
                            link,
                            priority,
                            request.publishedAt(),
                            request.externalId().trim());
                    DailyHighlight saved = highlightRepository.save(highlight);
                    return new InternalHighlightCreateResponse(saved.getId(), false);
                });
    }
}
