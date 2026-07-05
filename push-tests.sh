#!/bin/bash

IMAGE_NAME="${IMAGE_NAME:-nbank-tests}"
TAG="${1:-latest}"

# Проверяем, что переменные окружения установлены
if [ -z "$DOCKERHUB_USERNAME" ] || [ -z "$DOCKERHUB_TOKEN" ]; then
    echo "ОШИБКА: необходимо задать переменные окружения DOCKERHUB_USERNAME и DOCKERHUB_TOKEN"
    echo "Например:"
    echo "  export DOCKERHUB_USERNAME=myusername"
    echo "  export DOCKERHUB_TOKEN=dckr_pat_..."
    exit 1
fi

FULL_IMAGE_NAME="$DOCKERHUB_USERNAME/$IMAGE_NAME:$TAG"

# Авторизация в Docker Hub по token
echo ">>> Логин в Docker Hub"
echo "$DOCKERHUB_TOKEN" | docker login -u "$DOCKERHUB_USERNAME" --password-stdin

# формат: <dockerhub-username>/<image-name>:<tag>
echo ">>> Тегирование и пуш образа $FULL_IMAGE_NAME ..."
docker tag "$IMAGE_NAME" "$FULL_IMAGE_NAME"
docker push "$FULL_IMAGE_NAME"

echo ""
echo ">>> Готово! Образ запушен: $FULL_IMAGE_NAME"
echo ">>> Скачать: docker pull $FULL_IMAGE_NAME"