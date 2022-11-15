package com.slapin.napt.sample

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.slapin.napt.sample.dagger.MainActivityComponent
import com.slapin.napt.sample.databinding.ActivityMainBinding
import dagger.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

  @Inject lateinit var stringProvider: StringProvider

  @SuppressLint("SetTextI18n")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
    val component = MainActivityComponent.factory().create(Random.nextInt())
    binding.labelText =
      "Created dagger component $component from Kotlin sources without using KAPT!!!\n" +
        "Providing ${component.int} from it.\n" +
        "String from hilt code ${stringProvider.get()}."
  }
}
