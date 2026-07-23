package br.com.trcon.site.internal.news.controller;

import br.com.trcon.site.internal.news.dto.InternalNewsCreateRequest;
import br.com.trcon.site.internal.news.dto.InternalNewsCreateResponse;
import br.com.trcon.site.internal.news.service.InternalNewsService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/news")
public class InternalNewsController {

    private final InternalNewsService internalNewsService;

    public InternalNewsController(InternalNewsService internalNewsService) {
        this.internalNewsService = internalNewsService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InternalNewsCreateResponse criar(@Valid @RequestBody InternalNewsCreateRequest request) {
        return internalNewsService.criar(request);
    }
}
