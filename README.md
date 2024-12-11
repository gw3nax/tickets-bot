# Ticket Tracker Bot

Ticket Tracker Bot — это микросервис, реализующий логику общения с пользователем через Telegram.

Микросервис взаимодействует с [TicketTrackerAPI](https://github.com/gw3nax/TicketTrackerAPI) для получения данных об авиабилетах.

## Содержание

1. [Описание проекта](#Описание-проекта)
2. [Технологии](#Технологии)
3. [Структура проекта](#Структура-проекта)
4. [Установка и запуск](#Установка-и-запуск)
    - [Требования](#Требования)
    - [Локальный запуск через Docker Compose](#Локальный-запуск-через-Docker-Compose)
5. [Как взаимодействовать с ботом](#Как-взаимодействовать-с-ботом)

## Описание проекта

- **Управление запросами**: создание и удаление запросов на поиск билета.
- **Отслеживание цен на билеты**: получение актуальной информации об интересующих вас билетах.
- **Интеграция с Kafka**: обмен сообщениями между микросервисами через брокер Kafka.
- **Использование базы данных**: хранение данных в PostgreSQL с поддержкой миграций через Liquibase.

## Технологии

- **Java 21**
- **Spring Boot**
- **PostgreSQL**
- **Docker / Docker Compose**
- **Apache Kafka**
- **Liquibase**
- **Gradle**
- **[Telegram Bot Api](https://github.com/pengrad/java-telegram-bot-api)**

## Структура проекта
```
TicketTrackerBot/
│
├── src/
│   ├── main/                      # Исходный код приложения
│   ├── test/                      # Тесты приложения
│
├── .gitignore                     # Исключения для Git
├── docker-compose/
│   ├── docker-compose.yml         # compose файл для развертывания приложения в Docker
│   ├── DockerFile                 # Dockerfile для создания образа
│   ├── kafka_server_jaas.conf     # Конфигурационный файл для Kafka
│   ├── zookeeper_jaas.conf        # Конфигурационный файл для Zookeeper
├── build.gradle                   # Gradle файл для сборки
└── README.md                      # Документация
```

## Установка и запуск

### Требования

- Docker и Docker Compose
- Java 21+
- Gradle

### Локальный запуск через Docker Compose

1. Клонируйте репозиторий:
    ```bash
    git clone https://github.com/gw3nax/tickets-bot.git
    cd tickets-bot
    ```

2. Запустите все контейнеры:
    ```bash
    docker-compose up
    ```

## Как взаимодействовать с ботом

1. Найдите бота в Telegram.
2. Введите команду `/start`.
3. Используйте команды бота для создания и управления запросами к API.
