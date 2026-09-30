package br.com.trcon.site.chat.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class ChatSystemPromptProvider {
    private final String prompt;
    public ChatSystemPromptProvider(ChatKnowledgeProvider knowledge) {
        try {
            String rules = new ClassPathResource("chat/system-prompt-pt-br.txt").getContentAsString(StandardCharsets.UTF_8);
            prompt = rules + "\n\nBASE INSTITUCIONAL AUTORIZADA:\n" + knowledge.content();
        } catch (IOException ex) { throw new IllegalStateException("Prompt do chat ausente", ex); }
    }
    public String content() { return prompt; }
}
