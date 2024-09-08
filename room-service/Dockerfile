FROM openjdk:17
ARG JAVA_FILE=build/libs/*.jar
COPY ${JAVA_FILE}  room-service.jar
ENTRYPOINT ["java", "-jar", "/room-service.jar"]