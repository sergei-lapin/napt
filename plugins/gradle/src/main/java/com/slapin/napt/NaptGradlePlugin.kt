package com.slapin.napt

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.TaskManager
import com.slapin.napt.task.CreateNaptTrigger
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

class NaptGradlePlugin : Plugin<Project> {

  override fun apply(target: Project) {
    with(target) {
      val extension = extensions.create("napt", NaptGradleExtension::class.java, objects)
      plugins.withId("kotlin") { applyNaptToJvmProject(extension) }
      plugins.withId("kotlin-android") { applyNaptToAndroidProject(extension) }
    }
  }

  private fun Project.applyNaptToJvmProject(extension: NaptGradleExtension) {
    extensions.getByType(KotlinProjectExtension::class.java).sourceSets.all { kotlinSourceSet ->
      val javaSourceSet =
        extensions.getByType(SourceSetContainer::class.java).maybeCreate(kotlinSourceSet.name)
      val createTrigger =
        configureNaptForSourceSet(
          sourceSetName = javaSourceSet.name,
          extension = extension,
        )
      javaSourceSet.java.srcDir(createTrigger.flatMap(CreateNaptTrigger::triggerDir))
    }
    configureJavaCompileTasks(
      kotlinClassesDir = layout.buildDirectory.dir("classes/kotlin"),
      extension = extension,
    )
    configureCompilerPluginDependency()
  }

  private fun Project.applyNaptToAndroidProject(extension: NaptGradleExtension) {
    fun Project.applyNaptForAndroid() {
      extensions.findByType(BaseExtension::class.java)?.sourceSets?.all { androidSourceSet ->
        val createTrigger =
          configureNaptForSourceSet(
            sourceSetName = androidSourceSet.name,
            extension = extension,
          )
        androidSourceSet.java.srcDirs(createTrigger.flatMap(CreateNaptTrigger::triggerDir))
        // setting srcDirs on AndroidSourceDirectorySet doesn't act the same way as with
        // java SourceSet (task action is not performed during provider resolving somehow)
        // so, we have to manually wire trigger generation to some early build stage
        tasks.named(TaskManager.MAIN_PREBUILD).configure { preBuild ->
          preBuild.dependsOn(createTrigger)
        }
      }
      configureJavaCompileTasks(
        kotlinClassesDir = layout.buildDirectory.dir("tmp/kotlin-classes"),
        extension = extension,
      )
    }
    plugins.withId("com.android.application") { applyNaptForAndroid() }
    plugins.withId("com.android.library") { applyNaptForAndroid() }
    plugins.withId("com.android.test") { applyNaptForAndroid() }
    plugins.withId("com.android.dynamic-feature") { applyNaptForAndroid() }
    configureCompilerPluginDependency()
  }
}
