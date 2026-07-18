#!/bin/bash

IMAGE_NAME="${IMAGE_NAME:-nbank-tests}"

echo "Останавливаем тестовое окружение"
docker compose down
echo "Окружение остановлено"

if ! docker image inspect "$IMAGE_NAME" &> /dev/null; then
    echo "Собираем образ $IMAGE_NAME из $DOCKERFILE_PATH ..."
    docker build -t "$IMAGE_NAME" -f "$DOCKERFILE_PATH" .
    if [ $? -ne 0 ]; then
        echo "Ошибка сборки образа. Прерываем"
        exit 1
    fi
    echo "Образ собран"
else
    echo "Образ $IMAGE_NAME уже существует"
fi


echo "Поднимаем тестовое окружение с помощью Docker Compose"
docker compose up -d --remove-orphans
echo "Окружение запущено"

echo ">>> Запустить контейнер с тестами"
docker run --rm \
  -e APIBASEURL=http://localhost:4111 \
  -e  UIBASEURL=http://localhost:3000 \
  -e  SELENOID_URL=http://localhost:4444 \
  -e SELENOID_UI_URL=http://localhost:8080 \
  -e TEST_PROFILE="api,ui" \
  "$IMAGE_NAME"

echo ">>> Остановка окружения"
docker compose down