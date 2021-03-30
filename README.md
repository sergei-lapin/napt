# NAPT

[![](https://jitpack.io/v/sergei-lapin/napt.svg)](https://jitpack.io/#sergei-lapin/napt)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## What is it?

An alternative to KAPT that skips stub generation and hence runs up to 50% faster

## Usage

*JDK 9+ is required to run this particular set of tools*

## Sample

You could see an example of usage in [sample](https://github.com/sergei-lapin/napt/blob/main/sample/build.gradle)

## Download
- javac plugin is distributed through JitPack
- Gradle plugin is distributed through Gradle Plugin Portal

#### Add to the root build.gradle

``` Gradle
buildscript {
    repositories {
        gradlePluginPortal()
    }
}

subprojects {
    repositories {
        maven { url("https://jitpack.io") }
    }
}
```

#### Add library module:

``` Gradle
plugins {
    id("com.sergei-lapin.napt") version("{latest-version}")
}

dependencies {
    annotationProcessor("com.github.sergei-lapin:napt:{latest-version}")
}
```

## Dev mode

- Uncomment `includeBuild("napt-gradle")` in root `settings.gradle`
- Run Gradle sync
