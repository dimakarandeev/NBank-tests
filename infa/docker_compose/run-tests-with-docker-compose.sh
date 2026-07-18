#!/bin/bash

IMAGE_NAME="${IMAGE_NAME:-nbank-tests}"
TIMESTAMP=$(date +"%Y%m%d_%H%M")
TEST_OUTPUT_DIR=$(pwd -W)/test-output/$TIMESTAMP

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

mkdir -p "$TEST_OUTPUT_DIR/logs"
mkdir -p "$TEST_OUTPUT_DIR/results"
mkdir -p "$TEST_OUTPUT_DIR/report"

echo "Поднимаем тестовое окружение с помощью Docker Compose"
docker compose up -d --remove-orphans
echo "Окружение запущено"

echo "⏳ Ждём 5 секунд для запуска сервисов..."
sleep 5

echo ">>> Запустить контейнер с тестами"
docker run --rm \
  --network nbank-network \
  -v "$TEST_OUTPUT_DIR/logs":/app/logs \
  -v "$TEST_OUTPUT_DIR/results":/app/target/surefire-reports \
  -v "$TEST_OUTPUT_DIR/report":/app/target/site \
  -e APIBASEURL=http://localhost:4111 \
  -e UIBASEURL=http://localhost:3000 \
  -e SELENOID_URL=http://localhost:4444 \
  -e SELENOID_UI_URL=http://localhost:8080 \
  -e TEST_PROFILE="api,ui" \
  "$IMAGE_NAME"

echo ">>> Тесты завершены"
echo "Лог файл: $TEST_OUTPUT_DIR/logs/run.log"
echo "Результаты тестов: $TEST_OUTPUT_DIR/results"
echo "Репорт: $TEST_OUTPUT_DIR/report"

echo ">>> Остановка окружения"
docker compose down