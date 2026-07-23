package br.com.trcon.site.internal.news.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record InternalNewsCreateRequest(
        @NotBlank @Size(max = 200) String title,
        @NotBlank @Size(max = 600) String summary,
        @NotBlank @Size(max = 500) String url,
        @NotBlank @Size(max = 80) String category,
        @NotBlank @Size(max = 80) String brandSlug,
        @NotNull Instant publishedAt,
        @NotBlank @Size(max = 120) String externalId,
        @Size(max = 120) String source) {}
