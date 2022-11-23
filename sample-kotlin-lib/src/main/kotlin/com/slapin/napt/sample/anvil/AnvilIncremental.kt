package com.slapin.napt.sample.anvil

import com.squareup.anvil.annotations.MergeComponent
import javax.inject.Singleton

@MergeComponent(Singleton::class)
interface AnvilComponent {

  val deps: Set<@JvmSuppressWildcards SomeDep>
}

interface SomeDep
