# netty-kafka-chat

Chat based on Netty and Kafka

![alt text](https://github.com/vadimsirenko/kafnetty/blob/blob/chat1.JPG?raw=true)

Данная версия чата предназначена для возможности общения пользователей в путешествиях на ЖД, паромах, дальних поездках.


![alt text](https://github.com/vadimsirenko/kafnetty/blob/blob/img1.jpg?raw=true)

Все информационное пространство чатов поделено на области - домены. В рамках каждого домена происходит клиент-серверное взаимодействие по технологии WebSockets. У каждого домена есть база (postgres).

Каждый домен не зависит от других и позволяет подключенным к нему пользователям пользователям общаться между собой.

Взаимодействие между доменами происходит через Kafka и при наличии связи с Kafka общение пользователей расширяется добавлением пользователей подключенных к Kafka доменов. В этом случае вхаимодействие происходит: 
```
WebBrowser -> WebSocket -> Kafka -> WebSocket -> WebBrowser
```
При появлении связи сообщения, не отправленные в Kafka для других пользователей, отправляются.
Между доменами пересылаются сообщения:
- комнаты (чаты)
- сообщения чатов
- профиль пользователей

## Общая схема приложения

![alt text](https://github.com/vadimsirenko/kafnetty/blob/blob/img2.jpg?raw=true)

Приложение состоит из следующх компонентов:
- Сервера на Netty **KafnettyServer** которое принимает запрос на подключение (http://localhost:8181/chat.html) и открывающее канал для обмена с клиентом
- Базы данных **Postgres** (**ChatDB**) с не связанными сущностями (таблицами): **message, client и room**, так как порядок получения из Kafka сущностей мы не можем контролировать.
- Службы отправки в Kafka сообщений - **KafnettyProducer**
- Службы приема сообщений их Kafka - **KafnettyConsumer**

 ![alt text](https://github.com/vadimsirenko/kafnetty/blob/blob/db.jpg?raw=true)

## Используемые технологии и библиотеки
- netty - построение веб-сервера
- jackson-dataformat-xml - Json сериализация
- lombok - вспомогательная библиотека
- spring-data-jpa - доступ к БД
- mapstruct - маппинги
- flyway-core - миграция БД
- tika-core - получение Mime-type по файлу в ресурсах для формирования ответов http (css, js,..)
- kafka-clients - consumer/producer - взаимодействие с Kafka

## План разработки
- [x] Структура проекта
- [x] Объекты сущности
- [x] Http-WebSocker сервер
- [x] Http - отдача контента - интерфейса чата
- [x] Сохранение сообщений в БД
- [x] Передача сообщений в Kafka
- [x] Прием сообщений из Kafka
- [X] Сохранение сообщений из Kafka в БД и вывод в онлайн чат (WebSocket)
- [X] Создание чатов (комнат) - интерфейс
- [X] Сохранение чатов (комнат) в БД
- [X] Передача чатов (комнат) в Kafka
- [X] Сохранение чатов (комнат) из Kafka в БД и добавление в списко в онлайн чат (WebSocket)
- [X] Изменение профиля пользователя в интерфейсе
- [X] Сохранение профиля пользователя в БД
- [X] Передача профиля пользователя в Kafka
- [X] Прием профиля пользователя из Kafka
- [X] Сохранение профиля пользователя из Kafka в БД
- [ ] Создание процесса передачи не переданных (из-за отсутствия связи) в Kafka