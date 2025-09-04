--liquibase formatted sql

--changeset tatar:1
insert into dev.users (name, username, password, role) values
('Petr Petrov', 'admin','$2a$11$Ajlqx/pZ6zYJqgSSP4EdruQUIKwhmjVcLPLId13aavwjd8p8QTama', 'ADMIN'),
('Ivan Ivanov', 'user', '$2a$05$sfy7csYJVFnb.pyn9CSdsudL2PA1cGtifOfZomhZA/U4/BIpU7siS', 'USER');

insert into dev.cards (card_number, expire_date, owner_id, status, balance) values
('176b5c5c0fe2d7807762729ceadb88e477c88315fbf6af1adb8e9d312bbb54cb1f8f7a7b88987280731da4b0d071a260', '2029-09-02', 2, 'active', 1000.00);