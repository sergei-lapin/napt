package com.slapin.napt.task

import com.slapin.napt.ext.javaSourceDir
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

open class CleanNaptTrigger @Inject constructor(
    private val target: Project
) : DefaultTask() {

    @TaskAction
    fun run() {
        val trigger = File(target.javaSourceDir, "NaptTrigger.java")
        didWork = trigger.exists()
        if (trigger.exists()) trigger.delete()
    }
}