package br.com.trcon.site.highlights.dto.response;

import java.time.Instant;
import java.util.List;

public record HighlightListResponse(
        Instant generatedAt,
        List<HighlightResponse> items
) {

    public static HighlightListResponse of(List<HighlightResponse> items) {
        return new HighlightListResponse(Instant.now(), items);
    }
}
