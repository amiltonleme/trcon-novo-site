package br.com.trcon.site.news.controller;

import br.com.trcon.site.news.service.ArticlePageService;
import br.com.trcon.site.shared.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ArticlePageController {

    private final ArticlePageService articlePageService;

    public ArticlePageController(ArticlePageService articlePageService) {
        this.articlePageService = articlePageService;
    }

    @GetMapping(value = "/novidades/{slug}", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> page(@PathVariable String slug) {
        try {
            String html = articlePageService.renderHtml(slug);
            return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.TEXT_HTML)
                    .body(articlePageService.notFoundHtml());
        }
    }
}
