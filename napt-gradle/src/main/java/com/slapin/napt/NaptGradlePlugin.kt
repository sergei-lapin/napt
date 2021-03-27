package com.slapin.napt

import com.slapin.napt.task.CleanNaptTrigger
import com.slapin.napt.task.CreateNaptTrigger
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.compile.JavaCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class NaptGradlePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val extension = target.extensions.create("napt", NaptGradleExtension::class.java)
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
        target.tasks.withType(JavaCompile::class.java).configureEach { javaCompile ->
            javaCompile.doFirst {
                javaCompile.options.compilerArgs.add("-Xplugin:Napt")
            }
        }
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