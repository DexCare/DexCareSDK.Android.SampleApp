package org.dexcare.sampleapp.ui.common.viewmodel

import androidx.databinding.Bindable
import org.dexcare.sampleapp.BR
import org.dexcare.services.retail.models.TimeSlot
import java.time.format.DateTimeFormatter

abstract class BaseTimeSlotViewModel(
    timeSlot: TimeSlot? = null
) : BaseViewModel() {

    private val formatter = DateTimeFormatter.ofPattern("hh:mm a")

    @Bindable
    var startTime: String = timeSlot?.slotDateTime?.format(formatter) ?: ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.startTime)
        }
}
