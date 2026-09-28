package br.com.trcon.site.chat.integration;

import br.com.trcon.site.chat.exception.ChatProviderUnavailableException;
import br.com.trcon.site.shared.config.ChatAiProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class DeepSeekChatClient {
    private final ChatAiProperties properties;
    private final RestClient restClient;
    @Autowired
    public DeepSeekChatClient(ChatAiProperties properties) {
        this(properties, RestClient.builder().baseUrl(properties.baseUrl()).build());
    }
    DeepSeekChatClient(ChatAiProperties properties, RestClient restClient) {
        this.properties = properties; this.restClient = restClient;
    }
    public DeepSeekChatResponse complete(DeepSeekChatRequest request) {
        try {
            DeepSeekChatResponse response = restClient.post().uri("/chat/completions")
                    .header("Authorization", "Bearer " + properties.apiKey())
                    .body(request).retrieve().body(DeepSeekChatResponse.class);
            if (response == null || response.choices() == null || response.choices().isEmpty()) throw new ChatProviderUnavailableException();
            return response;
        } catch (RestClientException ex) {
            throw new ChatProviderUnavailableException();
        }
    }
}
