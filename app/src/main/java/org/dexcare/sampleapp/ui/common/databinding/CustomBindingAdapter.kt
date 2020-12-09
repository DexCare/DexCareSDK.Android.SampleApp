package org.dexcare.sampleapp.ui.common.databinding

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.seatgeek.placesautocomplete.PlacesAutocompleteTextView
import org.dexcare.exts.toCalendar
import org.dexcare.sampleapp.R
import org.dexcare.sampleapp.ui.retail.adapter.RetailTimeSlotAdapter
import java.text.SimpleDateFormat
import java.util.*

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
    fun setDay(textView: TextView, startTime: Date? = null) {
        if (startTime == null) {
            textView.text = ""
            return
        }

        val day = startTime.toCalendar().get(Calendar.DAY_OF_MONTH)
        val today: Int
        val tomorrow: Int
        Date().toCalendar().run {
            today = get(Calendar.DAY_OF_MONTH)

            add(Calendar.DAY_OF_MONTH, 1)
            tomorrow = get(Calendar.DAY_OF_MONTH)
        }

        textView.text = when (day) {
            today -> textView.context.getString(R.string.today)
            tomorrow -> textView.context.getString(R.string.tomorrow)
            else -> SimpleDateFormat("E", Locale.getDefault()).format(startTime)
        }
    }
}
