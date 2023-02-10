@file:Suppress("UnstableApiUsage")

pluginManagement {
  includeBuild("plugins")
  repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
  }
}

rootProject.name = "napt"

dependencyResolutionManagement {
  repositories {
    mavenCentral()
    google()
  }
}

include(":sample-android-app")
include(":sample-kotlin-lib")
