package com.slapin.napt.sample.dagger

import dagger.BindsInstance
import dagger.Component

@Component
interface MainActivityComponent {

  val int: Int

  @Component.Factory
  interface Factory {

    fun create(@BindsInstance int: Int): MainActivityComponent
  }

  companion object {

    fun factory(): Factory = MainActivityComponentBridge.factory()
  }
}
