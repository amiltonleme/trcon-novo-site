package br.com.trcon.site.highlights.domain;

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
@Table(name = "daily_highlights")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyHighlight {

    @Id
    private UUID id;

    @Column(nullable = false, length = 80)
    private String category;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 600)
    private String summary;

    @Column(length = 300)
    private String link;

    @Column(nullable = false)
    private int priority;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "published_at", nullable = false)
    private Instant publishedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    private DailyHighlight(UUID id, String category, String title, String summary, String link,
                            int priority, boolean active, Instant publishedAt, Instant now) {
        this.id = id;
        this.category = category;
        this.title = title;
        this.summary = summary;
        this.link = link;
        this.priority = priority;
        this.active = active;
        this.publishedAt = publishedAt;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public static DailyHighlight novo(String category, String title, String summary, String link,
                                       int priority, boolean active, Instant publishedAt) {
        Instant now = Instant.now();
        return new DailyHighlight(UUID.randomUUID(), category, title, summary, link, priority, active,
                publishedAt, now);
    }
}
