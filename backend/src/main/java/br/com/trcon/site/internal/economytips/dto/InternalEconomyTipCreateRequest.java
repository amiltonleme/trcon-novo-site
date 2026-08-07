package br.com.trcon.site.internal.economytips.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record InternalEconomyTipCreateRequest(
        @NotBlank @Size(max = 40) String tag,
        @NotBlank @Size(max = 20) String tagClass,
        @NotBlank @Size(max = 200) String title,
        @NotBlank @Size(max = 600) String body,
        @Size(max = 500) String url,
        @Size(max = 40) String linkLabel,
        Boolean featured,
        Integer priority,
        @NotNull Instant publishedAt,
        @NotBlank @Size(max = 120) String externalId,
        @Size(max = 80) String brandSlug,
        @Size(max = 120) String source,
        @Min(0) Integer ttlDays,
        Instant expiresAt) {}
