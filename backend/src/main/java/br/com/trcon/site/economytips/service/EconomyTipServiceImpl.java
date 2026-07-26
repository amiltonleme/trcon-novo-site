package br.com.trcon.site.economytips.service;

import br.com.trcon.site.economytips.domain.EconomyTip;
import br.com.trcon.site.economytips.dto.response.EconomyTipListResponse;
import br.com.trcon.site.economytips.mapper.EconomyTipMapper;
import br.com.trcon.site.economytips.repository.EconomyTipRepository;
import java.util.List;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;

@Service
public class EconomyTipServiceImpl implements EconomyTipService {

    private static final int MAX_ITEMS = 8;

    private final EconomyTipRepository economyTipRepository;
    private final EconomyTipMapper economyTipMapper;

    public EconomyTipServiceImpl(EconomyTipRepository economyTipRepository, EconomyTipMapper economyTipMapper) {
        this.economyTipRepository = economyTipRepository;
        this.economyTipMapper = economyTipMapper;
    }

    @Override
    public EconomyTipListResponse listarAtivos() {
        List<EconomyTip> ativos =
                economyTipRepository.findByActiveTrueOrderByPriorityAscPublishedAtDesc(Limit.of(MAX_ITEMS));
        return economyTipMapper.toListResponse(ativos);
    }
}
