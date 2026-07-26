package br.com.trcon.site.economytips.dto.response;

import java.time.Instant;
import java.util.List;

public record EconomyTipListResponse(
        Instant generatedAt, String disclaimer, List<EconomyTipResponse> items) {

    public static final String DEFAULT_DISCLAIMER =
            "Conteudo educacional. Nao constitui recomendacao individual de investimento.";

    public static EconomyTipListResponse of(List<EconomyTipResponse> items) {
        return new EconomyTipListResponse(Instant.now(), DEFAULT_DISCLAIMER, items);
    }
}
