create table room
(
    id         uuid not null primary key,
    cluster_id character varying(30),
    name       varchar(50) CONSTRAINT UK_Room_Name UNIQUE,
    is_sent    boolean
);
create table client
(
    id         uuid not null primary key,
    cluster_id character varying(30),
    login      varchar(50) CONSTRAINT UK_Client_Login UNIQUE,
    nick_name  varchar(30) CONSTRAINT UK_Client_nick_name UNIQUE,
    email      varchar(50) CONSTRAINT UK_Client_email UNIQUE,
    token      varchar(255),
    ts         bigint,
    is_sent    boolean
);
create table message
(
    id           uuid not null primary key,
    cluster_id   character varying(30),
    sender_id    uuid not null,
    sender       character varying(30),
    room_id      uuid null,
    message_text varchar(1024),
    ts           bigint,
    is_sent      boolean
);