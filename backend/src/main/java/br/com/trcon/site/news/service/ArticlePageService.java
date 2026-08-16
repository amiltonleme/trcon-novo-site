package br.com.trcon.site.news.service;

public interface ArticlePageService {

    String renderHtml(String slug);

    String notFoundHtml();
}
