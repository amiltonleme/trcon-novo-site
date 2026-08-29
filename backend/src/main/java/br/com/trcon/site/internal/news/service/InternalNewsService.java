package br.com.trcon.site.internal.news.service;

import br.com.trcon.site.internal.news.dto.InternalNewsCreateRequest;
import br.com.trcon.site.internal.news.dto.InternalNewsCreateResponse;
import br.com.trcon.site.news.domain.NewsItem;
import br.com.trcon.site.news.domain.NewsQueryInvalidaException;
import br.com.trcon.site.news.repository.NewsRepository;
import br.com.trcon.site.news.util.CoverImageUrls;
import br.com.trcon.site.news.util.SlugUtils;
import br.com.trcon.site.shared.config.ContentTtlProperties;
import br.com.trcon.site.shared.expiry.ExpiryInstantCalculator;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InternalNewsService {

    /** Educacao = página de leitura de newsletter/landing (fora do grid Novidades). */
    private static final Set<String> ALLOWED_CATEGORIES =
            Set.of("IA", "Tecnologia", "Financas", "Mercado", "Educacao");

    private final NewsRepository newsRepository;
    private final ExpiryInstantCalculator expiryInstantCalculator;
    private final ContentTtlProperties contentTtlProperties;

    public InternalNewsService(
            NewsRepository newsRepository,
            ExpiryInstantCalculator expiryInstantCalculator,
            ContentTtlProperties contentTtlProperties) {
        this.newsRepository = newsRepository;
        this.expiryInstantCalculator = expiryInstantCalculator;
        this.contentTtlProperties = contentTtlProperties;
    }

    @Transactional
    public InternalNewsCreateResponse criar(InternalNewsCreateRequest request) {
        if (!ALLOWED_CATEGORIES.contains(request.category())) {
            throw new NewsQueryInvalidaException(
                    "category deve ser um dos valores suportados: " + ALLOWED_CATEGORIES);
        }

        String source = resolveSource(request.source());
        String body = resolveBody(request.body(), request.summary());
        final String metaTitle = resolveMetaTitle(request.metaTitle(), request.title());
        final String metaDescription = resolveMetaDescription(request.metaDescription(), request.summary());
        final String coverImageUrl;
        try {
            coverImageUrl = CoverImageUrls.normalize(request.coverImageUrl());
        } catch (IllegalArgumentException ex) {
            throw new NewsQueryInvalidaException(ex.getMessage());
        }

        Instant expiresAt;
        try {
            expiresAt = expiryInstantCalculator.resolve(
                    request.publishedAt(),
                    request.ttlDays(),
                    request.expiresAt(),
                    contentTtlProperties.ttlDays());
        } catch (IllegalArgumentException ex) {
            throw new NewsQueryInvalidaException(ex.getMessage());
        }

        return newsRepository
                .findByExternalId(request.externalId())
                .map(existing -> {
                    String slug = resolveUniqueSlug(request.slug(), request.title(), request.externalId(), existing.getId());
                    existing.updateFromMarketing(
                            source,
                            request.category(),
                            request.title().trim(),
                            request.summary().trim(),
                            request.url().trim(),
                            request.publishedAt(),
                            request.brandSlug().trim(),
                            slug,
                            body,
                            metaTitle,
                            metaDescription,
                            coverImageUrl,
                            expiresAt);
                    NewsItem saved = newsRepository.save(existing);
                    return new InternalNewsCreateResponse(saved.getId(), true, saved.getSlug());
                })
                .orElseGet(() -> {
                    String slug = resolveUniqueSlug(request.slug(), request.title(), request.externalId(), null);
                    NewsItem item = NewsItem.fromMarketing(
                            source,
                            request.category(),
                            request.title().trim(),
                            request.summary().trim(),
                            request.url().trim(),
                            request.publishedAt(),
                            request.brandSlug().trim(),
                            request.externalId().trim(),
                            slug,
                            body,
                            metaTitle,
                            metaDescription,
                            coverImageUrl,
                            expiresAt);
                    NewsItem saved = newsRepository.save(item);
                    return new InternalNewsCreateResponse(saved.getId(), false, saved.getSlug());
                });
    }

    private String resolveSource(String source) {
        if (source == null || source.isBlank()) {
            return "Sirius Marketing";
        }
        return source.trim();
    }

    private String resolveBody(String body, String summary) {
        if (body != null && !body.isBlank()) {
            return body.trim();
        }
        return summary.trim();
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private String resolveMetaTitle(String metaTitle, String title) {
        String resolved = blankToNull(metaTitle);
        return resolved != null ? resolved : title.trim();
    }

    private String resolveMetaDescription(String metaDescription, String summary) {
        String resolved = blankToNull(metaDescription);
        return resolved != null ? resolved : summary.trim();
    }

    private String resolveUniqueSlug(String requestedSlug, String title, String externalId, UUID existingId) {
        String base = requestedSlug == null || requestedSlug.isBlank()
                ? SlugUtils.slugify(title)
                : SlugUtils.slugify(requestedSlug);
        String candidate = base;
        int suffix = 2;
        while (true) {
            var conflict = newsRepository.findBySlug(candidate);
            if (conflict.isEmpty() || (existingId != null && conflict.get().getId().equals(existingId))) {
                return candidate;
            }
            candidate = base + "-" + suffix;
            suffix++;
            if (suffix > 50) {
                return base + "-" + Math.abs(externalId.hashCode());
            }
        }
    }
}
