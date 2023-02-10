import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("org.jetbrains.kotlin.jvm")
  id("org.jetbrains.dokka")
  id("com.gradle.plugin-publish")
  id("java-gradle-plugin")
}

kotlin {
  jvmToolchain(11)
}

tasks.withType<JavaCompile> {
  sourceCompatibility = JavaVersion.VERSION_11.toString()
  targetCompatibility = JavaVersion.VERSION_11.toString()
}

java {
  withJavadocJar()
  withSourcesJar()
}

tasks.named<Jar>("javadocJar") { from(tasks.named("dokkaJavadoc")) }

dependencies {
  implementation(gradleApi())
  val kotlinVersion = libs.versions.kotlin.get()
  compileOnly("com.android.tools.build:gradle:${libs.versions.agp.get()}")
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
