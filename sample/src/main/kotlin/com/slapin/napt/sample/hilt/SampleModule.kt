package com.slapin.napt.sample.hilt

import com.slapin.napt.sample.StringProvider
import com.slapin.napt.sample.StringProviderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
interface SampleModule {

    @Binds
    fun bind(provider: StringProviderImpl): StringProvider
}
