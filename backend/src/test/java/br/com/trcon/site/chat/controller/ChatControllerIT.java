package br.com.trcon.site.chat.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import br.com.trcon.site.TestcontainersConfiguration;
import br.com.trcon.site.chat.exception.ChatBudgetExceededException;
import br.com.trcon.site.chat.integration.DeepSeekChatClient;
import br.com.trcon.site.chat.integration.DeepSeekChatResponse;
import br.com.trcon.site.chat.service.ChatQuotaService;
import br.com.trcon.site.shared.exception.ErrorResponse;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "trcon.site.chat.ai.enabled=true",
        "trcon.site.chat.ai.stub-enabled=false",
        "trcon.site.chat.ai.api-key=test-key",
        "trcon.site.chat.ai.rate-limit-per-minute=2"
})
@Import(TestcontainersConfiguration.class)
class ChatControllerIT {
    @Autowired private TestRestTemplate restTemplate;
    @MockitoBean private DeepSeekChatClient client;
    @MockitoBean private ChatQuotaService quota;

    @BeforeEach
    void setUp() {
        reset(client, quota);
        when(client.complete(any())).thenReturn(new DeepSeekChatResponse(
                List.of(new DeepSeekChatResponse.Choice(
                        new DeepSeekChatResponse.Message("""
                                {"reply":"A TRCONGROUP atua com tecnologia e IA.",
                                "sourceIds":["company.current_focus"],"outOfScope":false,
                                "knowledgeMissing":false,"suggestContactForm":false,
                                "suggestCareersPage":false}
                                """), "stop")),
                new DeepSeekChatResponse.Usage(100, 20)));
    }

    @Test
    void responde200ComFonteValidada() {
        ResponseEntity<Map> response = post(validPayload(), uniqueIp(), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("reply", "A TRCONGROUP atua com tecnologia e IA.");
        assertThat(response.getBody().get("sourceIds")).isEqualTo(List.of("company.current_focus"));
    }

    @Test
    void usaEnderecoRemotoQuandoForwardedForNaoFoiEnviado() {
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/v1/site/chat", validPayload(), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void responde400ParaPayloadInvalidoInclusiveRoleAusente() {
        Map<String, Object> payload = Map.of(
                "message", "Olá",
                "origem", "site-trcon-chat-home",
                "history", List.of(Map.of("role", "", "content", "anterior")));

        ResponseEntity<ErrorResponse> response = post(payload, uniqueIp(), ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("VALIDATION_ERROR");
        assertThat(response.getBody().fields()).containsKey("history[0].role");
    }

    @Test
    void responde429QuandoOrcamentoEsgotou() {
        doThrow(new ChatBudgetExceededException()).when(quota).assertWithinBudget();

        ResponseEntity<ErrorResponse> response = post(validPayload(), uniqueIp(), ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("CHAT_BUDGET_EXCEEDED");
        assertThat(response.getBody().message()).doesNotContain("orçamento", "DeepSeek");
    }

    @Test
    void responde429AoExcederLimitePorIp() {
        String ip = uniqueIp();
        post(validPayload(), ip, Map.class);
        post(validPayload(), ip, Map.class);

        ResponseEntity<ErrorResponse> response = post(validPayload(), ip, ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("CHAT_RATE_LIMITED");
    }

    private Map<String, Object> validPayload() {
        return Map.of("message", "Quem é a TRCONGROUP?", "history", List.of(),
                "origem", "site-trcon-chat-home");
    }

    private <T> ResponseEntity<T> post(Map<String, Object> payload, String ip, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Forwarded-For", ip);
        return restTemplate.postForEntity("/api/v1/site/chat", new HttpEntity<>(payload, headers), responseType);
    }

    private String uniqueIp() {
        return "test-" + UUID.randomUUID();
    }
}
