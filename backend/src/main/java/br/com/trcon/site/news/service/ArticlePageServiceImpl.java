package br.com.trcon.site.news.service;

import br.com.trcon.site.news.dto.response.NewsArticleResponse;
import br.com.trcon.site.news.util.ArticlePageHtmlBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ArticlePageServiceImpl implements ArticlePageService {

    private final NewsService newsService;
    private final String publicBaseUrl;

    public ArticlePageServiceImpl(
            NewsService newsService,
            @Value("${trcon.site.public-base-url:https://trcongroup.com.br}") String publicBaseUrl) {
        this.newsService = newsService;
        this.publicBaseUrl = publicBaseUrl.replaceAll("/+$", "");
    }

    @Override
    public String renderHtml(String slug) {
        NewsArticleResponse article = newsService.buscarPorSlug(slug);
        return ArticlePageHtmlBuilder.build(article, publicBaseUrl);
    }

    @Override
    public String notFoundHtml() {
        return ArticlePageHtmlBuilder.notFound(publicBaseUrl);
    }
}
