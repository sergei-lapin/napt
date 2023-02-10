package com.slapin.napt.sample

import dagger.Binds
import dagger.Module
import javax.inject.Inject
import kotlin.random.Random
import com.squareup.moshi.JsonClass

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

@JsonClass(generateAdapter = true)
data class BasicJsonClass(
  val someValue: String,
)
