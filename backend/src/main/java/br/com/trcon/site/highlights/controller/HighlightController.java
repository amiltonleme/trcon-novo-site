package br.com.trcon.site.highlights.controller;

import br.com.trcon.site.highlights.dto.response.HighlightListResponse;
import br.com.trcon.site.highlights.service.HighlightService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/highlights")
public class HighlightController {

    private final HighlightService highlightService;

    public HighlightController(HighlightService highlightService) {
        this.highlightService = highlightService;
    }

    @GetMapping
    public HighlightListResponse listar() {
        return highlightService.listarAtivos();
    }
}
