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
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.boot:spring-boot-starter-validation")
  implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
  implementation("org.springframework.kafka:spring-kafka")
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
  implementation("vottega:security-starter:1.1.2")
  implementation("org.apache.avro:avro:1.11.4")
  implementation("io.confluent:kafka-avro-serializer:7.5.0")
  implementation("com.h2database:h2")

  runtimeOnly("org.mariadb.jdbc:mariadb-java-client")

  testImplementation("org.springframework.kafka:spring-kafka-test")
  testImplementation("org.springframework.security:spring-security-test")
  testImplementation("org.testcontainers:kafka")
  testImplementation("org.testcontainers:junit-jupiter")
}
