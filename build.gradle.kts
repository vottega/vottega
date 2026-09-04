import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// 모든 모듈이 공유하는 플러그인 버전. 각 모듈은 plugins 블록에서 버전 없이 id 만 선언한다.
plugins {
  kotlin("jvm") version "1.9.25" apply false
  kotlin("plugin.spring") version "1.9.25" apply false
  kotlin("plugin.jpa") version "1.9.25" apply false
  id("org.springframework.boot") version "3.3.2" apply false
  id("io.spring.dependency-management") version "1.1.6" apply false
  id("com.github.davidmc24.gradle.plugin.avro") version "1.9.1" apply false
}

val springCloudVersion = "2023.0.3"

subprojects {
  group = "vottega"
  version = "0.0.1-SNAPSHOT"

  repositories {
    mavenCentral()
  }

  plugins.withId("org.jetbrains.kotlin.jvm") {
    configure<JavaPluginExtension> {
      toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
      }
    }

    configurations["compileOnly"].extendsFrom(configurations["annotationProcessor"])

    dependencies {
      "implementation"("org.jetbrains.kotlin:kotlin-reflect")
      "implementation"("com.fasterxml.jackson.module:jackson-module-kotlin")
      "compileOnly"("org.projectlombok:lombok")
      "annotationProcessor"("org.projectlombok:lombok")
      "testImplementation"("org.springframework.boot:spring-boot-starter-test")
      "testImplementation"("org.jetbrains.kotlin:kotlin-test-junit5")
      "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
    }

    tasks.withType<KotlinCompile>().configureEach {
      compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
      }
    }

    tasks.withType<Test>().configureEach {
      useJUnitPlatform()
    }
  }

  plugins.withId("io.spring.dependency-management") {
    configure<DependencyManagementExtension> {
      imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
      }
    }
  }
}
