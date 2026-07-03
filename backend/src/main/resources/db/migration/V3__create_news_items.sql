create table news_items (
    id uuid primary key,
    source varchar(120) not null,
    category varchar(80) not null,
    title varchar(200) not null,
    summary varchar(600) not null,
    url varchar(500) not null,
    published_at timestamp with time zone not null,
    ingestion_batch varchar(80) null,
    created_at timestamp with time zone not null
);

create index idx_news_category on news_items (category);
create index idx_news_published_at on news_items (published_at desc);
