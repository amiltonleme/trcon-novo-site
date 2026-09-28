package br.com.trcon.site.chat.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class ChatKnowledgeProvider {
    private static final Pattern ID_LINE = Pattern.compile("^\\s*- id: ([a-z0-9._-]+)\\s*$");
    private static final Set<String> REQUIRED_IDS = Set.of(
            "company.identity", "company.current_focus", "business.offerings", "business.delivery",
            "products.hub", "products.scheduling", "products.marketing", "commercial.cases",
            "commercial.status", "careers.status", "careers.areas", "governance.missing");
    private final String knowledge;
    private final Set<String> sourceIds;
    public ChatKnowledgeProvider() {
        this.knowledge = read("chat/trcon-knowledge.yml");
        List<String> parsedIds = knowledge.lines().map(ID_LINE::matcher).filter(m -> m.matches())
                .map(m -> m.group(1)).toList();
        this.sourceIds = parsedIds.stream().collect(Collectors.toUnmodifiableSet());
        if (sourceIds.size() != parsedIds.size())
            throw new IllegalStateException("Base institucional contém sourceIds duplicados");
        Set<String> missingIds = new HashSet<>(REQUIRED_IDS);
        missingIds.removeAll(sourceIds);
        if (!missingIds.isEmpty())
            throw new IllegalStateException("Base institucional incompleta; IDs ausentes: " + missingIds);
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
