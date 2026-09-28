package br.com.trcon.site.shared.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "trcon.site.chat.ai")
public record ChatAiProperties(
        boolean enabled, boolean stubEnabled, String apiKey, String baseUrl, String model,
        int maxOutputTokens, int maxHistoryTurns, int rateLimitPerMinute,
        double monthlyBudgetUsd, double inputCostPer1mUsd, double outputCostPer1mUsd) {
    public boolean providerConfigured() {
        return enabled && apiKey != null && !apiKey.isBlank();
    }
}
