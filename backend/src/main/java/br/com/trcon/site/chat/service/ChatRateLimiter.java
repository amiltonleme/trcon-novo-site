package br.com.trcon.site.chat.service;

import br.com.trcon.site.chat.exception.ChatRateLimitedException;
import br.com.trcon.site.shared.config.ChatAiProperties;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChatRateLimiter {
    private final ConcurrentHashMap<String, Deque<Instant>> requests = new ConcurrentHashMap<>();
    private final ChatAiProperties properties;
    private final Clock clock;

    @Autowired
    public ChatRateLimiter(ChatAiProperties properties) { this(properties, Clock.systemUTC()); }
    ChatRateLimiter(ChatAiProperties properties, Clock clock) { this.properties = properties; this.clock = clock; }

    public void check(String clientKey) {
        Instant cutoff = clock.instant().minusSeconds(60);
        Deque<Instant> bucket = requests.computeIfAbsent(clientKey, ignored -> new ArrayDeque<>());
        synchronized (bucket) {
            while (!bucket.isEmpty() && bucket.peekFirst().isBefore(cutoff)) bucket.removeFirst();
            if (bucket.size() >= Math.max(1, properties.rateLimitPerMinute())) throw new ChatRateLimitedException();
            bucket.addLast(clock.instant());
        }
    }
}
