create table combustivel (
    id bigserial primary key,
    nome varchar(120) not null,
    preco_litro numeric(10, 2) not null,
    criado_em timestamp not null,
    atualizado_em timestamp not null,
    constraint uk_combustivel_nome unique (nome),
    constraint ck_combustivel_preco_litro check (preco_litro > 0)
);
