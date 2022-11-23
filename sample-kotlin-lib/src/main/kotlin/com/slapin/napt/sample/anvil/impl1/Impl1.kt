package com.slapin.napt.sample.anvil.impl1

import com.slapin.napt.sample.anvil.SomeDep
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject
import javax.inject.Singleton

@ContributesMultibinding(Singleton::class) class SomeDepImpl1 @Inject constructor() : SomeDep
