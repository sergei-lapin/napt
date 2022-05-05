package com.slapin.napt.task

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.*

abstract class CreateNaptTrigger : DefaultTask() {

    @get:Input abstract val projectName: Property<String>

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val mainSourceSetDir: DirectoryProperty

    @get:Input @get:Optional abstract val packagePrefix: Property<String>

    @get:OutputFile
    val output: Provider<RegularFile>
        get() = mainSourceSetDir.file("java/NaptTrigger.java")

    @TaskAction
    fun run() {
        val triggerCode =
            """
                        package ${getPackageName()};
                        class NaptTrigger {
                        }
                    """.trimIndent()
        val output = output.get().asFile
        output.parentFile?.mkdirs()
        output.createNewFile()
        output.writeText(triggerCode)
    }

    private fun getPackageName(): String {
        val packagePrefix = packagePrefix.orNull?.let { "$it." }.orEmpty()
        val projectName = projectName.get().replace("-", "_")
        return "$packagePrefix$projectName"
    }
}
