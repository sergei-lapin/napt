package com.slapin.napt.sample

import android.annotation.SuppressLint
import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import dagger.BindsInstance
import dagger.Component

@Component
interface MainActivityComponent {

    val activity: Activity

    @Component.Factory
    interface Factory {

        fun create(@BindsInstance activity: Activity): MainActivityComponent
    }

    companion object {

        fun factory(): Factory = MainActivityComponentBridge.factory()
    }
}

class MainActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val textView = findViewById<TextView>(R.id.text)
        val component = MainActivityComponent.factory().create(this);
        textView.text = "Created dagger component $component from Kotlin sources without using KAPT!!!\n" +
                "Providing ${component.activity} from it"
    }
}