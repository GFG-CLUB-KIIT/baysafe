package com.gfg.kiit.baysafe

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.gfg.kiit.baysafe.viewmodel.SharedViewModel
import com.google.android.material.slider.Slider

@BindingAdapter("updateSliderValueTextView","getGeoRadius", requireAll = true)
fun Slider.updateSliderValue(textView: TextView, sharedViewModel: SharedViewModel)
{
    this.addOnChangeListener{ _, value,_ ->
            sharedViewModel.geoRadius=value
        updateSliderValueTextView(sharedViewModel.geoRadius,textView)
    }
}

fun updateSliderValueTextView(geoRadius: Float, textView: TextView) {
    val radius=geoRadius.toString()+"km"
    textView.text = radius
}
