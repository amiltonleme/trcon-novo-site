package br.com.trcon.site.news.dto.response;

import java.time.Instant;
import java.util.UUID;

public record NewsItemResponse(
        UUID id,
        String source,
        String category,
        String title,
        String summary,
        String url,
        Instant publishedAt
) {
}
