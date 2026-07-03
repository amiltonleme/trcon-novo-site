create table daily_highlights (
    id uuid primary key,
    category varchar(80) not null,
    title varchar(200) not null,
    summary varchar(600) not null,
    link varchar(300) null,
    priority integer not null,
    active boolean not null default true,
    published_at timestamp with time zone not null,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null
);

create index idx_highlights_active_priority on daily_highlights (active, priority, published_at desc);
