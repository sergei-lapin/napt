package com.slapin.napt.sample

import dagger.Binds
import dagger.Module
import javax.inject.Inject
import kotlin.random.Random

interface LongProvider {

  fun get(): Long
}

class LongProviderImpl @Inject constructor() : LongProvider {

  override fun get(): Long = Random.nextLong()
}

@Module
interface LongProviderModule {

  @Binds fun LongProviderImpl.bind(): LongProvider
}
