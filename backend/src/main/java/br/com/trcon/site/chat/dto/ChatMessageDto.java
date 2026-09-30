package br.com.trcon.site.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ChatMessageDto(
        @NotBlank @Pattern(regexp = "user|assistant") String role,
        @NotBlank @Size(max = 800) String content) {}
