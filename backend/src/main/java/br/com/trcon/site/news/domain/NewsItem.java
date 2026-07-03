package br.com.trcon.site.news.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "news_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NewsItem {

    @Id
    private UUID id;

    @Column(nullable = false, length = 120)
    private String source;

    @Column(nullable = false, length = 80)
    private String category;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 600)
    private String summary;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(name = "published_at", nullable = false)
    private Instant publishedAt;

    @Column(name = "ingestion_batch", length = 80)
    private String ingestionBatch;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    private NewsItem(UUID id, String source, String category, String title, String summary, String url,
                      Instant publishedAt, String ingestionBatch, Instant createdAt) {
        this.id = id;
        this.source = source;
        this.category = category;
        this.title = title;
        this.summary = summary;
        this.url = url;
        this.publishedAt = publishedAt;
        this.ingestionBatch = ingestionBatch;
        this.createdAt = createdAt;
    }

    public static NewsItem novo(String source, String category, String title, String summary, String url,
                                 Instant publishedAt, String ingestionBatch) {
        return new NewsItem(UUID.randomUUID(), source, category, title, summary, url, publishedAt,
                ingestionBatch, Instant.now());
    }
}
