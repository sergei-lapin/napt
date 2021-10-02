package com.slapin.napt.task

import com.slapin.napt.ext.javaSourceDir
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import java.io.File
import javax.inject.Inject

open class CreateNaptTrigger @Inject constructor(
    private val target: Project,
    @get:Input @get:Optional val packagePrefix: Property<String>
) : DefaultTask() {

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    val input = target.javaSourceDir

    @get:OutputFile
    val output = File(target.javaSourceDir, "NaptTrigger.java")

    @TaskAction
    fun run() {
        val packagePrefix = packagePrefix.orNull?.let { "$it." } ?: ""
        val triggerCode = """
                        package ${getPackageName(packagePrefix, target)};
                        class NaptTrigger {
                        }
                    """.trimIndent()
        output.writeText(triggerCode)
    }

    private fun getPackageName(packagePrefix: String, project: Project): String {
        val projectName = project.name.replace("-", "_")
        return "${packagePrefix}${projectName}"
    }
}