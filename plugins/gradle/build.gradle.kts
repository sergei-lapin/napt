import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("org.jetbrains.kotlin.jvm")
  id("org.jetbrains.dokka")
  id("com.gradle.plugin-publish") version "1.0.0"
  id("java-gradle-plugin")
}

group = "com.sergei-lapin.napt"

version = "1.17"

tasks.withType<KotlinCompile> { kotlinOptions.jvmTarget = "9" }

tasks.withType<JavaCompile> {
  sourceCompatibility = JavaVersion.VERSION_1_9.toString()
  targetCompatibility = JavaVersion.VERSION_1_9.toString()
}

java {
  withJavadocJar()
  withSourcesJar()
}

tasks.named<Jar>("javadocJar") { from(tasks.named("dokkaJavadoc")) }

dependencies {
  implementation(gradleApi())
  val kotlinVersion = "1.7.10"
  compileOnly("com.android.tools.build:gradle:7.2.0")
  compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
  dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:$kotlinVersion")
}

gradlePlugin {
  plugins.create("napt") {
    id = group.toString()
    displayName = "NAPT"
    description = "The Gradle Plugin for enabling NAPT javac plugin"
    implementationClass = "com.slapin.napt.NaptGradlePlugin"
  }
}

pluginBundle {
  website = "https://github.com/sergei-lapin/napt"
  vcsUrl = "https://github.com/sergei-lapin/napt.git"
  tags = listOf("kotlin", "java", "apt", "kapt")
  description = "Plugin that enables the work of NAPT javac plugin"
}
