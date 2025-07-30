plugins {
  // apply false → 서브모듈에서만 실제 적용
  alias(libs.plugins.spring.boot)              apply false
  alias(libs.plugins.spring.dep.mgmt)          apply false
  alias(libs.plugins.kotlin.jvm)               apply false
  alias(libs.plugins.kotlin.spring)            apply false
  alias(libs.plugins.kotlin.jpa)               apply false
  alias(libs.plugins.avro)                     apply false
}

allprojects {
  group   = "vottega"
  version = "0.0.1-SNAPSHOT"
}

subprojects {
  // Java/Kotlin 공통
  java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(17)) }
  }

  tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
      freeCompilerArgs = listOf("-Xjsr305=strict")
      jvmTarget = "17"
    }
  }

  tasks.withType<Test>().configureEach {
    useJUnitPlatform()
  }

  // Spring Cloud BOM 공통 import
  apply(plugin = libs.plugins.spring.dep.mgmt.get().pluginId)
  dependencyManagement {
    imports { mavenBom(libs.boms.spring.cloud) }
  }
}