package br.com.trcon.site.chat.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.trcon.site.chat.exception.ChatProviderUnavailableException;
import br.com.trcon.site.shared.config.ChatAiProperties;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

class DeepSeekChatClientTest {
    private HttpServer server;
    private String baseUrl;
    private final AtomicReference<String> body = new AtomicReference<>();
    private final AtomicReference<String> authorization = new AtomicReference<>();
    private final AtomicInteger status = new AtomicInteger(200);

    @BeforeEach
    void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/chat/completions", exchange -> {
            authorization.set(exchange.getRequestHeaders().getFirst("Authorization"));
            body.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            byte[] response = """
                    {"choices":[{"message":{"content":"{\\"reply\\":\\"ok\\"}"},"finish_reason":"stop"}],
                    "usage":{"prompt_tokens":12,"completion_tokens":4}}
                    """.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(status.get(), response.length);
            try (OutputStream output = exchange.getResponseBody()) { output.write(response); }
        });
        server.start();
        baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
    }

    @Test
    void enviaContratoCompativelComDeepSeek() {
        DeepSeekChatClient client = client();
        DeepSeekChatResponse response = client.complete(new DeepSeekChatRequest(
                "deepseek-flash", List.of(new DeepSeekChatRequest.Message("user", "Olá")),
                0.1, 400, Map.of("type", "json_object")));

        assertThat(authorization.get()).isEqualTo("Bearer test-key");
        assertThat(body.get())
                .contains("\"model\":\"deepseek-flash\"")
                .contains("\"max_tokens\":400")
                .contains("\"response_format\":{\"type\":\"json_object\"}");
        assertThat(response.choices().getFirst().finishReason()).isEqualTo("stop");
        assertThat(response.usage().promptTokens()).isEqualTo(12);
    }

    @Test
    void converteFalhaDoProvedorEmErroSeguro() {
        status.set(503);

        assertThatThrownBy(() -> client().complete(new DeepSeekChatRequest(
                "deepseek-flash", List.of(), 0.1, 400, Map.of("type", "json_object"))))
                .isInstanceOf(ChatProviderUnavailableException.class)
                .hasMessage("Assistente temporariamente indisponível.");
    }

    private DeepSeekChatClient client() {
        ChatAiProperties properties = new ChatAiProperties(true, false, "test-key", baseUrl,
                "deepseek-flash", 400, 6, 8, 10, 0.30, 1.20);
        return new DeepSeekChatClient(properties, RestClient.builder().baseUrl(baseUrl).build());
    }
}
