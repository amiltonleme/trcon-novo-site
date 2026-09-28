package br.com.trcon.site.chat.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chat_usage_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatUsageLog {
    @Id private UUID id;
    @Column(nullable = false, length = 80) private String model;
    @Column(name = "prompt_tokens", nullable = false) private int promptTokens;
    @Column(name = "completion_tokens", nullable = false) private int completionTokens;
    @Column(name = "estimated_cost_usd", nullable = false, precision = 12, scale = 8) private BigDecimal estimatedCostUsd;
    @Column(name = "client_hash", nullable = false, length = 64) private String clientHash;
    @Column(name = "created_at", nullable = false) private Instant createdAt;

    public static ChatUsageLog of(String model, int promptTokens, int completionTokens,
                                  BigDecimal cost, String clientHash) {
        ChatUsageLog log = new ChatUsageLog();
        log.id = UUID.randomUUID(); log.model = model; log.promptTokens = promptTokens;
        log.completionTokens = completionTokens; log.estimatedCostUsd = cost;
        log.clientHash = clientHash; log.createdAt = Instant.now();
        return log;
    }
}
