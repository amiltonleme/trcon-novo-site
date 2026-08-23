package br.com.trcon.site.economytips.mapper;

import br.com.trcon.site.economytips.domain.EconomyTip;
import br.com.trcon.site.economytips.dto.response.EconomyTipListResponse;
import br.com.trcon.site.economytips.dto.response.EconomyTipResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EconomyTipMapper {

    @Mapping(target = "meta", expression = "java(buildMeta(tip))")
    EconomyTipResponse toResponse(EconomyTip tip);

    List<EconomyTipResponse> toResponseList(List<EconomyTip> tips);

    default EconomyTipListResponse toListResponse(List<EconomyTip> tips) {
        return EconomyTipListResponse.of(toResponseList(tips));
    }

    default List<String> buildMeta(EconomyTip tip) {
        String source = tip.getSource() != null && !tip.getSource().isBlank()
                ? tip.getSource()
                : "TRCONGROUP";
        return List.of("Leitura rapida", "Fonte: " + source);
    }
}
