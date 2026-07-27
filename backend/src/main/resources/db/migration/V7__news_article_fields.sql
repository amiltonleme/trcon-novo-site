alter table news_items add column slug varchar(120);
alter table news_items add column body text;
alter table news_items add column meta_title varchar(200);
alter table news_items add column meta_description varchar(320);

create unique index uk_news_slug on news_items (slug) where slug is not null;
