package com.slapin.napt.task

import java.io.File
import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.internal.file.FileOperations
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

abstract class CleanNaptTrigger : DefaultTask() {

    @get:Inject abstract val fileOperations: FileOperations

    @get:Input abstract val mainSourceSetJavaDirPath: Property<String>

    @TaskAction
    fun run() {
        val trigger = File(fileOperations.file(mainSourceSetJavaDirPath.get()), "NaptTrigger.java")
        didWork = trigger.exists()
        if (trigger.exists()) trigger.delete()
    }
}
