rootProject.name = "vottega-services"
include("user-service", "room-service", "vote-service",
  "sse-server", "gateway", "auth-server")

pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()
  }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    mavenCentral()
    maven("https://packages.confluent.io/maven/")
  }

  versionCatalogs {
    create("libs") {
      from(files("gradle/libs.versions.toml"))
    }
  }
}