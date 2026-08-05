package br.com.trcon.site.news.repository;

import br.com.trcon.site.news.domain.NewsItem;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NewsRepository extends JpaRepository<NewsItem, UUID> {

    List<NewsItem> findByCategoryOrderByPublishedAtDesc(String category, Limit limit);

    List<NewsItem> findAllByOrderByPublishedAtDesc(Limit limit);

    /** Grid Novidades / feed RSS: exclui páginas de leitura de Educação Financeira. */
    List<NewsItem> findByCategoryNotOrderByPublishedAtDesc(String category, Limit limit);

    Optional<NewsItem> findByExternalId(String externalId);

    Optional<NewsItem> findBySlug(String slug);

    List<NewsItem> findBySlugIsNotNullOrderByPublishedAtDesc(Limit limit);

    List<NewsItem> findBySlugIsNotNullAndCategoryNotOrderByPublishedAtDesc(String category, Limit limit);
}
