plugins {
  kotlin("jvm")
  kotlin("plugin.spring")
  kotlin("plugin.jpa")
  id("org.springframework.boot")
  id("io.spring.dependency-management")
  id("com.github.davidmc24.gradle.plugin.avro")
}

repositories {
  maven {
    url = uri("https://maven.pkg.github.com/vottega/security")
    credentials {
      username = findProperty("gpr.user") as String?
        ?: System.getenv("GITHUB_ACTOR")
      password = findProperty("gpr.key") as String?
        ?: System.getenv("GITHUB_TOKEN")
    }
  }
  maven("https://packages.confluent.io/maven/")
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-data-redis")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
  implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
  implementation("org.springframework.kafka:spring-kafka")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
  implementation("io.swagger.core.v3:swagger-annotations:2.2.20")
  implementation("vottega:security-starter:1.1.2")
  implementation("org.apache.avro:avro:1.11.4")
  implementation("io.confluent:kafka-avro-serializer:7.5.0")
  implementation("com.h2database:h2")

  runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
  runtimeOnly("com.mysql:mysql-connector-j")

  testImplementation("org.springframework.kafka:spring-kafka-test")
  testImplementation("org.springframework.security:spring-security-test")
  testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
  testImplementation("org.mockito:mockito-core:4.8.0")
  testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
  testImplementation("org.testcontainers:testcontainers:1.20.6")
  testImplementation("org.testcontainers:junit-jupiter:1.20.6")
}
