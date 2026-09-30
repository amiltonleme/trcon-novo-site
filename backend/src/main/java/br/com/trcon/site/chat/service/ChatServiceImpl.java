package br.com.trcon.site.chat.service;

import br.com.trcon.site.chat.dto.ChatMessageDto;
import br.com.trcon.site.chat.dto.ChatRequest;
import br.com.trcon.site.chat.dto.ChatResponse;
import br.com.trcon.site.chat.exception.ChatProviderUnavailableException;
import br.com.trcon.site.chat.integration.DeepSeekChatClient;
import br.com.trcon.site.chat.integration.DeepSeekChatRequest;
import br.com.trcon.site.chat.integration.DeepSeekChatResponse;
import br.com.trcon.site.shared.config.ChatAiProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ChatServiceImpl implements ChatService {
    private static final Logger LOG = LoggerFactory.getLogger(ChatServiceImpl.class);
    private static final String DISCLAIMER = "Resposta gerada por IA. Para uma proposta, fale com nosso time.";
    private final ChatAiProperties properties;
    private final ChatRateLimiter rateLimiter;
    private final ChatQuotaService quota;
    private final ChatSystemPromptProvider prompt;
    private final ChatKnowledgeProvider knowledge;
    private final DeepSeekChatClient client;
    private final ObjectMapper objectMapper;

    public ChatServiceImpl(ChatAiProperties properties, ChatRateLimiter rateLimiter, ChatQuotaService quota,
                           ChatSystemPromptProvider prompt, ChatKnowledgeProvider knowledge,
                           DeepSeekChatClient client, ObjectMapper objectMapper) {
        this.properties = properties; this.rateLimiter = rateLimiter; this.quota = quota;
        this.prompt = prompt; this.knowledge = knowledge; this.client = client; this.objectMapper = objectMapper;
    }

    @Override
    public ChatResponse reply(ChatRequest request, String clientKey) {
        rateLimiter.check(clientKey);
        if (!properties.enabled()) throw new ChatProviderUnavailableException();
        if (properties.stubEnabled()) return groundedStub(request.message());
        if (!properties.providerConfigured()) throw new ChatProviderUnavailableException();
        quota.assertWithinBudget();

        List<DeepSeekChatRequest.Message> messages = new ArrayList<>();
        messages.add(new DeepSeekChatRequest.Message("system", prompt.content()));
        List<ChatMessageDto> history = request.history() == null ? List.of() : request.history();
        int maxMessages = Math.max(0, properties.maxHistoryTurns() * 2);
        history.stream().skip(Math.max(0, history.size() - maxMessages))
                .forEach(item -> messages.add(new DeepSeekChatRequest.Message(item.role(), item.content().trim())));
        messages.add(new DeepSeekChatRequest.Message("user", request.message().trim()));

        DeepSeekChatResponse provider = client.complete(new DeepSeekChatRequest(
                properties.model(), messages, 0.1, properties.maxOutputTokens(), Map.of("type", "json_object")));
        DeepSeekChatResponse.Usage usage = provider.usage();
        if (usage != null) quota.log(usage.promptTokens(), usage.completionTokens(), hash(clientKey));
        try {
            DeepSeekChatResponse.Choice choice = provider.choices().getFirst();
            if (choice.message() == null || (choice.finishReason() != null && !"stop".equals(choice.finishReason()))) {
                LOG.warn("Resposta do provedor descartada; finishReason={}", choice.finishReason());
                return missingKnowledge();
            }
            ModelAnswer answer = objectMapper.readValue(choice.message().content(), ModelAnswer.class);
            List<String> sourceIds = answer.sourceIds() == null ? List.of() : List.copyOf(answer.sourceIds());
            if (!isValid(answer, sourceIds)) {
                LOG.warn("Resposta do provedor violou o contrato; sourceIds={}, outOfScope={}, knowledgeMissing={}, generalTechnology={}, contact={}, careers={}",
                        sourceIds, answer.outOfScope(), answer.knowledgeMissing(), answer.generalTechnology(),
                        answer.suggestContactForm(), answer.suggestCareersPage());
                return missingKnowledge();
            }
            return new ChatResponse(answer.reply().trim(), sourceIds, DISCLAIMER, answer.outOfScope(),
                    answer.knowledgeMissing(), answer.suggestContactForm(), answer.suggestCareersPage());
        } catch (Exception ex) {
            LOG.warn("Falha ao interpretar a resposta estruturada do provedor do chat", ex);
            return missingKnowledge();
        }
    }

    private boolean isValid(ModelAnswer answer, List<String> sourceIds) {
        if (answer.reply() == null || answer.reply().isBlank()
                || sourceIds.size() != Set.copyOf(sourceIds).size()
                || !knowledge.containsAll(sourceIds)
                || (answer.outOfScope() && answer.knowledgeMissing())
                || (answer.outOfScope() && answer.generalTechnology())
                || (answer.knowledgeMissing() && answer.generalTechnology())
                || (answer.suggestContactForm() && answer.suggestCareersPage())) return false;
        if (answer.outOfScope())
            return sourceIds.isEmpty() && !answer.suggestContactForm() && !answer.suggestCareersPage();
        if (answer.knowledgeMissing())
            return sourceIds.contains("governance.missing") && answer.suggestContactForm();
        if (answer.generalTechnology())
            return !answer.suggestCareersPage();
        if (sourceIds.isEmpty()) return false;
        return !answer.suggestCareersPage() || sourceIds.stream().allMatch(id -> id.startsWith("careers."));
    }

    private ChatResponse groundedStub(String message) {
        String normalized = normalize(message);
        if (contains(normalized, "cliente", "case", "referência", "referencia"))
            return response("Ainda não há cases de clientes publicados na base institucional da TRCONGROUP. Posso apresentar nossas capacidades, produtos e formas de contratação.", "commercial.cases", false, false, true, false);
        if (contains(normalized, "contrato"))
            return response("A base institucional registra que a TRCONGROUP não possui contratos ativos no momento. Posso explicar nossas capacidades e direcionar você para uma conversa comercial.", "commercial.status", false, false, true, false);
        if (contains(normalized, "vaga", "carreira", "trabalhe", "emprego"))
            return response("No momento, não há vagas publicadas nem banco de talentos disponível. As próximas oportunidades serão divulgadas na página Trabalhe Conosco e no LinkedIn da TRCONGROUP.", "careers.status", false, false, false, true);
        if (contains(normalized, "produto", "hub", "agendamento", "marketing"))
            return response("Os produtos próprios publicados são o Sírius Hub de Inteligência Financeira, em beta, o Sírius Agendamento e o Sírius Marketing, ambos em desenvolvimento.", false, false, true, false,
                    "products.hub", "products.scheduling", "products.marketing");
        if (contains(normalized, "21", "história", "historia", "quem é", "quem e"))
            return response("A TRCONGROUP é uma empresa de tecnologia com 21 anos de existência. Atua com inteligência artificial, desenvolvimento sob demanda, modernização, produtos digitais e outsourcing.", false, false, false, false,
                    "company.identity", "company.current_focus");
        if (contains(normalized, "ia", "software", "desenvolvimento", "outsourcing", "serviço", "servico"))
            return response("A TRCONGROUP desenvolve software sob demanda, aplica IA, moderniza sistemas e oferece squads ou profissionais especializados. O formato é definido a partir do desafio e do contexto da empresa.", false, false, true, false,
                    "company.current_focus", "business.offerings");
        return new ChatResponse("Posso responder apenas sobre a TRCONGROUP, suas soluções, produtos, forma de trabalho e oportunidades.", List.of(), DISCLAIMER, true, false, false, false);
    }
    private static String normalize(String value) {
        String decomposed = Normalizer.normalize(value.toLowerCase(Locale.ROOT), Normalizer.Form.NFD);
        return decomposed.replaceAll("\\p{M}+", "");
    }
    private static boolean contains(String value, String... terms) { for (String term : terms) if (value.contains(term)) return true; return false; }
    private static ChatResponse response(String text, String sourceId, boolean out, boolean missing, boolean contact, boolean careers) {
        return new ChatResponse(text, List.of(sourceId), DISCLAIMER, out, missing, contact, careers);
    }
    private static ChatResponse response(String text, boolean out, boolean missing, boolean contact, boolean careers,
                                         String... sourceIds) {
        return new ChatResponse(text, List.of(sourceIds), DISCLAIMER, out, missing, contact, careers);
    }
    private static ChatResponse missingKnowledge() {
        return new ChatResponse("Não tenho essa informação na base institucional da TRCONGROUP. Posso direcionar você para o formulário de contato.", List.of("governance.missing"), DISCLAIMER, false, true, true, false);
    }
    private static String hash(String value) {
        try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8))); }
        catch (NoSuchAlgorithmException ex) { throw new IllegalStateException(ex); }
    }
    private record ModelAnswer(String reply, List<String> sourceIds, boolean outOfScope,
                               boolean knowledgeMissing, boolean generalTechnology, boolean suggestContactForm,
                               boolean suggestCareersPage) {}
}
