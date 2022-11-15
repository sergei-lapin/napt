package com.slapin.napt

import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.plugins.ide.idea.GenerateIdeaModule
import org.gradle.plugins.ide.idea.model.IdeaModel

internal fun Project.registerTriggerDirInIdea(
  triggerDir: Provider<Directory>,
) {
  plugins.withId("idea") {
    val triggerDirFile = triggerDir.get().asFile
    extensions.findByType(IdeaModel::class.java)?.module?.let { module ->
      module.sourceDirs.plusAssign(triggerDirFile)
      module.generatedSourceDirs.plusAssign(triggerDirFile)
    }
    tasks.withType(GenerateIdeaModule::class.java).forEach { task ->
      task.doFirst { triggerDirFile.mkdirs() }
    }
  }
}
