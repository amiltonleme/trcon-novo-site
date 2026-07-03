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

    private final HighlightRepository highlightRepository;
    private final HighlightMapper highlightMapper;

    public HighlightServiceImpl(HighlightRepository highlightRepository, HighlightMapper highlightMapper) {
        this.highlightRepository = highlightRepository;
        this.highlightMapper = highlightMapper;
    }

    @Override
    public HighlightListResponse listarAtivos() {
        List<DailyHighlight> ativos =
                highlightRepository.findByActiveTrueOrderByPriorityAscPublishedAtDesc(Limit.of(MAX_ITEMS));
        return highlightMapper.toListResponse(ativos);
    }
}
