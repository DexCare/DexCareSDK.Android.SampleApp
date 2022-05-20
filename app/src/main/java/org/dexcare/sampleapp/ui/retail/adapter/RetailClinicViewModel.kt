package org.dexcare.sampleapp.ui.retail.adapter

import androidx.databinding.Bindable
import org.dexcare.sampleapp.BR
import org.dexcare.sampleapp.ui.common.viewmodel.BaseViewModel
import org.dexcare.services.retail.models.RetailAppointmentTimeSlot
import org.dexcare.services.retail.models.RetailDepartment
import org.koin.core.component.get

class RetailClinicViewModel(val clinic: RetailDepartment? = null) : BaseViewModel() {

    @Bindable
    var clinicTitle: String = clinic?.displayName ?: ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.clinicTitle)
        }

    @Bindable
    var retailAppointmentTimeSlot: RetailAppointmentTimeSlot? = null
        set(value) {
            field = value
            retailAppointmentTimeSlot?.let { clinicTimeSlot ->
                val viewModels = clinicTimeSlot.scheduleDays.firstOrNull()?.timeSlots?.map {
                    RetailTimeSlotViewModel(it, clinic)
                }?.toMutableList() ?: mutableListOf()
                timeSlotsAdapter = RetailTimeSlotAdapter(
                    get(),
                    viewModels
                )
                loading = false
            }
            notifyPropertyChanged(BR.retailAppointmentTimeSlot)
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
