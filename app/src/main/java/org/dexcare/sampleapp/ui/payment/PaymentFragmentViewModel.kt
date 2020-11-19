package org.dexcare.sampleapp.ui.payment

import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.dexcare.DexCareSDK
import org.dexcare.exts.virtualService
import org.dexcare.sampleapp.ui.common.viewmodel.BaseViewModel
import org.dexcare.services.models.InsurancePayer
import timber.log.Timber

class PaymentFragmentViewModel : BaseViewModel() {

    @Bindable
    var insuranceMemberId: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.insuranceMemberId)
        }

    @Bindable
    var insuranceProviderName: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.insuranceProviderName)
        }

    private val insuranceProviders =
        MutableLiveData<List<InsurancePayer>>()

    fun getInsuranceProviders(brand: String): LiveData<List<InsurancePayer>> {
        return insuranceProviders.also {
            loadInsuranceProviders(brand)
        }
    }

    private fun loadInsuranceProviders(brand: String) {
        DexCareSDK.virtualService
            .getInsurancePayers(brand)
            .subscribe({
                insuranceProviders.value = it
            }, {
                Timber.e(it)
            })
    }
}
