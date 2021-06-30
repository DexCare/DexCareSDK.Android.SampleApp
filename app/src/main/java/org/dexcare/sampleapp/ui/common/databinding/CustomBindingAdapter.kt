package org.dexcare.sampleapp.ui.common.databinding

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.seatgeek.placesautocomplete.PlacesAutocompleteTextView
import org.dexcare.sampleapp.R
import org.dexcare.sampleapp.ui.retail.adapter.RetailTimeSlotAdapter
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

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

    @BindingAdapter("timeSlotsAdapter")
    @JvmStatic
    fun setTimeSlotsAdapter(recyclerView: RecyclerView, timeSlotsAdapter: RetailTimeSlotAdapter?) {
        recyclerView.adapter = timeSlotsAdapter
        recyclerView.layoutManager =
            LinearLayoutManager(recyclerView.context, RecyclerView.HORIZONTAL, false)
    }

    @BindingAdapter("setDay")
    @JvmStatic
    fun setDay(textView: TextView, startTime: ZonedDateTime? = null) {
        if (startTime == null) {
            textView.text = ""
            return
        }

        val day = startTime.dayOfMonth
        val today: Int
        val tomorrow: Int

        LocalDate.now().run {
            today = dayOfMonth
            tomorrow = dayOfMonth + 1
        }

        textView.text = when (day) {
            today -> textView.context.getString(R.string.today)
            tomorrow -> textView.context.getString(R.string.tomorrow)
            else -> DateTimeFormatter.ofPattern("E").format(startTime)
        }
    }
}
