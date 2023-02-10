@file:Suppress("UnstableApiUsage")

plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("com.sergei-lapin.napt")
  id("com.google.dagger.hilt.android")
}

napt {
  naptTriggerPackagePrefix.set("com.slapin.napt")
  forkJvmArgs.set(listOf("-Dsome.prop=some-value"))
  additionalSourceSetsForTriggerGeneration.set(setOf("test", "androidTest"))
}

hilt { enableAggregatingTask = false }

android {
  compileSdk = 33
  namespace = "com.slapin.napt.sample"

  buildFeatures { dataBinding = true }

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
}

kotlin {
  jvmToolchain(11)
}

androidComponents { beforeVariants { builder -> builder.enable = builder.name == "debug" } }

dependencies {
  implementation(projects.sampleKotlinLib)

  implementation(libs.androidXCoreKtx)
  implementation(libs.androidXAppcompat)
  implementation(libs.androidXConstraintlayout)
  implementation(libs.googleMaterial)

  implementation(libs.dagger)
  implementation(libs.hiltAndroid)
  annotationProcessor(libs.daggerCompiler)
  annotationProcessor(libs.hiltCompiler)
}
