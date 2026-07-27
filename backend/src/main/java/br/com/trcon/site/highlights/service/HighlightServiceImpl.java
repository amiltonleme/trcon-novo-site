package br.com.trcon.site.highlights.service;

import br.com.trcon.site.highlights.domain.DailyHighlight;
import br.com.trcon.site.highlights.dto.response.HighlightListResponse;
import br.com.trcon.site.highlights.mapper.HighlightMapper;
import br.com.trcon.site.highlights.repository.HighlightRepository;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HighlightServiceImpl implements HighlightService {

    private static final int MAX_ITEMS = 6;
    private static final int SCAN_LIMIT = 50;

    private final HighlightRepository highlightRepository;
    private final HighlightMapper highlightMapper;

    public HighlightServiceImpl(HighlightRepository highlightRepository, HighlightMapper highlightMapper) {
        this.highlightRepository = highlightRepository;
        this.highlightMapper = highlightMapper;
    }

    @Override
    public HighlightListResponse listarAtivos() {
        List<DailyHighlight> candidatos =
                highlightRepository.findByActiveTrueOrderByPriorityAscPublishedAtDesc(Limit.of(SCAN_LIMIT));
        List<DailyHighlight> pipeline = candidatos.stream()
                .filter(this::isPipelineHighlight)
                .limit(MAX_ITEMS)
                .toList();
        return highlightMapper.toListResponse(pipeline);
    }

    /** Radar = sinais do pipeline; artigos editoriais ficam só em Novidades. */
    private boolean isPipelineHighlight(DailyHighlight highlight) {
        String externalId = highlight.getExternalId();
        if (externalId != null && externalId.endsWith("-radar")) {
            return false;
        }
        String link = highlight.getLink();
        return link == null || !link.contains("/novidades/");
    }
}
