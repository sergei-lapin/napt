package com.slapin.napt.task

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault(because = "Not worth caching")
abstract class CreateNaptTrigger : DefaultTask() {

  @get:Input abstract val projectName: Property<String>

  @get:Input @get:Optional abstract val packagePrefix: Property<String>

  @get:OutputDirectory abstract val triggerDir: DirectoryProperty

  @TaskAction
  fun run() {
    val packageName = getPackageName()
    val triggerCode =
      """
            package $packageName;
            
            class NaptTrigger {}
            """.trimIndent(
      )
    val outputFile =
      triggerDir.get().asFile.resolve("${packageName.replace('.', '/')}/NaptTrigger.java")
    outputFile.parentFile?.mkdirs()
    outputFile.writeText(triggerCode)
  }

  private fun getPackageName(): String {
    val packagePrefix = packagePrefix.orNull?.let { "$it." }.orEmpty()
    val projectName = projectName.get().replace("-", "_")
    return "$packagePrefix$projectName"
  }
}
