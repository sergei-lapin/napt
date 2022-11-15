import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("org.jetbrains.kotlin.jvm")
  id("com.sergei-lapin.napt")
}

val jvmTarget = JavaVersion.VERSION_11.toString()

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = jvmTarget
}

tasks.withType<JavaCompile> {
  sourceCompatibility = jvmTarget
  targetCompatibility = jvmTarget
}

dependencies {
  implementation(libs.dagger)
  annotationProcessor(libs.daggerCompiler)
}
