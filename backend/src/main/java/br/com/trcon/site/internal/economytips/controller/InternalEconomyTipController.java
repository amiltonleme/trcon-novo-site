package br.com.trcon.site.internal.economytips.controller;

import br.com.trcon.site.internal.economytips.dto.InternalEconomyTipCreateRequest;
import br.com.trcon.site.internal.economytips.dto.InternalEconomyTipCreateResponse;
import br.com.trcon.site.internal.economytips.service.InternalEconomyTipService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/economy-tips")
public class InternalEconomyTipController {

    private final InternalEconomyTipService internalEconomyTipService;

    public InternalEconomyTipController(InternalEconomyTipService internalEconomyTipService) {
        this.internalEconomyTipService = internalEconomyTipService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InternalEconomyTipCreateResponse criar(@Valid @RequestBody InternalEconomyTipCreateRequest request) {
        return internalEconomyTipService.criar(request);
    }
}
