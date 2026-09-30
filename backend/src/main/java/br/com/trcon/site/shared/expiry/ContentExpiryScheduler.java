package br.com.trcon.site.shared.expiry;

import br.com.trcon.site.news.repository.NewsRepository;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Artigos usam filtro por {@code expires_at} nas APIs; o job registra a contagem. */
@Component
public class ContentExpiryScheduler {

    private static final Logger log = LoggerFactory.getLogger(ContentExpiryScheduler.class);

    private final NewsRepository newsRepository;

    public ContentExpiryScheduler(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    @Scheduled(cron = "${trcon.site.content.expiry-cron:0 15 3 * * *}")
    @Transactional
    public void expireDueContent() {
        Instant now = Instant.now();
        long newsExpired = newsRepository.countExpired(now);
        if (newsExpired > 0) {
            log.info("Content expiry: newsPastExpiresAt={}", newsExpired);
        }
    }
}
