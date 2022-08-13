@file:Suppress("UnstableApiUsage")

pluginManagement {
    includeBuild("plugins")
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
    plugins {
        id("com.android.application") version "7.1.2" apply false
        id("org.jetbrains.kotlin.android") version "1.6.10" apply false
    }
}

rootProject.name = "napt"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
}

include(":sample")
