package br.com.trcon.site.chat.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class ChatKnowledgeProvider {
    private static final Pattern ID_LINE = Pattern.compile("^\\s*- id: ([a-z0-9._-]+)\\s*$");
    private final String knowledge;
    private final Set<String> sourceIds;
    public ChatKnowledgeProvider() {
        this.knowledge = read("chat/trcon-knowledge.yml");
        this.sourceIds = knowledge.lines().map(ID_LINE::matcher).filter(m -> m.matches())
                .map(m -> m.group(1)).collect(Collectors.toUnmodifiableSet());
        if (sourceIds.isEmpty()) throw new IllegalStateException("Base institucional sem sourceIds");
    }
    public String content() { return knowledge; }
    public boolean containsAll(Iterable<String> ids) {
        for (String id : ids) if (!sourceIds.contains(id)) return false;
        return true;
    }
    private static String read(String path) {
        try { return new ClassPathResource(path).getContentAsString(StandardCharsets.UTF_8); }
        catch (IOException ex) { throw new IllegalStateException("Recurso de chat ausente: " + path, ex); }
    }
}
