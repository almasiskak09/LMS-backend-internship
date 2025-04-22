# Main Service

Main Service — это микросервис, разработанный для работы в системе стажировок.

## Сервис работает на порту
http://localhost:7070

## Документация Swagger
Полная документация API доступна по ссылке: http://localhost:7070/swagger-ui/index.html#/

## Запуск через Docker
Для запуска всех необходимых сервисов используется `docker-compose.yaml`.  
При запуске поднимаются:
- Main Service
- Main Service Database
- Keycloak Service (из другого репозитория)
- Keycloak Service Database

Команда для запуска всех сервисов:
"docker-compose up --build"
