plugins {
  id("org.jetbrains.kotlin.jvm")
  id("com.sergei-lapin.napt")
  id("com.google.devtools.ksp")
}

kotlin {
  jvmToolchain(11)
}

dependencies {
  implementation(libs.dagger)
  annotationProcessor(libs.daggerCompiler)

  api(libs.moshi)
  ksp(libs.moshi.codegen)
  // The issue will only show up once you have some ksp processor on the config
}
