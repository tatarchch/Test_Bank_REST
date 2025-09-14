--liquibase formatted sql

--changeset tatar:1
--preconditions onFail:warn
--precondition-sql-check expectedResult:1 select count(*) from information_schema.tables where table_name = 'users' and table_schema = 'dev'
--precondition-sql-check expectedResult:0 select count(*) from dev.users where username = 'admin'
--precondition-sql-check expectedResult:0 select count(*) from dev.users where username = 'user'
insert into dev.users (name, username, password, role) values
('Petr Petrov', 'admin','$2a$11$Ajlqx/pZ6zYJqgSSP4EdruQUIKwhmjVcLPLId13aavwjd8p8QTama', 'ADMIN'),
('Ivan Ivanov', 'user', '$2a$05$sfy7csYJVFnb.pyn9CSdsudL2PA1cGtifOfZomhZA/U4/BIpU7siS', 'USER');
--rollback delete from dev.users where username in ('admin', 'user')

--changeset tatar:2
--precondition-sql-check expectedResult:1 select count(*) from information_schema.tables where table_name = 'cards' and table_schema = 'dev'
--precondition-sql-check expectedResult:0 select count(*) from dev.cards where card_number = '176b5c5c0fe2d7807762729ceadb88e477c88315fbf6af1adb8e9d312bbb54cb1f8f7a7b88987280731da4b0d071a260'
insert into dev.cards (card_number, expire_date, owner_id, status, balance) values
('176b5c5c0fe2d7807762729ceadb88e477c88315fbf6af1adb8e9d312bbb54cb1f8f7a7b88987280731da4b0d071a260', '2029-09-02', 2, 'active', 1000.00);
--rollback delete from dev.cards where card_number = '176b5c5c0fe2d7807762729ceadb88e477c88315fbf6af1adb8e9d312bbb54cb1f8f7a7b88987280731da4b0d071a260';