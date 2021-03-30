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

#### Ignore NaptTrigger

Add `NaptTrigger.java` to root .gitignore

#### Conflitcting NaptTrigger classes

By default Gradle plugin will generate NaptTrigger with module-named package so the FQ names won't clash, but, just in case, the prefix of NaptTrigger package can be specified like that:

``` Gradle
napt {
    naptTriggerPackagePrefix.set("com.slapin.napt")
}
```

Assume we're in module named `sample`, will result in the following `NaptTrigger.java`:

``` Java
package com.slapin.napt.sample;
class NaptTrigger {
}
```

## Dev mode

- Uncomment `includeBuild("napt-gradle")` in root `settings.gradle`
- Run Gradle sync
