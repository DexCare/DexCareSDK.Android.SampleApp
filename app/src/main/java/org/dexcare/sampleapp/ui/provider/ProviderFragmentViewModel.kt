package org.dexcare.sampleapp.ui.provider

import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.dexcare.BR
import org.dexcare.DexCareSDK
import org.dexcare.sampleapp.ui.common.viewmodel.BaseViewModel
import org.dexcare.services.provider.models.Provider
import org.dexcare.services.provider.models.ProviderTimeSlot
import timber.log.Timber
import java.util.*

class ProviderFragmentViewModel : BaseViewModel() {

    // This could be expanded to show a list of Providers, but for the example we're only doing one provider.
    private val providerLiveData = MutableLiveData<Provider>()
    private val timeSlots = MutableLiveData<ProviderTimeSlot>()

    fun getProvider(providerNationalId: String): LiveData<Provider> {
        return providerLiveData.also {
            loadProvider(providerNationalId)
        }
    }

    private fun loadProvider(providerNationalId: String) {
        loading = true
        DexCareSDK.providerService
            .getProvider(providerNationalId)
            .subscribe({
                providerLiveData.value = it
                loading = false
            }, {
                errorLiveData.value = it
                Timber.e(it)
                loading = false
            })
    }

    fun getProviderTimeSlots() =
        timeSlots.also {
            getMaxLookaheadDaysAndLoadTimeSlots()
        }

    private fun getMaxLookaheadDaysAndLoadTimeSlots() {
        providerLiveData.value?.let {

            // Visit types could also be displayed and selected by the user
            val visitType = it.visitTypes.firstOrNull { it.shortName == "NewPatient" }
                ?: it.visitTypes.first()

            val visitTypeShortName = visitType.shortName ?: return

            loading = true
            DexCareSDK.providerService.getMaxLookaheadDays(
                visitTypeShortName,
                it.departments.first().ehrSystemName
            )
                .subscribe({ maxLookaheadDays ->

                    val newEndDate = Calendar.getInstance().apply {
                        // Only dividing by three to get less data
                        add(Calendar.DAY_OF_YEAR, maxLookaheadDays / 3)
                    }.time

                    DexCareSDK.providerService.getProviderTimeslots(
                        it.providerNationalId,
                        visitType.visitTypeId,
                        endDate = newEndDate
                    ).subscribe({
                        timeSlots.value = it
                        loading = false
                    }, {
                        errorLiveData.value = it
                        Timber.e(it)
                        loading = false
                    })
                }, {
                    errorLiveData.value = it
                    Timber.e(it)
                    loading = false
                })


        }
    }

    @Bindable
    var loading: Boolean = true
        set(value) {
            field = value
            notifyPropertyChanged(BR.loading)
        }

    @Bindable
    var providerName: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.providerName)
        }

    @Bindable
    var timeSlotDate: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.timeSlotDate)
        }
}
