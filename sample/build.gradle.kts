@file:Suppress("UnstableApiUsage")

import com.slapin.napt.JvmArgsStrongEncapsulation

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.sergei-lapin.napt")
    id("com.google.dagger.hilt.android") version "2.44"
}

napt {
    naptTriggerPackagePrefix.set("com.slapin.napt")
    forkJvmArgs.set(listOf("-Dsome.prop=some-value") + JvmArgsStrongEncapsulation)
}

hilt {
    enableAggregatingTask = false
}

android {
    compileSdk = 33
    namespace = "com.slapin.napt.sample"

    buildFeatures {
        dataBinding = true
    }

    defaultConfig {
        applicationId = "com.slapin.napt.sample"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()
}

androidComponents { beforeVariants { builder -> builder.enable = builder.name == "debug" } }

dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.material:material:1.6.1")

    val daggerVersion = "2.44"
    implementation("com.google.dagger:dagger:$daggerVersion")
    implementation("com.google.dagger:hilt-android:$daggerVersion")
    annotationProcessor("com.google.dagger:dagger-compiler:$daggerVersion")
    annotationProcessor("com.google.dagger:hilt-compiler:$daggerVersion")
}
