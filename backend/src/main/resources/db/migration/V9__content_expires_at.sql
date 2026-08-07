-- L0: TTL de visibilidade para artigos e tips editoriais (soft-hide)
ALTER TABLE news_items
    ADD COLUMN IF NOT EXISTS expires_at TIMESTAMPTZ NULL;

ALTER TABLE economy_tips
    ADD COLUMN IF NOT EXISTS expires_at TIMESTAMPTZ NULL;

CREATE INDEX IF NOT EXISTS idx_news_items_expires_at ON news_items (expires_at);
CREATE INDEX IF NOT EXISTS idx_economy_tips_expires_at ON economy_tips (expires_at);
