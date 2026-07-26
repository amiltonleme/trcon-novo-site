CREATE TABLE economy_tips (
    id UUID PRIMARY KEY,
    tag VARCHAR(40) NOT NULL,
    tag_class VARCHAR(20) NOT NULL,
    title VARCHAR(200) NOT NULL,
    body VARCHAR(600) NOT NULL,
    url VARCHAR(500),
    link_label VARCHAR(40) NOT NULL DEFAULT 'Ler mais',
    featured BOOLEAN NOT NULL DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    priority INT NOT NULL DEFAULT 50,
    published_at TIMESTAMPTZ NOT NULL,
    external_id VARCHAR(120),
    brand_slug VARCHAR(80),
    source VARCHAR(120),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE UNIQUE INDEX uk_economy_tips_external_id ON economy_tips (external_id)
    WHERE external_id IS NOT NULL;

CREATE INDEX idx_economy_tips_active_published ON economy_tips (active, priority, published_at DESC);
