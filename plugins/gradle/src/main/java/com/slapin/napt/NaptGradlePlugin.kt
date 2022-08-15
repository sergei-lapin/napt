package com.slapin.napt

import com.slapin.napt.task.CleanNaptTrigger
import com.slapin.napt.task.CreateNaptTrigger
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.compile.JavaCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

private const val CompilerPlugin = "io.github.sergei-lapin.napt:javac:1.1"
private const val AnnotationProcessor = "annotationProcessor"

class NaptGradlePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            val extension = extensions.create("napt", NaptGradleExtension::class.java)
            extension.defineDefaults()
            insertCompilerPluginDependency()
            bindTriggerCreation(extension)
            notifyJavaCompilerAboutPlugin(extension)
            bindTriggerCleaning()
        }
    }

    private fun NaptGradleExtension.defineDefaults() {
        generateNaptTrigger.convention(true)
        forkJvmArgs.convention(JvmArgsStrongEncapsulation)
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
        val createTrigger =
            tasks.register(
                "createNaptTrigger",
                CreateNaptTrigger::class.java,
            ) { task ->
                task.mainSourceSetJavaDirPath.set(
                    layout.projectDirectory.dir("src/main/java").asFile.path
                )
                task.projectName.set(name)
                task.packagePrefix.set(extension.naptTriggerPackagePrefix)
                task.onlyIf { extension.generateNaptTrigger.get() }
                task.group = "napt"
                task.description = "Creates NaptTrigger.java in order to trigger NAPT javac plugin"
            }
        tasks.withType(KotlinCompile::class.java).configureEach { kotlinCompile ->
            kotlinCompile.dependsOn(createTrigger)
        }
    }

    private fun Project.notifyJavaCompilerAboutPlugin(extension: NaptGradleExtension) {
        tasks.withType(JavaCompile::class.java).configureEach { javaCompile ->
            javaCompile.options.compilerArgs.add("-Xplugin:Napt")
            javaCompile.options.isFork = true
            requireNotNull(javaCompile.options.forkOptions.jvmArgs)
                .addAll(extension.forkJvmArgs.get())
        }
    }

    private fun Project.bindTriggerCleaning() {
        val cleanTrigger =
            tasks.register(
                "cleanNaptTrigger",
                CleanNaptTrigger::class.java,
            ) { task ->
                task.mainSourceSetJavaDirPath.set(
                    layout.projectDirectory.dir("src/main/java").asFile.path
                )
                task.group = "napt"
                task.description = "Removes NaptTrigger.java if present"
            }
        tasks.withType(Delete::class.java).configureEach { delete ->
            if (delete.name == "clean") delete.dependsOn(cleanTrigger)
        }
    }
}
