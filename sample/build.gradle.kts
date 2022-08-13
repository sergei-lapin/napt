@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.sergei-lapin.napt")
}

napt { naptTriggerPackagePrefix.set("com.slapin.napt") }

android {
    compileSdk = 30
    buildToolsVersion = "30.0.3"

    defaultConfig {
        applicationId = "com.slapin.napt.sample"
        minSdk = 21
        targetSdk = 30
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions { jvmTarget = JavaVersion.VERSION_1_8.toString() }
}

androidComponents { beforeVariants { builder -> builder.enable = builder.name == "debug" } }

dependencies {
    implementation("androidx.core:core-ktx:1.3.2")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation("com.google.android.material:material:1.3.0")

    val daggerVersion = "2.33"
    implementation("com.google.dagger:dagger:$daggerVersion")
    annotationProcessor("com.google.dagger:dagger-compiler:$daggerVersion")
}
