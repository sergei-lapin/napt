package com.slapin.napt

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

val JvmArgsStrongEncapsulation =
    listOf(
        "--add-opens=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED",
    )

abstract class NaptGradleExtension {

    /**
     * Defines whether to generate NaptTrigger.java file in order to kick Java compilation in or
     * not. Defaults to true.
     */
    abstract val generateNaptTrigger: Property<Boolean>

    /**
     * Defines package prefix for generated NaptTrigger class. Might be useful if project contains
     * more than 1 equally named module. Default is not specified.
     */
    abstract val naptTriggerPackagePrefix: Property<String>

    /**
     * Defines additional JVM arguments for the compiler process. Defaults to
     * [JvmArgsStrongEncapsulation].
     *
     * **Warning**: In case of setting this property defined value **must** include arguments
     * defined in [JvmArgsStrongEncapsulation], e.g.
     *
     * ```
     * napt {
     *     forkJvmArgs.set(listOf("your_custom_arg") + JvmArgsStrongEncapsulation)
     * }
     * ```
     */
    abstract val forkJvmArgs: ListProperty<String>
}
