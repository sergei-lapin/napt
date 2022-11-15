package com.slapin.napt

import org.gradle.api.Project

private const val CompilerPlugin = "io.github.sergei-lapin.napt:javac:1.2"
private const val AnnotationProcessor = "annotationProcessor"

internal fun Project.configureCompilerPluginDependency() {
  val compilerPluginDependency = dependencies.create(CompilerPlugin)
  configurations.all { configuration ->
    if (configuration.name.contains(AnnotationProcessor, ignoreCase = true)) {
      configuration.dependencies.add(compilerPluginDependency)
    }
  }
}
