package org.dexcare.sampleapp.ui.payment

import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.dexcare.DexCareSDK
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

    @Bindable
    var couponCode: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.couponCode)
        }

    @Bindable
    var discountAmount: String = "$0.00"
        set(value) {
            field = value
            notifyPropertyChanged(BR.discountAmount)
        }

    @Bindable
    var verifyCouponCodeLoading: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.verifyCouponCodeLoading)
        }

    @Bindable
    var loading: Boolean = false
    set(value) {
        field = value
        notifyPropertyChanged(BR.loading)
    }

    private val insuranceProviders =
        MutableLiveData<List<InsurancePayer>>()

    fun getInsuranceProviders(brand: String): LiveData<List<InsurancePayer>> {
        return insuranceProviders.also {
            loadInsuranceProviders(brand)
        }
    }

    private fun loadInsuranceProviders(brand: String) {
        DexCareSDK.paymentService
            .getInsurancePayers(brand)
            .subscribe({
                insuranceProviders.value = it
            }, {
                Timber.e(it)
                errorLiveData.value = it
            })
    }
}