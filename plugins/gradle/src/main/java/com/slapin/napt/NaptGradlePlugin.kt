package com.slapin.napt

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.TaskManager
import com.slapin.napt.task.CreateNaptTrigger
import java.io.File
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.plugins.ide.idea.GenerateIdeaModule
import org.gradle.plugins.ide.idea.model.IdeaModel
import org.gradle.process.CommandLineArgumentProvider
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

private const val CompilerPlugin = "io.github.sergei-lapin.napt:javac:1.2"
private const val AnnotationProcessor = "annotationProcessor"
private const val MainSourceSet = "main"

class NaptGradlePlugin : Plugin<Project> {

  override fun apply(target: Project) {
    with(target) {
      val extension = extensions.create("napt", NaptGradleExtension::class.java, objects)
      insertCompilerPluginDependency()
      notifyJavaCompilerAboutPlugin(extension)
      bindTriggerCreation(extension)
    }
  }

  private fun Project.insertCompilerPluginDependency() {
    val compilerPluginDependency = dependencies.create(CompilerPlugin)
    configurations.all { configuration ->
      if (configuration.name.contains(AnnotationProcessor, ignoreCase = true)) {
        configuration.dependencies.add(compilerPluginDependency)
      }
    }
  }

  private fun Project.bindTriggerCreation(extension: NaptGradleExtension) {
    extensions.findByType(BaseExtension::class.java)?.sourceSets?.configureEach { sourceSet ->
      val triggerDir = naptTriggerDirForSourceSet(sourceSet.name)
      val createTrigger =
        registerCreateTriggerTaskForSourceSet(
          sourceSetName = sourceSet.name,
          triggerDir = triggerDir,
          extension = extension,
        )
      registerTriggerDirInIdea(triggerDir.get().asFile)
      sourceSet.java.srcDirs(createTrigger.flatMap(CreateNaptTrigger::triggerDir))
      // setting srcDirs on AndroidSourceDirectorySet doesn't act the same way as with
      // java SourceSet (task action is not performed during provider resolving somehow)
      // so, we have to manually wire trigger generation to some early build stage
      tasks.named(TaskManager.MAIN_PREBUILD).configure { preBuild ->
        preBuild.dependsOn(createTrigger)
      }
    }
    extensions.findByType(SourceSetContainer::class.java)?.configureEach { sourceSet ->
      val triggerDir = naptTriggerDirForSourceSet(sourceSet.name)
      val createTrigger =
        registerCreateTriggerTaskForSourceSet(
          sourceSetName = sourceSet.name,
          triggerDir = triggerDir,
          extension = extension,
        )
      registerTriggerDirInIdea(triggerDir.get().asFile)
      sourceSet.java.srcDir(createTrigger.flatMap(CreateNaptTrigger::triggerDir))
    }
  }

  private fun Project.naptTriggerDirForSourceSet(sourceSetName: String): Provider<Directory> =
    layout.buildDirectory.dir("generated/source/napt_trigger/$sourceSetName")

  private fun Project.registerCreateTriggerTaskForSourceSet(
    sourceSetName: String,
    triggerDir: Provider<Directory>,
    extension: NaptGradleExtension,
  ): TaskProvider<CreateNaptTrigger> =
    tasks.register(
      "createNaptTrigger${sourceSetName.replaceFirstChar(Char::uppercaseChar)}",
      CreateNaptTrigger::class.java,
    ) { task ->
      task.triggerDir.set(triggerDir)
      task.projectName.set(name)
      task.packagePrefix.set(
        extension.naptTriggerPackagePrefix.orElse("").map { packagePrefix ->
          buildString {
            if (packagePrefix.isNotBlank()) {
              append(packagePrefix)
              append('.')
            }
            append(sourceSetName)
          }
        }
      )
      task.onlyIf {
        isNaptTriggerGenerationRequired(sourceSetName, extension) &&
          extension.generateNaptTrigger.get()
      }
      task.group = "napt"
      task.description =
        "Creates NaptTrigger.java for $sourceSetName source set in order to trigger java compilation"
    }

  private fun Project.registerTriggerDirInIdea(
    triggerDir: File,
  ) {
    project.plugins.withId("idea") {
      project.extensions.findByType(IdeaModel::class.java)?.module?.let { module ->
        module.sourceDirs = module.sourceDirs + triggerDir
        module.generatedSourceDirs = module.generatedSourceDirs + triggerDir
      }
      project.tasks.withType(GenerateIdeaModule::class.java).configureEach { task ->
        task.doFirst { triggerDir.mkdirs() }
      }
    }
  }

  private fun isNaptTriggerGenerationRequired(
    sourceSetName: String,
    extension: NaptGradleExtension,
  ): Boolean =
    sourceSetName == MainSourceSet ||
      sourceSetName in extension.additionalSourceSetsForTriggerGeneration.get()

  private fun Project.notifyJavaCompilerAboutPlugin(extension: NaptGradleExtension) {
    val rootProjectDir = rootDir
    tasks.withType(JavaCompile::class.java).configureEach { javaCompile ->
      javaCompile.options.compilerArgumentProviders.add(
        NaptCompilerArgumentsProvider(
          kotlinClassesDirPath =
            tasks
              .withType(KotlinCompile::class.java)
              .named(javaCompile.name.replace("JavaWithJavac", "Kotlin").replace("Java", "Kotlin"))
              .flatMap(KotlinCompile::destinationDirectory)
              .map { directory -> directory.asFile.relativeTo(rootProjectDir).path }
        )
      )
      javaCompile.options.compilerArgs.add("-Xplugin:Napt")
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
