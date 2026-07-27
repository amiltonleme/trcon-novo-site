package br.com.trcon.site.news.controller;

import br.com.trcon.site.news.service.NewsFeedService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NewsFeedController {

    private final NewsFeedService newsFeedService;

    public NewsFeedController(NewsFeedService newsFeedService) {
        this.newsFeedService = newsFeedService;
    }

    @GetMapping(value = "/feed/news.xml", produces = MediaType.APPLICATION_RSS_XML_VALUE)
    public ResponseEntity<String> rss() {
        return ResponseEntity.ok(newsFeedService.gerarRss());
    }

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> sitemap() {
        return ResponseEntity.ok(newsFeedService.gerarSitemap());
    }
}
