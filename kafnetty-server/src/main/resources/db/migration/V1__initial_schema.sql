create table room
(
    id         uuid not null primary key,
    cluster_id character varying(30),
    name       varchar(50),
    is_sent    boolean
);
create table client
(
    id        uuid not null primary key,
    login     varchar(50),
    nick_name varchar(30),
    email     varchar(50),
    token     varchar(255)
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