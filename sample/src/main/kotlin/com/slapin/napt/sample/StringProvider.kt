package com.slapin.napt.sample

import java.util.*
import javax.inject.Inject

interface StringProvider {

  fun get(): String
}

class StringProviderImpl @Inject constructor() : StringProvider {

  override fun get(): String = UUID.randomUUID().toString()
}
