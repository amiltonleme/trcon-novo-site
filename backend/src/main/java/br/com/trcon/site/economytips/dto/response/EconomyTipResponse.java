package br.com.trcon.site.economytips.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.UUID;

public record EconomyTipResponse(
        UUID id,
        String tag,
        @JsonProperty("tag_class") String tagClass,
        String title,
        String body,
        List<String> meta,
        String url,
        @JsonProperty("link_label") String linkLabel,
        Boolean featured) {}
