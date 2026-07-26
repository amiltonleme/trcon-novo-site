package br.com.trcon.site.internal.economytips.dto;

import java.util.UUID;

public record InternalEconomyTipCreateResponse(UUID id, boolean duplicate) {}
