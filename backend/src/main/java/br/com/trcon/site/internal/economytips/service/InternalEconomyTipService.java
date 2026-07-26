package br.com.trcon.site.internal.economytips.service;

import br.com.trcon.site.economytips.domain.EconomyTip;
import br.com.trcon.site.economytips.repository.EconomyTipRepository;
import br.com.trcon.site.internal.economytips.dto.InternalEconomyTipCreateRequest;
import br.com.trcon.site.internal.economytips.dto.InternalEconomyTipCreateResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InternalEconomyTipService {

    private static final int DEFAULT_PRIORITY = 10;

    private final EconomyTipRepository economyTipRepository;

    public InternalEconomyTipService(EconomyTipRepository economyTipRepository) {
        this.economyTipRepository = economyTipRepository;
    }

    @Transactional
    public InternalEconomyTipCreateResponse criar(InternalEconomyTipCreateRequest request) {
        return economyTipRepository
                .findByExternalId(request.externalId().trim())
                .map(existing -> new InternalEconomyTipCreateResponse(existing.getId(), true))
                .orElseGet(() -> {
                    int priority = request.priority() != null ? request.priority() : DEFAULT_PRIORITY;
                    boolean featured = Boolean.TRUE.equals(request.featured());
                    String url = request.url() == null || request.url().isBlank() ? null : request.url().trim();
                    String linkLabel =
                            request.linkLabel() == null || request.linkLabel().isBlank()
                                    ? "Ler mais"
                                    : request.linkLabel().trim();
                    String source =
                            request.source() == null || request.source().isBlank()
                                    ? "Sirius Marketing"
                                    : request.source().trim();
                    EconomyTip tip = EconomyTip.fromMarketing(
                            request.tag().trim(),
                            request.tagClass().trim(),
                            request.title().trim(),
                            request.body().trim(),
                            url,
                            linkLabel,
                            featured,
                            priority,
                            request.publishedAt(),
                            request.externalId().trim(),
                            request.brandSlug() != null ? request.brandSlug().trim() : null,
                            source);
                    EconomyTip saved = economyTipRepository.save(tip);
                    return new InternalEconomyTipCreateResponse(saved.getId(), false);
                });
    }
}
