package br.com.trcon.site.chat.integration;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record DeepSeekChatResponse(List<Choice> choices, Usage usage) {
    public record Choice(Message message) {}
    public record Message(String content) {}
    public record Usage(@JsonProperty("prompt_tokens") int promptTokens,
                        @JsonProperty("completion_tokens") int completionTokens) {}
}
