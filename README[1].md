
# OTP Service

Backend-сервис для генерации и проверки одноразовых OTP-кодов.

Проект реализует:
- регистрацию и авторизацию пользователей;
- JWT-аутентификацию;
- роли `ADMIN` и `USER`;
- генерацию OTP-кодов;
- валидацию OTP-кодов;
- хранение данных в PostgreSQL;
- отправку OTP через файл;
- логирование запросов;
- работу через HTTP API на порту `8080`.

---

## Технологии

- Java 17+
- Spring Boot
- Spring Security
- JWT
- JDBC
- PostgreSQL 17
- Maven

---

## Запуск проекта

### 1. Создать базу данных PostgreSQL

```sql
CREATE DATABASE otp_service;
````

---

### 2. Настроить подключение к БД

Файл:

```text
src/main/resources/application.properties
```

Пример:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/otp_service
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD

spring.sql.init.mode=always

server.port=8080
```

---

### 3. Запустить приложение

Через IntelliJ IDEA:

```text
Run → OtpApplication
```

Или через Maven:

```bash
mvn spring-boot:run
```

После успешного запуска в консоли должно быть:

```text
Started OtpApplication
Tomcat started on port 8080
```

Сервис доступен по адресу:

```text
http://localhost:8080
```

---

## Основные API

Все запросы выполняются через:

```text
http://localhost:8080
```

---

# Auth API

## Регистрация пользователя

```http
POST http://localhost:8080/api/auth/register
```

Body:

```json
{
  "login": "admin",
  "password": "1234",
  "role": "ADMIN"
}
```

Для обычного пользователя:

```json
{
  "login": "user1",
  "password": "1234",
  "role": "USER"
}
```

Важно: в системе может быть только один пользователь с ролью `ADMIN`.

---

## Авторизация

```http
POST http://localhost:8080/api/auth/login
```

Body:

```json
{
  "login": "admin",
  "password": "1234"
}
```

Ответ содержит JWT-токен.

Для защищённых запросов нужно передавать токен:

```http
Authorization: Bearer YOUR_TOKEN
```

В Postman:

```text
Authorization → Type: Bearer Token → Token: YOUR_TOKEN
```

---

# Admin API

Доступно только пользователю с ролью `ADMIN`.

## Получить список пользователей

```http
GET http://localhost:8080/api/admin/users
```

Header:

```http
Authorization: Bearer YOUR_ADMIN_TOKEN
```

---

## Изменить конфигурацию OTP

```http
PUT http://localhost:8080/api/admin/config
```

Body:

```json
{
  "codeLength": 6,
  "ttlSeconds": 120
}
```

Где:

* `codeLength` — длина OTP-кода;
* `ttlSeconds` — время жизни OTP-кода в секундах.

---

## Удалить пользователя

```http
DELETE http://localhost:8080/api/admin/users/{id}
```

Пример:

```http
DELETE http://localhost:8080/api/admin/users/2
```

---

# User API

Доступно пользователю с ролью `USER` или `ADMIN`.

## Сгенерировать OTP-код

```http
POST http://localhost:8080/api/user/otp/generate
```

Body:

```json
{
  "operationId": "payment-1",
  "destination": "test",
  "channel": "FILE"
}
```

Header:

```http
Authorization: Bearer YOUR_TOKEN
```

---

## Проверить OTP-код

```http
POST http://localhost:8080/api/user/otp/validate
```

Body:

```json
{
  "operationId": "payment-1",
  "code": "123456"
}
```

Ответ:

```json
{
  "valid": true
}
```

После успешной проверки код получает статус `USED`.

---

## Каналы отправки OTP

Поддерживаются значения поля `channel`:

```text
FILE
```

Для быстрого тестирования рекомендуется использовать:

```json
"channel": "FILE"
```

В этом случае OTP-код сохраняется в файл в корне проекта:

```text
otp-codes.txt
```
