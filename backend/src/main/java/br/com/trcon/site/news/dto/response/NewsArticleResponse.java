package br.com.trcon.site.news.dto.response;

import java.time.Instant;
import java.util.UUID;

public record NewsArticleResponse(
        UUID id,
        String source,
        String category,
        String title,
        String summary,
        String url,
        String slug,
        String body,
        String metaTitle,
        String metaDescription,
        String coverImageUrl,
        String brandSlug,
        Instant publishedAt) {}
