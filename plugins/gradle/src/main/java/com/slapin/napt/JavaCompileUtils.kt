package com.slapin.napt

import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.process.CommandLineArgumentProvider
import java.io.File

internal fun Project.configureJavaCompileTasks(
  kotlinClassesDir: Provider<Directory>,
  extension: NaptGradleExtension,
) {
  val rootDir = rootDir
  tasks.withType(JavaCompile::class.java).configureEach { javaCompile ->
    javaCompile.options.compilerArgumentProviders.add(
      NaptCompilerArgumentsProvider(
        kotlinClassesDirPath = kotlinClassesDir.map { it.asFile.relativeTo(rootDir).toString() },
      )
    )
    javaCompile.options.isFork = true
    @Suppress("UnstableApiUsage")
    javaCompile.options.forkOptions.jvmArgumentProviders.add(
      NaptForkOptionsJvmArgumentsProvider(
        forkJvmArgs = extension.forkJvmArgs,
        jvmArgsStrongEncapsulation =
          objects.listProperty(String::class.java).convention(JvmArgsStrongEncapsulation),
      )
    )
  }
}

private class NaptCompilerArgumentsProvider(
  @get:Input val kotlinClassesDirPath: Provider<String>,
) : CommandLineArgumentProvider {

  override fun asArguments(): Iterable<String> {
    return listOf(
      "-Xplugin:Napt",
      "-XDKotlinClassesDir=${File(kotlinClassesDirPath.get())}",
    )
  }
}

private class NaptForkOptionsJvmArgumentsProvider(
  @get:Input val forkJvmArgs: ListProperty<String>,
  @get:Input val jvmArgsStrongEncapsulation: ListProperty<String>,
) : CommandLineArgumentProvider {

  override fun asArguments(): Iterable<String> {
    return (forkJvmArgs.get() + jvmArgsStrongEncapsulation.get()).toSet()
  }
}
