package com.slapin.napt

import org.gradle.api.provider.Property

abstract class NaptGradleExtension {

    abstract val naptTriggerPackagePrefix: Property<String>
}
