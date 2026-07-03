package br.com.trcon.site.news.dto.response;

import java.time.Instant;
import java.util.List;

public record NewsListResponse(
        Instant generatedAt,
        List<NewsItemResponse> items
) {

    public static NewsListResponse of(List<NewsItemResponse> items) {
        return new NewsListResponse(Instant.now(), items);
    }
}
