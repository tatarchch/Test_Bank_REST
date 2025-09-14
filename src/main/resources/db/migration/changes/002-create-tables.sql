--liquibase formatted sql

--changeset tatar:1
--preconditions onFail:warn
--precondition-sql-check expectedResult:0 select count(*) from information_schema.tables where table_name = 'users' and table_schema = 'dev'
create table dev.users (
    id bigint primary key not null default(nextval('dev.users_seq')),
    name varchar(225) not null,
    username varchar(50) unique not null,
    password varchar(255) not null,
    role varchar(15) not null default 'USER'
);
--rollback drop table dev.users

--changeset tatar:2
--preconditions onFail:warn
--precondition-sql-check expectedResult:0 select count(*) from information_schema.tables where table_name = 'cards' and table_schema = 'dev'
create table dev.cards (
    id bigint primary key not null default(nextval('dev.cards_seq')),
    card_number varchar(255) not null unique,
    owner_id bigint references dev.users(id) on delete cascade,
    expire_date date,
    status varchar(20) default 'active',
    balance decimal(15, 2) not null default 0.00,
    constraint balance_non_negative check (balance >= 0)
);
--rollback drop table dev.cards;
