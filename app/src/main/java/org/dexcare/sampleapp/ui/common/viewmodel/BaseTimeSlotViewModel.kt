package org.dexcare.sampleapp.ui.common.viewmodel

import androidx.databinding.Bindable
import org.dexcare.sampleapp.BR
import org.dexcare.services.retail.models.TimeSlot
import java.text.SimpleDateFormat
import java.util.*

abstract class BaseTimeSlotViewModel(
    timeSlot: TimeSlot? = null
) : BaseViewModel() {
    private val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())

    @Bindable
    var startTime: String = timeSlot?.slotDateTime?.let {
        formatter.format(it)
    } ?: ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.startTime)
        }
}
