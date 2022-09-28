package com.slapin.napt

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty

val JvmArgsStrongEncapsulation =
  listOf(
    "--add-opens=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED",
    "--add-opens=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
    "--add-opens=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED",
  )

open class NaptGradleExtension(objectFactory: ObjectFactory) {

  /**
   * Defines whether to generate NaptTrigger.java file in order to kick Java compilation in or not.
   * Defaults to true.
   */
  val generateNaptTrigger: Property<Boolean> =
    objectFactory.property(Boolean::class.java).convention(true)

  /**
   * Defines package prefix for generated NaptTrigger class. Might be useful if project contains
   * more than 1 equally named module. Default is not specified.
   */
  val naptTriggerPackagePrefix: Property<String> = objectFactory.property(String::class.java)

  /**
   * Defines additional JVM arguments for the compiler process.
   *
   * ```
   * napt {
   *     forkJvmArgs.set(listOf("your_custom_arg"))
   * }
   * ```
   */
  val forkJvmArgs: ListProperty<String> =
    objectFactory.listProperty(String::class.java).convention(emptyList())

  /**
   * By default, plugin will generate NaptTrigger.java only for the main source set, but there might
   * be cases when you need annotation processing for different source sets (e.g. test)
   *
   * For these cases you should specify these source sets explicitly via this property, e.g.
   *
   * ```
   * napt {
   *    additionalSourceSetsForTriggerGeneration.set(listOf("test", "androidTest"))
   * }
   * ```
   */
  val additionalSourceSetsForTriggerGeneration: SetProperty<String> =
    objectFactory.setProperty(String::class.java).convention(emptySet())
}
