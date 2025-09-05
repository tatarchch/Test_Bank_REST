--liquibase formatted sql

--changeset tatar:1
create schema if not exists dev;

create sequence dev.users_seq
INCREMENT by 1
START with 1
MINVALUE 1
MAXVALUE 1000000
CACHE 1;

create sequence dev.cards_seq
INCREMENT by 1
START with 1
MINVALUE 1
MAXVALUE 1000000
CACHE 1;

create sequence dev.transfers_seq
INCREMENT by 1
START with 1
MINVALUE 1
MAXVALUE 1000000
CACHE 1;