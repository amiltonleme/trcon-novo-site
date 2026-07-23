package br.com.trcon.site.internal.highlights.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record InternalHighlightCreateRequest(
        @NotBlank @Size(max = 200) String title,
        @NotBlank @Size(max = 600) String summary,
        @Size(max = 300) String link,
        @NotBlank @Size(max = 80) String category,
        @NotNull Instant publishedAt,
        @NotBlank @Size(max = 120) String externalId,
        @Min(1) @Max(999) Integer priority) {}
