package br.com.trcon.site.chat;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.trcon.site.chat.integration.DeepSeekChatClient;
import br.com.trcon.site.chat.service.ChatRateLimiter;
import br.com.trcon.site.shared.config.ChatAiProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

class ChatWiringTest {
    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(ChatBeans.class)
            .withPropertyValues(
                    "trcon.site.chat.ai.enabled=true",
                    "trcon.site.chat.ai.stub-enabled=true",
                    "trcon.site.chat.ai.base-url=https://api.deepseek.com",
                    "trcon.site.chat.ai.model=deepseek-flash",
                    "trcon.site.chat.ai.max-output-tokens=400",
                    "trcon.site.chat.ai.max-history-turns=6",
                    "trcon.site.chat.ai.rate-limit-per-minute=8",
                    "trcon.site.chat.ai.monthly-budget-usd=10",
                    "trcon.site.chat.ai.input-cost-per-1m-usd=0.30",
                    "trcon.site.chat.ai.output-cost-per-1m-usd=1.20");

    @Test
    void springInjetaConstrutoresDeProducao() {
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(ChatRateLimiter.class);
            assertThat(context).hasSingleBean(DeepSeekChatClient.class);
        });
    }

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(ChatAiProperties.class)
    @Import({ChatRateLimiter.class, DeepSeekChatClient.class})
    static class ChatBeans {}
}
