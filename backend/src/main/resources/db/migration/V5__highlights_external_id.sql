alter table daily_highlights add column external_id varchar(120);

create unique index uk_highlights_external_id on daily_highlights (external_id) where external_id is not null;
