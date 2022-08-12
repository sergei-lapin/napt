package com.slapin.napt

import org.gradle.api.provider.Property

abstract class NaptGradleExtension {

    /**
     * Defines whether to generate NaptTrigger.java file in order to kick Java compilation in or
     * not. Defaults to true.
     */
    abstract val generateNaptTrigger: Property<Boolean>

    /**
     * Defines package prefix for generated NaptTrigger class. Might be useful if project contains
     * more than 1 equally named module. Defaults to null.
     */
    abstract val naptTriggerPackagePrefix: Property<String>
}
