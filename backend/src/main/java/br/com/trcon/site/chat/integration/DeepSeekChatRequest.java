package br.com.trcon.site.chat.integration;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public record DeepSeekChatRequest(
        String model, List<Message> messages, double temperature,
        @JsonProperty("max_tokens") int maxTokens,
        @JsonProperty("response_format") Map<String, String> responseFormat) {
    public record Message(String role, String content) {}
}
