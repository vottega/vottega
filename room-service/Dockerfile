# 빌드 컨텍스트는 리포지토리 루트다 (docker build -f room-service/Dockerfile .)
FROM gradle:8.12.1-jdk17 AS builder
WORKDIR /home/gradle/project

# 멀티모듈 전체를 복사한 뒤 이 모듈만 빌드
COPY --chown=gradle:gradle . .
RUN gradle :room-service:bootJar --no-daemon

# 2) 런타임 스테이지
FROM eclipse-temurin:17-jre-jammy
VOLUME /tmp
WORKDIR /app

# 빌더에서 만든 JAR만 복사
COPY --from=builder /home/gradle/project/room-service/build/libs/*.jar app.jar

# 컨테이너 내 포트
EXPOSE 8080

# JVM 옵션(필요시)
ENV JAVA_OPTS="-Xms256m -Xmx512m"

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
