package br.com.trcon.site.chat.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.trcon.site.chat.exception.ChatRateLimitedException;
import br.com.trcon.site.shared.config.ChatAiProperties;
import org.junit.jupiter.api.Test;

class ChatRateLimiterTest {
    @Test
    void bloqueiaQuandoExcedeLimitePorCliente() {
        ChatAiProperties properties = new ChatAiProperties(true, true, "", "https://api.deepseek.com",
                "deepseek-chat", 400, 6, 2, 10, 0.14, 0.28);
        ChatRateLimiter limiter = new ChatRateLimiter(properties);
        limiter.check("ip"); limiter.check("ip");
        assertThatThrownBy(() -> limiter.check("ip")).isInstanceOf(ChatRateLimitedException.class);
    }
}
