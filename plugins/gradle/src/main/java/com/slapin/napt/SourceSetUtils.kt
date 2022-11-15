package com.slapin.napt

import com.slapin.napt.task.CreateNaptTrigger
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider

private const val MainSourceSet = "main"

internal fun Project.configureNaptForSourceSet(
  sourceSetName: String,
  extension: NaptGradleExtension,
): TaskProvider<CreateNaptTrigger> {
  val triggerDir = getNaptTriggerDirForSourceSet(sourceSetName)
  val createTrigger =
    registerCreateTriggerTaskForSourceSet(
      sourceSetName = sourceSetName,
      triggerDir = triggerDir,
      extension = extension,
    )
  afterEvaluate { registerTriggerDirInIdea(triggerDir) }
  return createTrigger
}

private fun Project.getNaptTriggerDirForSourceSet(sourceSetName: String): Provider<Directory> =
  layout.buildDirectory.dir("generated/source/napt_trigger/$sourceSetName/java")

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
      shouldGenerateNaptTriggerForSourceSet(sourceSetName, extension) &&
        extension.generateNaptTrigger.get()
    }
    task.group = "napt"
    task.description =
      "Creates NaptTrigger.java for '$sourceSetName' source set in order to trigger java compilation"
  }

private fun shouldGenerateNaptTriggerForSourceSet(
  sourceSetName: String,
  extension: NaptGradleExtension,
): Boolean =
  sourceSetName == MainSourceSet ||
    sourceSetName in extension.additionalSourceSetsForTriggerGeneration.get()
