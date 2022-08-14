package com.slapin.napt.task

import java.io.File
import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.internal.file.FileOperations
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault(because = "Not worth caching")
abstract class CreateNaptTrigger : DefaultTask() {

    @get:Inject abstract val providersFactory: ProviderFactory

    @get:Inject abstract val fileOperations: FileOperations

    @get:Input abstract val projectName: Property<String>

    @get:Input abstract val mainSourceSetJavaDirPath: Property<String>

    @get:Input @get:Optional abstract val packagePrefix: Property<String>

    @get:OutputFile
    val output: Provider<File>
        get() =
            providersFactory.provider {
                val javaDir = fileOperations.file(mainSourceSetJavaDirPath.get())
                File(javaDir, "NaptTrigger.java")
            }

    @TaskAction
    fun run() {
        val triggerCode =
            """
                        package ${getPackageName()};
                        class NaptTrigger {
                        }
                    """.trimIndent()
        val output = output.get()
        output.parentFile?.mkdirs()
        if (!output.exists()) output.createNewFile()
        output.writeText(triggerCode)
    }

    private fun getPackageName(): String {
        val packagePrefix = packagePrefix.orNull?.let { "$it." }.orEmpty()
        val projectName = projectName.get().replace("-", "_")
        return "$packagePrefix$projectName"
    }
}
