plugins {
  kotlin("jvm")
  kotlin("plugin.spring")
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
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
  implementation("org.springframework.boot:spring-boot-configuration-processor")
  implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
  implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
  implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.5.0")
  implementation("vottega:security-starter:1.1.2")
  implementation("org.apache.avro:avro:1.11.4")
  implementation("io.confluent:kafka-avro-serializer:7.5.0")
  implementation("io.projectreactor.kafka:reactor-kafka:1.3.23")
  implementation("io.projectreactor:reactor-core:3.6.11")

  testImplementation("io.projectreactor:reactor-test")
  testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
}
