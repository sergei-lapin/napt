@file:Suppress("UnstableApiUsage")

pluginManagement {
  plugins {
    val kotlinVersion = "1.7.10"
    id("org.jetbrains.kotlin.jvm") version kotlinVersion apply false
    id("org.jetbrains.dokka") version kotlinVersion apply false
  }
}

dependencyResolutionManagement {
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
  }
}

include(":gradle")

include(":javac")
