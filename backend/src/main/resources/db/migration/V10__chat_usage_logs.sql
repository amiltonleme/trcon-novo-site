create table chat_usage_logs (
    id uuid primary key,
    model varchar(80) not null,
    prompt_tokens integer not null,
    completion_tokens integer not null,
    estimated_cost_usd numeric(12,8) not null,
    client_hash varchar(64) not null,
    created_at timestamp with time zone not null
);

create index idx_chat_usage_created_at on chat_usage_logs (created_at);
