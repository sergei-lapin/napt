package com.slapin.napt

import com.slapin.napt.task.CleanNaptTrigger
import com.slapin.napt.task.CreateNaptTrigger
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.compile.JavaCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

private const val CompilerPlugin = "io.github.sergei-lapin.napt:javac:1.0-SNAPSHOT"
private const val AnnotationProcessor = "annotationProcessor"

class NaptGradlePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            val extension = extensions.create("napt", NaptGradleExtension::class.java)
            insertCompilerPluginDependency()
            bindTriggerCreation(extension)
            notifyJavaCompilerAboutPlugin()
            bindTriggerCleaning()
        }
    }

    private fun Project.insertCompilerPluginDependency() {
        val compilerPluginDependency = dependencies.create(CompilerPlugin)
        configurations.whenObjectAdded { configuration ->
            if (configuration.name.contains(AnnotationProcessor, ignoreCase = true)) {
                configuration.dependencies.add(compilerPluginDependency)
            }
        }
    }

    private fun Project.bindTriggerCreation(extension: NaptGradleExtension) {
        val createTrigger =
            tasks.register(
                "createNaptTrigger",
                CreateNaptTrigger::class.java,
            ) { task ->
                task.javaDirectory.set(layout.projectDirectory.dir("src/main/java"))
                task.projectName.set(name)
                task.packagePrefix.set(extension.naptTriggerPackagePrefix)
                task.group = "napt"
                task.description = "Creates NaptTrigger.java in order to trigger NAPT javac plugin"
            }
        tasks.withType(KotlinCompile::class.java).configureEach { kotlinCompile ->
            kotlinCompile.dependsOn(createTrigger)
        }
    }

    private fun Project.notifyJavaCompilerAboutPlugin() {
        tasks.withType(JavaCompile::class.java).configureEach { javaCompile ->
            javaCompile.options.compilerArgs.add("-Xplugin:Napt")
        }
    }
    private fun Project.bindTriggerCleaning() {
        val cleanTrigger =
            tasks.register(
                "cleanNaptTrigger",
                CleanNaptTrigger::class.java,
            ) { task ->
                task.javaDirectory.set(layout.projectDirectory.dir("src/main/java"))
                task.group = "napt"
                task.description = "Removes NaptTrigger.java if present"
            }
        tasks.withType(Delete::class.java).configureEach { delete ->
            if (delete.name == "clean") delete.dependsOn(cleanTrigger)
        }
    }
}
