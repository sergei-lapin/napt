@file:Suppress("UnstableApiUsage")

pluginManagement {
  includeBuild("plugins")
  repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
  }
  plugins {
    id("com.android.application") version "7.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.7.10" apply false
  }
}

rootProject.name = "napt"

dependencyResolutionManagement {
  includeBuild("plugins")
  repositories {
    mavenCentral()
    google()
  }
}

include(":sample")
