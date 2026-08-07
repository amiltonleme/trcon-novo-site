package br.com.trcon.site.shared.expiry;

import br.com.trcon.site.economytips.repository.EconomyTipRepository;
import br.com.trcon.site.news.repository.NewsRepository;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Soft-hide: desativa tips expirados ({@code active=false}).
 * Artigos usam filtro por {@code expires_at} nas APIs — job só registra contagem.
 */
@Component
public class ContentExpiryScheduler {

    private static final Logger log = LoggerFactory.getLogger(ContentExpiryScheduler.class);

    private final EconomyTipRepository economyTipRepository;
    private final NewsRepository newsRepository;

    public ContentExpiryScheduler(EconomyTipRepository economyTipRepository, NewsRepository newsRepository) {
        this.economyTipRepository = economyTipRepository;
        this.newsRepository = newsRepository;
    }

    @Scheduled(cron = "${trcon.site.content.expiry-cron:0 15 3 * * *}")
    @Transactional
    public void expireDueContent() {
        Instant now = Instant.now();
        int tips = economyTipRepository.deactivateExpired(now);
        long newsExpired = newsRepository.countExpired(now);
        if (tips > 0 || newsExpired > 0) {
            log.info("Content expiry: deactivatedTips={}, newsPastExpiresAt={}", tips, newsExpired);
        }
    }
}
