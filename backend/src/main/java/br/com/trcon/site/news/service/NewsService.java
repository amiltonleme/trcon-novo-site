package br.com.trcon.site.news.service;

import br.com.trcon.site.news.dto.response.NewsArticleResponse;
import br.com.trcon.site.news.dto.response.NewsListResponse;

public interface NewsService {

    NewsListResponse listar(String category, Integer limit);

    NewsArticleResponse buscarPorSlug(String slug);

    NewsListResponse listarComSlug(Integer limit);
}
