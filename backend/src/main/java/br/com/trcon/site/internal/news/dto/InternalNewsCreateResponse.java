package br.com.trcon.site.internal.news.dto;

import java.util.UUID;

public record InternalNewsCreateResponse(UUID id, boolean duplicate) {}
