package com.slapin.napt.sample.hilt

import android.app.Application
import android.util.Log
import com.slapin.napt.sample.StringProvider
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

  @Inject lateinit var stringProvider: StringProvider

  override fun onCreate() {
    super.onCreate()
    Log.d("NaptSample", stringProvider.get())
  }
}
