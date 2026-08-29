package br.com.trcon.site.internal.news.dto;

import java.util.UUID;

/**
 * {@code slug} é o slug efetivamente persistido — pode diferir do enviado na
 * requisição quando houve colisão e um sufixo foi aplicado (ver
 * {@code InternalNewsService#resolveUniqueSlug}). Chamadores que constroem
 * URLs de leitura (ex.: Educação Financeira) devem usar este valor, nunca o
 * slug computado localmente antes da chamada.
 */
public record InternalNewsCreateResponse(UUID id, boolean duplicate, String slug) {}
