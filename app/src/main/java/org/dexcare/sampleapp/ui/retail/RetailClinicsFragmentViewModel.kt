package org.dexcare.sampleapp.ui.retail

import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.dexcare.BR
import org.dexcare.DexCareSDK
import org.dexcare.sampleapp.ui.common.viewmodel.BaseViewModel
import org.dexcare.services.retail.models.Clinic
import org.dexcare.services.retail.models.ClinicTimeSlot
import timber.log.Timber
import java.util.concurrent.Semaphore

class RetailClinicsFragmentViewModel : BaseViewModel() {

    private val clinics = MutableLiveData<List<Clinic>>()
    private val timeSlots = MutableLiveData<List<ClinicTimeSlot>>()
    private val semaphore = Semaphore(1)

    fun getClinics(brand: String): LiveData<List<Clinic>> {
        return clinics.also {
            loadClinics(brand)
        }
    }

    private fun loadClinics(brand: String) {
        loading = true
        DexCareSDK.retailService
            .getClinics(brand)
            .subscribe({
                clinics.value = it.sortedBy { it.departmentName }
                loading = false
            }, {
                errorLiveData.value = it
                Timber.e(it)
            })
    }

    fun getTimeSlots() =
        timeSlots.also {
            loadTimeSlots()
        }

    private fun loadTimeSlots() {
        clinics.value?.forEach {
            DexCareSDK.retailService.getTimeSlots(it.departmentName)
                .subscribe({
                    semaphore.acquire()

                    val currentList = timeSlots.value?.toMutableList() ?: mutableListOf()
                    currentList.add(it)
                    timeSlots.value = currentList

                    semaphore.release()
                }, {
                    errorLiveData.value = it
                    Timber.e(it)
                })
        }
    }

    @Bindable
    var loading: Boolean = true
        set(value) {
            field = value
            notifyPropertyChanged(BR.loading)
        }
}
