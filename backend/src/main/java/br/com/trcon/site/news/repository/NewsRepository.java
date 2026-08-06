package br.com.trcon.site.news.repository;

import br.com.trcon.site.news.domain.NewsItem;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NewsRepository extends JpaRepository<NewsItem, UUID> {

    @Query("""
            SELECT n FROM NewsItem n
            WHERE n.category = :category
              AND (n.expiresAt IS NULL OR n.expiresAt > :now)
            ORDER BY n.publishedAt DESC
            """)
    List<NewsItem> findActiveByCategory(
            @Param("category") String category, @Param("now") Instant now, Limit limit);

    @Query("""
            SELECT n FROM NewsItem n
            WHERE n.category <> :excludedCategory
              AND (n.expiresAt IS NULL OR n.expiresAt > :now)
            ORDER BY n.publishedAt DESC
            """)
    List<NewsItem> findActiveExcludingCategory(
            @Param("excludedCategory") String excludedCategory, @Param("now") Instant now, Limit limit);

    Optional<NewsItem> findByExternalId(String externalId);

    Optional<NewsItem> findBySlug(String slug);

    @Query("""
            SELECT n FROM NewsItem n
            WHERE n.slug IS NOT NULL
              AND n.category <> :excludedCategory
              AND (n.expiresAt IS NULL OR n.expiresAt > :now)
            ORDER BY n.publishedAt DESC
            """)
    List<NewsItem> findActiveWithSlugExcludingCategory(
            @Param("excludedCategory") String excludedCategory, @Param("now") Instant now, Limit limit);

    @Query("""
            SELECT COUNT(n) FROM NewsItem n
            WHERE n.expiresAt IS NOT NULL AND n.expiresAt <= :now
            """)
    long countExpired(@Param("now") Instant now);
}
