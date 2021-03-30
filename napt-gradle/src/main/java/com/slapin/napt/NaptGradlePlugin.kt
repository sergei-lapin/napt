package com.slapin.napt

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile

class NaptGradlePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.tasks.withType(JavaCompile::class.java).configureEach { javaCompile ->
            javaCompile.doFirst {
                javaCompile.options.compilerArgs.add("-Xplugin:Napt")
            }
        }
    }
}