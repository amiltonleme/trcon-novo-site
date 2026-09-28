package br.com.trcon.site.chat.service;

import br.com.trcon.site.chat.domain.ChatUsageLog;
import br.com.trcon.site.chat.exception.ChatBudgetExceededException;
import br.com.trcon.site.chat.repository.ChatUsageLogRepository;
import br.com.trcon.site.shared.config.ChatAiProperties;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneOffset;
import org.springframework.stereotype.Service;

@Service
public class ChatQuotaService {
    private final ChatUsageLogRepository repository;
    private final ChatAiProperties properties;
    public ChatQuotaService(ChatUsageLogRepository repository, ChatAiProperties properties) {
        this.repository = repository; this.properties = properties;
    }
    public void assertWithinBudget() {
        Instant start = Instant.now().atZone(ZoneOffset.UTC).withDayOfMonth(1).toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant();
        BigDecimal used = repository.sumCostSince(start);
        if (used != null && used.compareTo(BigDecimal.valueOf(properties.monthlyBudgetUsd())) >= 0) throw new ChatBudgetExceededException();
    }
    public void log(int promptTokens, int completionTokens, String clientHash) {
        double raw = promptTokens * properties.inputCostPer1mUsd() / 1_000_000d
                + completionTokens * properties.outputCostPer1mUsd() / 1_000_000d;
        repository.save(ChatUsageLog.of(properties.model(), promptTokens, completionTokens,
                BigDecimal.valueOf(raw).setScale(8, RoundingMode.HALF_UP), clientHash));
    }
}
