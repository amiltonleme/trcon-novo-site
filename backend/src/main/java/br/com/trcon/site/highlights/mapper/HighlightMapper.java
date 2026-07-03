package br.com.trcon.site.highlights.mapper;

import br.com.trcon.site.highlights.domain.DailyHighlight;
import br.com.trcon.site.highlights.dto.response.HighlightListResponse;
import br.com.trcon.site.highlights.dto.response.HighlightResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HighlightMapper {

    HighlightResponse toResponse(DailyHighlight highlight);

    List<HighlightResponse> toResponseList(List<DailyHighlight> highlights);

    default HighlightListResponse toListResponse(List<DailyHighlight> highlights) {
        return HighlightListResponse.of(toResponseList(highlights));
    }
}
