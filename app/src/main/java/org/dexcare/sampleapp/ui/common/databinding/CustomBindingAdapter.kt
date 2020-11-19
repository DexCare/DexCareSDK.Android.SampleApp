package org.dexcare.sampleapp.ui.common.databinding

import android.graphics.drawable.Drawable
import android.view.View
import androidx.databinding.BindingAdapter
import com.seatgeek.placesautocomplete.PlacesAutocompleteTextView

object CustomBindingAdapter {
    @BindingAdapter("isVisible")
    @JvmStatic
    fun handleVisible(view: View, isVisible: Boolean) {
        view.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    @BindingAdapter("clearButton")
    @JvmStatic
    fun setClearButton(textView: PlacesAutocompleteTextView, drawable: Drawable) {
        textView.imgClearButton = drawable
    }

    @BindingAdapter("country")
    @JvmStatic
    fun setCountry(textView: PlacesAutocompleteTextView, country: String) {
        textView.setCountry(country)
    }
}
