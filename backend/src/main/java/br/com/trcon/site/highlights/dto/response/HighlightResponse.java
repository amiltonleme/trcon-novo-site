package br.com.trcon.site.highlights.dto.response;

import java.time.Instant;
import java.util.UUID;

public record HighlightResponse(
        UUID id,
        String category,
        String title,
        String summary,
        String link,
        int priority,
        Instant publishedAt
) {
}
