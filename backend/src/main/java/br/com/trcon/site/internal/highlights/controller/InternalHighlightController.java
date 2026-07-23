package br.com.trcon.site.internal.highlights.controller;

import br.com.trcon.site.internal.highlights.dto.InternalHighlightCreateRequest;
import br.com.trcon.site.internal.highlights.dto.InternalHighlightCreateResponse;
import br.com.trcon.site.internal.highlights.service.InternalHighlightService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/highlights")
public class InternalHighlightController {

    private final InternalHighlightService internalHighlightService;

    public InternalHighlightController(InternalHighlightService internalHighlightService) {
        this.internalHighlightService = internalHighlightService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InternalHighlightCreateResponse criar(@Valid @RequestBody InternalHighlightCreateRequest request) {
        return internalHighlightService.criar(request);
    }
}
