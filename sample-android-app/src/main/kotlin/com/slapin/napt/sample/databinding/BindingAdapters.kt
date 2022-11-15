package com.slapin.napt.sample.databinding

import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("app:customText")
fun TextView.setTextCustom(text: String) {
  this.text = "$text\n(suffix from custom binding adapter)"
}
