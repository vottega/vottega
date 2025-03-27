FROM openjdk:17
ARG JAVA_FILE=build/libs/*.jar
COPY ${JAVA_FILE}  user-service.jar
ENTRYPOINT ["java", "-jar", "/user-service.jar"]