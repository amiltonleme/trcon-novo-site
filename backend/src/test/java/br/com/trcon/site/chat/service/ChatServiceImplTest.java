package br.com.trcon.site.chat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import br.com.trcon.site.chat.dto.ChatRequest;
import br.com.trcon.site.chat.dto.ChatResponse;
import br.com.trcon.site.chat.integration.DeepSeekChatClient;
import br.com.trcon.site.shared.config.ChatAiProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;

class ChatServiceImplTest {
    private final ChatAiProperties properties = new ChatAiProperties(
            true, true, "", "https://api.deepseek.com", "deepseek-chat",
            400, 6, 8, 10, 0.14, 0.28);
    private final ChatKnowledgeProvider knowledge = new ChatKnowledgeProvider();
    private final ChatService service = new ChatServiceImpl(
            properties, new ChatRateLimiter(properties), mock(ChatQuotaService.class),
            new ChatSystemPromptProvider(knowledge), knowledge, mock(DeepSeekChatClient.class), new ObjectMapper());

    @Test
    void informaIdentidadeEFocoAtual() {
        ChatResponse response = service.reply(new ChatRequest("Quem é a empresa?", List.of(), "site"), "ip-1");
        assertThat(response.reply()).contains("21 anos", "inteligência artificial", "outsourcing");
        assertThat(response.sourceIds()).containsExactly("company.identity", "company.current_focus");
    }

    @Test
    void naoInventaCasesNemConfidencialidade() {
        ChatResponse response = service.reply(new ChatRequest("Quais clientes e cases vocês têm?", List.of(), "site"), "ip-2");
        assertThat(response.reply()).contains("não há cases de clientes publicados");
        assertThat(response.reply()).doesNotContain("confidencial", "sigilo");
        assertThat(response.suggestContactForm()).isTrue();
    }

    @Test
    void refleteEstadoRealDeVagas() {
        ChatResponse response = service.reply(new ChatRequest("Tem vaga aberta?", List.of(), "site"), "ip-3");
        assertThat(response.reply()).contains("não há vagas publicadas", "banco de talentos");
        assertThat(response.suggestCareersPage()).isTrue();
    }

    @Test
    void refleteEstadosReaisDosProdutosSemPrometerCondicoesComerciais() {
        ChatResponse response = service.reply(new ChatRequest("Quais produtos vocês têm?", List.of(), "site"), "ip-4");
        assertThat(response.reply()).contains("Sírius Hub", "em beta", "Sírius Agendamento", "Sírius Marketing", "em desenvolvimento");
        assertThat(response.reply()).doesNotContain("grátis", "90 dias", "desconto");
        assertThat(response.sourceIds()).containsExactly("products.hub", "products.scheduling", "products.marketing");
    }
}
