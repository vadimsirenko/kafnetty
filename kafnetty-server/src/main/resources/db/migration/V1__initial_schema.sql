create table room
(
    id         uuid not null primary key,
    cluster_id character varying(30),
    name       varchar(50) CONSTRAINT UK_Room_Name UNIQUE,
    is_sent    boolean
);
create table _user
(
    id         uuid not null primary key,
    full_name  varchar(100),
    nick_name  varchar(30) CONSTRAINT UK_user_nick_name UNIQUE,
    email      varchar(50) CONSTRAINT UK_user_email UNIQUE,
    password   varchar(128),
    role       varchar(64),
    sent    boolean
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