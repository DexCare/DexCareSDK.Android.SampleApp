package org.dexcare.sampleapp.ui.retail.adapter

import androidx.databinding.Bindable
import org.dexcare.sampleapp.BR
import org.dexcare.sampleapp.ui.common.viewmodel.BaseViewModel
import org.dexcare.services.retail.models.Clinic
import org.dexcare.services.retail.models.TimeSlot
import java.text.SimpleDateFormat
import java.util.*

class RetailTimeSlotViewModel(
    val timeSlot: TimeSlot? = null,
    val clinic: Clinic? = null
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
