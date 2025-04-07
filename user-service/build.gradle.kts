plugins {
  id("org.springframework.boot") version "3.3.2"
  id("io.spring.dependency-management") version "1.1.6"
  kotlin("plugin.jpa") version "1.9.24"
  kotlin("jvm") version "1.9.24"
  kotlin("plugin.spring") version "1.9.24"
}

group = "vottega"
version = "0.0.1-SNAPSHOT"

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(17)
  }
}

configurations {
  compileOnly {
    extendsFrom(configurations.annotationProcessor.get())
  }
}

ext {
  set("springCloudVersion", "2023.0.3")
}


dependencyManagement {
  imports {
    mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
  }
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-mail")

  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
  implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.springframework.kafka:spring-kafka")
  compileOnly("org.projectlombok:lombok")
  runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
  annotationProcessor("org.projectlombok:lombok")

  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
  testImplementation("org.springframework.security:spring-security-test")
  testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
  testImplementation("org.mockito:mockito-core:4.8.0")
  testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
  implementation("org.springframework.boot:spring-boot-starter-data-redis")
}

kotlin {
  compilerOptions {
    freeCompilerArgs.addAll("-Xjsr305=strict")
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}
