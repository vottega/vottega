plugins {
  kotlin("jvm") version "1.9.25"
  kotlin("plugin.spring") version "1.9.25"
  id("org.springframework.boot") version "3.4.0"
  id("io.spring.dependency-management") version "1.1.6"
  id("com.github.davidmc24.gradle.plugin.avro") version "1.9.1"
}

group = "vottega"
version = "0.0.1-SNAPSHOT"

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(17)
  }
}

repositories {
  mavenCentral()
  maven("https://packages.confluent.io/maven/")
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
  implementation("org.springframework.boot:spring-boot-configuration-processor")
  implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  //implementation("org.springframework.boot:spring-boot-starter-security")
  testImplementation("io.projectreactor:reactor-test")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
  testImplementation("org.springframework.kafka:spring-kafka-test")
  testImplementation("org.testcontainers:kafka")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
  implementation("org.apache.avro:avro:1.11.4")
  implementation("io.confluent:kafka-avro-serializer:7.5.0")
  implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.5.0")

  implementation("io.projectreactor.kafka:reactor-kafka:1.3.23")
  implementation("io.projectreactor:reactor-core:3.6.11")
}

kotlin {
  compilerOptions {
    freeCompilerArgs.addAll("-Xjsr305=strict")
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}
