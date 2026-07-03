create table leads (
    id uuid primary key,
    nome varchar(160) not null,
    email varchar(160) not null,
    telefone varchar(40) not null,
    tipo_interesse varchar(40) not null,
    mensagem text null,
    origem varchar(80) not null,
    status varchar(32) not null,
    consentimento_lgpd boolean not null,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null
);

create unique index uq_leads_email_origem on leads (email, origem);
create index idx_leads_tipo_interesse on leads (tipo_interesse);
