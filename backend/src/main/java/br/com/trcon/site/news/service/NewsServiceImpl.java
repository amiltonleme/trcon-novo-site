package br.com.trcon.site.news.service;

import br.com.trcon.site.news.domain.NewsItem;
import br.com.trcon.site.news.domain.NewsQueryInvalidaException;
import br.com.trcon.site.news.dto.response.NewsArticleResponse;
import br.com.trcon.site.news.dto.response.NewsListResponse;
import br.com.trcon.site.news.mapper.NewsMapper;
import br.com.trcon.site.news.repository.NewsRepository;
import br.com.trcon.site.shared.exception.ResourceNotFoundException;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class NewsServiceImpl implements NewsService {

    private static final int DEFAULT_LIMIT = 20;
    private static final int MAX_LIMIT = 50;
    private static final int FEED_LIMIT = 200;
    private static final Set<String> ALLOWED_CATEGORIES = Set.of("IA", "Tecnologia", "Financas", "Mercado");

    private final NewsRepository newsRepository;
    private final NewsMapper newsMapper;

    public NewsServiceImpl(NewsRepository newsRepository, NewsMapper newsMapper) {
        this.newsRepository = newsRepository;
        this.newsMapper = newsMapper;
    }

    @Override
    public NewsListResponse listar(String category, Integer limit) {
        int limiteEfetivo = resolverLimite(limit);

        if (category != null && !category.isBlank()) {
            if (!ALLOWED_CATEGORIES.contains(category)) {
                throw new NewsQueryInvalidaException(
                        "category deve ser um dos valores suportados: " + ALLOWED_CATEGORIES);
            }
            List<NewsItem> itens = newsRepository.findByCategoryOrderByPublishedAtDesc(
                    category, Limit.of(limiteEfetivo));
            return newsMapper.toListResponse(itens);
        }

        List<NewsItem> itens = newsRepository.findAllByOrderByPublishedAtDesc(Limit.of(limiteEfetivo));
        return newsMapper.toListResponse(itens);
    }

    @Override
    public NewsArticleResponse buscarPorSlug(String slug) {
        if (slug == null || slug.isBlank()) {
            throw new NewsQueryInvalidaException("slug é obrigatório");
        }
        NewsItem item = newsRepository
                .findBySlug(slug.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Artigo não encontrado: " + slug));
        return newsMapper.toArticleResponse(item);
    }

    @Override
    public NewsListResponse listarComSlug(Integer limit) {
        int limiteEfetivo = limit == null ? FEED_LIMIT : Math.min(Math.max(limit, 1), FEED_LIMIT);
        List<NewsItem> itens = newsRepository.findBySlugIsNotNullOrderByPublishedAtDesc(Limit.of(limiteEfetivo));
        return newsMapper.toListResponse(itens);
    }

    private int resolverLimite(Integer limit) {
        if (limit == null) {
            return DEFAULT_LIMIT;
        }
        if (limit < 1 || limit > MAX_LIMIT) {
            throw new NewsQueryInvalidaException("limit deve estar entre 1 e " + MAX_LIMIT);
        }
        return limit;
    }
}
