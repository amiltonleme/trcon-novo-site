package br.com.trcon.site.chat.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record ChatRequest(
        @NotBlank @Size(max = 800) String message,
        @Valid List<ChatMessageDto> history,
        @NotBlank @Size(max = 80) String origem) {}
