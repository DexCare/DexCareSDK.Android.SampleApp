package org.dexcare.sampleapp.ui.retail.adapter

import androidx.databinding.Bindable
import org.dexcare.sampleapp.BR
import org.dexcare.sampleapp.ui.common.databinding.CustomBindingAdapter
import org.dexcare.sampleapp.ui.common.viewmodel.BaseViewModel
import org.dexcare.services.retail.models.Clinic
import org.dexcare.services.retail.models.ClinicTimeSlot

class RetailClinicViewModel(val clinic: Clinic? = null) : BaseViewModel() {

    @Bindable
    var clinicTitle: String = clinic?.displayName ?: ""
    set(value) {
        field = value
        notifyPropertyChanged(BR.clinicTitle)
    }

    @Bindable
    var clinicTimeSlot: ClinicTimeSlot? = null
    set(value) {
        field = value
        clinicTimeSlot?.let { clinicTimeSlot ->
            val viewModels = clinicTimeSlot.scheduleDays.firstOrNull()?.timeSlots?.map {
                RetailTimeSlotViewModel(it, clinic)
            }?.toMutableList() ?: mutableListOf()
            timeSlotsAdapter = RetailTimeSlotAdapter(viewModels)
            loading = false
        }
        notifyPropertyChanged(BR.clinicTimeSlot)
    }

    @Bindable
    var timeSlotsAdapter: RetailTimeSlotAdapter? = null
    set(value) {
        field = value
        notifyPropertyChanged(BR.timeSlotsAdapter)
    }

    @Bindable
    var loading: Boolean = true
    set(value) {
        field = value
        notifyPropertyChanged(BR.loading)
    }
}
