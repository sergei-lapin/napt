package com.slapin.napt.ext

import org.gradle.api.Project
import java.io.File

inline val Project.javaSourceDir: File
    get() {
        val sourceDir = file("src/main/java")
        if (!sourceDir.exists()) sourceDir.mkdirs()
        return sourceDir
    }
