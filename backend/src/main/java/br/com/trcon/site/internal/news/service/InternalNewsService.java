package br.com.trcon.site.internal.news.service;

import br.com.trcon.site.internal.news.dto.InternalNewsCreateRequest;
import br.com.trcon.site.internal.news.dto.InternalNewsCreateResponse;
import br.com.trcon.site.news.domain.NewsItem;
import br.com.trcon.site.news.domain.NewsQueryInvalidaException;
import br.com.trcon.site.news.repository.NewsRepository;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InternalNewsService {

    private static final Set<String> ALLOWED_CATEGORIES = Set.of("IA", "Tecnologia", "Financas", "Mercado");

    private final NewsRepository newsRepository;

    public InternalNewsService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    @Transactional
    public InternalNewsCreateResponse criar(InternalNewsCreateRequest request) {
        if (!ALLOWED_CATEGORIES.contains(request.category())) {
            throw new NewsQueryInvalidaException(
                    "category deve ser um dos valores suportados: " + ALLOWED_CATEGORIES);
        }

        return newsRepository
                .findByExternalId(request.externalId())
                .map(existing -> new InternalNewsCreateResponse(existing.getId(), true))
                .orElseGet(() -> {
                    String source = request.source() == null || request.source().isBlank()
                            ? "Sirius Marketing"
                            : request.source().trim();
                    NewsItem item = NewsItem.fromMarketing(
                            source,
                            request.category(),
                            request.title().trim(),
                            request.summary().trim(),
                            request.url().trim(),
                            request.publishedAt(),
                            request.brandSlug().trim(),
                            request.externalId().trim());
                    NewsItem saved = newsRepository.save(item);
                    return new InternalNewsCreateResponse(saved.getId(), false);
                });
    }
}
