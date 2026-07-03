package br.com.trcon.site.news.controller;

import br.com.trcon.site.news.dto.response.NewsListResponse;
import br.com.trcon.site.news.service.NewsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/news")
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    public NewsListResponse listar(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer limit) {
        return newsService.listar(category, limit);
    }
}
