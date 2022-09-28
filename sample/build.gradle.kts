@file:Suppress("UnstableApiUsage")

plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("com.sergei-lapin.napt")
  id("com.google.dagger.hilt.android") version libs.versions.dagger.get()
}

napt {
  naptTriggerPackagePrefix.set("com.slapin.napt")
  forkJvmArgs.set(listOf("-Dsome.prop=some-value"))
  additionalSourceSetsForTriggerGeneration.set(setOf("test", "androidTest"))
}

hilt { enableAggregatingTask = false }

android {
  compileSdk = 32
  buildToolsVersion = "30.0.3"

  buildFeatures { dataBinding = true }

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
  implementation(libs.androidXCoreKtx)
  implementation(libs.androidXAppcompat)
  implementation(libs.androidXConstraintlayout)
  implementation(libs.googleMaterial)

  implementation(libs.dagger)
  implementation(libs.hiltAndroid)
  annotationProcessor(libs.daggerCompiler)
  annotationProcessor(libs.hiltCompiler)
}
