package br.com.trcon.site.economytips.controller;

import br.com.trcon.site.economytips.dto.response.EconomyTipListResponse;
import br.com.trcon.site.economytips.service.EconomyTipService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/economy-tips")
public class EconomyTipController {

    private final EconomyTipService economyTipService;

    public EconomyTipController(EconomyTipService economyTipService) {
        this.economyTipService = economyTipService;
    }

    @GetMapping
    public EconomyTipListResponse listar() {
        return economyTipService.listarAtivos();
    }
}
