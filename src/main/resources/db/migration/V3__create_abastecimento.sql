create table abastecimento (
    id bigserial primary key,
    bomba_id bigint not null,
    data timestamp not null,
    litros numeric(10, 3) not null,
    preco_litro numeric(10, 2) not null,
    valor_total numeric(12, 2) not null,
    criado_em timestamp not null,
    constraint ck_abastecimento_litros check (litros > 0),
    constraint fk_abastecimento_bomba foreign key (bomba_id) references bomba (id)
);

create index idx_abastecimento_bomba_id on abastecimento (bomba_id);
create index idx_abastecimento_data on abastecimento (data);
