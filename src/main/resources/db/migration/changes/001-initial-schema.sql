--changeset tatar:1
--preconditions onFail:warn
--precondition-sql-check expected-result:0 select count(*) from information_schema.schemata where schema_name = 'dev'
create schema if not exists dev;

--changeset tatar:2
--preconditions onFail:warn
--precondition-sql-check expected-result:0 select count(*) from information_schema.sequences where sequence_schema = 'dev' and sequence_name = 'users_seq'
create sequence dev.users_seq
INCREMENT by 1
START with 1
MINVALUE 1
MAXVALUE 1000000
CACHE 1;
--rollback drop sequence dev.users_seq;

--changeset tatar:3
--preconditions onFail:warn
--precondition-sql-check expected-result:0 select count(*) from information_schema.sequences where sequence_schema = 'dev' and sequence_name = 'cards_seq'
create sequence dev.cards_seq
INCREMENT by 1
START with 1
MINVALUE 1
MAXVALUE 1000000
CACHE 1;
--rollback drop sequence dev.cards_seq;