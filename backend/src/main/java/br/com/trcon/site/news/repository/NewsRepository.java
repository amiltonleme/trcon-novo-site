package br.com.trcon.site.news.repository;

import br.com.trcon.site.news.domain.NewsItem;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NewsRepository extends JpaRepository<NewsItem, UUID> {

    List<NewsItem> findByCategoryOrderByPublishedAtDesc(String category, Limit limit);

    List<NewsItem> findAllByOrderByPublishedAtDesc(Limit limit);
}
