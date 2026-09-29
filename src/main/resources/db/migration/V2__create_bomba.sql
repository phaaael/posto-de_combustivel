create table bomba (
    id bigserial primary key,
    nome varchar(120) not null,
    combustivel_id bigint not null,
    criado_em timestamp not null,
    atualizado_em timestamp not null,
    constraint uk_bomba_nome unique (nome),
    constraint fk_bomba_combustivel foreign key (combustivel_id) references combustivel (id)
);
