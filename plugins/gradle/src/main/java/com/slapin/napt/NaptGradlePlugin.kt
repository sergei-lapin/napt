package com.slapin.napt

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.TaskManager
import com.slapin.napt.task.CreateNaptTrigger
import java.io.File
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.plugins.ide.idea.GenerateIdeaModule
import org.gradle.plugins.ide.idea.model.IdeaModel

private const val CompilerPlugin = "io.github.sergei-lapin.napt:javac:1.1"
private const val AnnotationProcessor = "annotationProcessor"
private const val MainSourceSet = "main"

class NaptGradlePlugin : Plugin<Project> {

  override fun apply(target: Project) {
    with(target) {
      val extension = extensions.create("napt", NaptGradleExtension::class.java, objects)
      insertCompilerPluginDependency()
      bindTriggerCreation(extension)
      notifyJavaCompilerAboutPlugin(extension)
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
    if (!extension.generateNaptTrigger.get()) return
    afterEvaluate {
      extensions.findByType(BaseExtension::class.java)?.sourceSets?.configureEach { sourceSet ->
        if (isNaptTriggerGenerationRequired(sourceSet.name, extension)) {
          val triggerDir = naptTriggerDirForSourceSet(sourceSet.name)
          val createTrigger =
            registerCreateTriggerTaskForSourceSet(
              sourceSetName = sourceSet.name,
              triggerDir = triggerDir,
              extension = extension,
            )
          registerTriggerDirInIdea(triggerDir.get().asFile)
          sourceSet.java.srcDir(triggerDir.get().asFile)
          // setting srcDirs on AndroidSourceDirectorySet doesn't act the same way as with
          // java SourceSet (task action is not performed during provider resolving somehow)
          // so, we have to manually wire trigger generation to some early build stage
          tasks.named(TaskManager.MAIN_PREBUILD).configure { preBuild ->
            preBuild.dependsOn(createTrigger)
          }
        }
      }
      extensions.findByType(SourceSetContainer::class.java)?.configureEach { sourceSet ->
        if (isNaptTriggerGenerationRequired(sourceSet.name, extension)) {
          val triggerDir = naptTriggerDirForSourceSet(sourceSet.name)
          val createTrigger =
            registerCreateTriggerTaskForSourceSet(
              sourceSetName = sourceSet.name,
              triggerDir = triggerDir,
              extension = extension,
            )
          registerTriggerDirInIdea(triggerDir.get().asFile)
          sourceSet.java.srcDir(createTrigger.flatMap { it.triggerDir })
        }
      }
    }
  }

  private fun Project.naptTriggerDirForSourceSet(sourceSetName: String): Provider<Directory> =
    layout.buildDirectory.dir("generated/napt_trigger/$sourceSetName/out")

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
    tasks.withType(JavaCompile::class.java).configureEach { javaCompile ->
      javaCompile.options.compilerArgs.add("-Xplugin:Napt")
      javaCompile.options.isFork = true
      requireNotNull(javaCompile.options.forkOptions.jvmArgs)
        .addAll((extension.forkJvmArgs.get() + JvmArgsStrongEncapsulation).toSet())
    }
  }
}
