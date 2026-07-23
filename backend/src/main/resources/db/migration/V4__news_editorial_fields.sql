alter table news_items add column brand_slug varchar(80);
alter table news_items add column external_id varchar(120);

create unique index uk_news_external_id on news_items (external_id) where external_id is not null;
