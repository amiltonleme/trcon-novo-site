package br.com.trcon.site.economytips.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "economy_tips")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EconomyTip {

    @Id
    private UUID id;

    @Column(nullable = false, length = 40)
    private String tag;

    @Column(name = "tag_class", nullable = false, length = 20)
    private String tagClass;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 600)
    private String body;

    @Column(length = 500)
    private String url;

    @Column(name = "link_label", nullable = false, length = 40)
    private String linkLabel;

    @Column(nullable = false)
    private boolean featured;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private int priority;

    @Column(name = "published_at", nullable = false)
    private Instant publishedAt;

    @Column(name = "external_id", length = 120)
    private String externalId;

    @Column(name = "brand_slug", length = 80)
    private String brandSlug;

    @Column(length = 120)
    private String source;

    /** Null = permanente. */
    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    private EconomyTip(
            UUID id,
            String tag,
            String tagClass,
            String title,
            String body,
            String url,
            String linkLabel,
            boolean featured,
            boolean active,
            int priority,
            Instant publishedAt,
            String externalId,
            String brandSlug,
            String source,
            Instant expiresAt,
            Instant now) {
        this.id = id;
        this.tag = tag;
        this.tagClass = tagClass;
        this.title = title;
        this.body = body;
        this.url = url;
        this.linkLabel = linkLabel;
        this.featured = featured;
        this.active = active;
        this.priority = priority;
        this.publishedAt = publishedAt;
        this.externalId = externalId;
        this.brandSlug = brandSlug;
        this.source = source;
        this.expiresAt = expiresAt;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public static EconomyTip fromMarketing(
            String tag,
            String tagClass,
            String title,
            String body,
            String url,
            String linkLabel,
            boolean featured,
            int priority,
            Instant publishedAt,
            String externalId,
            String brandSlug,
            String source,
            Instant expiresAt) {
        Instant now = Instant.now();
        return new EconomyTip(
                UUID.randomUUID(),
                tag,
                tagClass,
                title,
                body,
                url,
                linkLabel,
                featured,
                true,
                priority,
                publishedAt,
                externalId,
                brandSlug,
                source,
                expiresAt,
                now);
    }

    public void updateFromMarketing(
            String tag,
            String tagClass,
            String title,
            String body,
            String url,
            String linkLabel,
            boolean featured,
            int priority,
            Instant publishedAt,
            String brandSlug,
            String source,
            Instant expiresAt) {
        this.tag = tag;
        this.tagClass = tagClass;
        this.title = title;
        this.body = body;
        this.url = url;
        this.linkLabel = linkLabel;
        this.featured = featured;
        this.active = true;
        this.priority = priority;
        this.publishedAt = publishedAt;
        this.brandSlug = brandSlug;
        this.source = source;
        this.expiresAt = expiresAt;
        this.updatedAt = Instant.now();
    }

    public void deactivate(Instant now) {
        this.active = false;
        this.updatedAt = now;
    }

    public boolean isVisibleAt(Instant now) {
        return active && (expiresAt == null || expiresAt.isAfter(now));
    }
}
