#!/bin/bash

# 스크립트 사용법 출력 함수
function usage() {
    echo "Usage: $0 {init|exit} local"
    exit 1
}

# 인자 확인
if [ $# -ne 2 ]; then
    usage
fi

# 변수 설정
ACTION=$1
TARGET=$2

# 조건 처리
if [ "$TARGET" == "local" ]; then
    if [ "$ACTION" == "init" ]; then
        echo "Starting Kafka and Database services..."
        docker-compose -f kafka-docker-compose.yml up -d
        docker-compose -f database-docker-compose.yml up -d
        echo "Services started successfully."
    elif [ "$ACTION" == "exit" ]; then
        echo "Stopping Kafka and Database services..."
        docker-compose -f kafka-docker-compose.yml down
        docker-compose -f database-docker-compose.yml down
        echo "Services stopped successfully."
    else
        usage
    fi
else
    usage
fi