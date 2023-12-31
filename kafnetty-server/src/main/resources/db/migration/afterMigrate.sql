TRUNCATE TABLE _user;
INSERT INTO _user(id, full_name, nick_name, email, password, role, sent)
VALUES ('a5461723-b1de-4351-a2f0-94bc2e1a2f92', 'Вадим Сиренко', 'Вадим', 'vadim@mail.ru', '$2a$10$r4emLi5YWWu2rruNkXixjOFKDD8paiBV1UUMfmJ9ci0CebKl4cF7e', 'ADMIN', true),
       ('f9b26b13-6a8e-41e0-9332-9b14eaa6d60a', 'Сергей Иванов', 'Сергей', 'sergey@mail.ru', '$2a$10$r4emLi5YWWu2rruNkXixjOFKDD8paiBV1UUMfmJ9ci0CebKl4cF7e', 'USER', true),
       ('92aeaf7c-39e3-49a7-b69a-18de73efb479', 'Олег Погудин','Олег', 'oleg@mail.ru', '$2a$10$r4emLi5YWWu2rruNkXixjOFKDD8paiBV1UUMfmJ9ci0CebKl4cF7e', 'USER', false);

TRUNCATE TABLE room;
INSERT INTO room(id, cluster_id, name, is_sent)
VALUES ('2bd09cbf-ef16-469f-82ab-f51ae9913aa0', 'test-1', 'Первый чат', false),
       ('643c0714-a948-42c3-946a-a4818896a219', 'test-1', 'Второй чат', false),
       ('54a72323-1be5-460e-a4c4-43f2cb227a1f', 'kafnetty-1', 'Третий чат', false);

TRUNCATE TABLE message;
INSERT INTO message(id, cluster_id, sender_id, sender, room_id, message_text, ts, is_sent)
VALUES ('fd2d7cc1-5ac9-40e9-b4f7-fc518eedd095', 'test-1', 'a5461723-b1de-4351-a2f0-94bc2e1a2f92', 'Вадим',
        '2bd09cbf-ef16-469f-82ab-f51ae9913aa0',
        'Our use case is simple, the server will simply createMessage the request body and query parameters, if any, to uppercase. A word of caution here on reflecting request data in the response – we are doing this only for demonstration purposes, to understand how we can use Netty to implement an HTTP server.',
        4324324, false),
       ('057a7522-df71-4406-9559-844e7ce7cf4c', 'test-1', 'a5461723-b1de-4351-a2f0-94bc2e1a2f92', 'Вадим',
        '2bd09cbf-ef16-469f-82ab-f51ae9913aa0',
        'As we can see, when our channel receives an HttpRequest, it first checks if the request expects a 100 Continue status. In that case, we immediately write back with an empty response with a status of CONTINUE',
        3254356, false),
       ('3eac2bf1-e223-4bbf-a51e-2c3d3ef71cbc', 'test-1', 'a5461723-b1de-4351-a2f0-94bc2e1a2f92', 'Вадим',
        '2bd09cbf-ef16-469f-82ab-f51ae9913aa0',
        'Can anyone provide an example of a simple HTTP server implemented using Netty, that supports persistent HTTP connections. In other words, it won''t close the connection until the user closes it, and can receive additional HTTP requests over the same connection?',
        54366534, false),
       ('9c2ccdad-5aca-474b-8151-f8368a65b844', 'test-1', 'f9b26b13-6a8e-41e0-9332-9b14eaa6d60a', 'Сергей',
        '2bd09cbf-ef16-469f-82ab-f51ae9913aa0',
        'In other words, it won''t close the connection until the user closes it, and can receive additional HTTP requests over the same connection?',
        3543535, false),
       ('65eda846-3dfe-48b1-8c2b-8304e047c544', 'test-1', 'a5461723-b1de-4351-a2f0-94bc2e1a2f92', 'Вадим',
        '2bd09cbf-ef16-469f-82ab-f51ae9913aa0',
        'Netty — это фреймвёрк, позволяющий разрабатывать высокопроизводительные сетевые приложения. Подробнее о нём можно прочитать на сайте проекта. Для создания сокет-серверов Netty предоставляет весьма удобный функционал, но для создание REST-серверов данный функционал, на мой взгляд, является не очень удобным.',
        54376765, false),
       ('2bd09cbf-ef16-469f-82ab-f51ae9913aa0', 'test-2', '92aeaf7c-39e3-49a7-b69a-18de73efb479', 'Олег',
        '2bd09cbf-ef16-469f-82ab-f51ae9913aa0',
        'Just use overflow: auto. Since your content by default just breaks to the next line when it cannot fit on the current line, a horizontal scrollbar won''t be created (unless it''s on an element that has word-wrapping disabled). For the vertical bar,it will allow the content to expand up to the height you have specified. If it exceeds that height, it will show a vertical scrollbar to view the rest of the content, but will not show a scrollbar if it does not exceed the height.',
        1698398477040, false),
       ('0ea840e0-8c9f-4a4e-93ef-5cb75a28027e', 'test-1', 'f9b26b13-6a8e-41e0-9332-9b14eaa6d60a', 'Сергей',
        '2bd09cbf-ef16-469f-82ab-f51ae9913aa0',
        'Вот, в общем-то, и всё. Используя указанные выше методы, можно обрабатывать http-запросы. Правда, обрабатывать всё придётся в одном месте, а именно в методе channelRead. Даже если разнести логику обработки запросов по разным методам и классам, всё равно придётся сопоставлять URL с этими методами где-то в одном месте.',
        76457543, false);