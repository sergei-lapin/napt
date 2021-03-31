package com.slapin.napt

import com.slapin.napt.task.CleanNaptTrigger
import com.slapin.napt.task.CreateNaptTrigger
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.compile.JavaCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

private const val COMPILER_PLUGIN = "com.github.sergei-lapin:napt:1.5"
private const val ANNOTATION_PROCESSOR_CONFIGURATION = "annotationProcessor"

class NaptGradlePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val extension = target.extensions.create("napt", NaptGradleExtension::class.java)
        insertCompilerPluginDependency(target)
        bindTriggerCreation(target, extension)
        notifyJavaCompilerAboutPlugin(target)
        bindTriggerCleaning(target)
    }

    private fun insertCompilerPluginDependency(target: Project) {
        val compilerPluginDependency = target.dependencies.create(COMPILER_PLUGIN)
        target.configurations.whenObjectAdded { configuration ->
            if (configuration.name.contains(ANNOTATION_PROCESSOR_CONFIGURATION, ignoreCase = true)) {
                configuration.dependencies.add(compilerPluginDependency)
            }
        }
    }

    private fun notifyJavaCompilerAboutPlugin(target: Project) {
        target.tasks.withType(JavaCompile::class.java).configureEach { javaCompile ->
            javaCompile.doFirst {
                javaCompile.options.compilerArgs.add("-Xplugin:Napt")
            }
        }
    }

    private fun bindTriggerCreation(
        target: Project,
        extension: NaptGradleExtension
    ) {
        val createTrigger = target.tasks.register(
            "createNaptTrigger",
            CreateNaptTrigger::class.java,
            target,
            extension.naptTriggerPackagePrefix
        )
        createTrigger.configure { task ->
            task.group = "napt"
            task.description = "Creates NaptTrigger.java in order to trigger NAPT javac plugin"
        }
        target.tasks.withType(KotlinCompile::class.java).configureEach { kotlinCompile ->
            kotlinCompile.dependsOn(createTrigger)
        }
    }

    private fun bindTriggerCleaning(target: Project) {
        val cleanTrigger = target.tasks.register(
            "cleanNaptTrigger",
            CleanNaptTrigger::class.java,
            target
        )
        cleanTrigger.configure { task ->
            task.group = "napt"
            task.description = "Removes NaptTrigger.java if present"
        }
        target.tasks.withType(Delete::class.java).configureEach { delete ->
            delete.dependsOn(cleanTrigger)
        }
    }
}