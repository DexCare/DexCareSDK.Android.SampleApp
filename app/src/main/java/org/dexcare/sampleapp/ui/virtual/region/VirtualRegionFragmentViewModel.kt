package org.dexcare.sampleapp.ui.virtual.region

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.dexcare.DexCareSDK
import org.dexcare.sampleapp.ui.common.viewmodel.BaseViewModel
import org.dexcare.services.virtualvisit.models.VirtualPractice
import timber.log.Timber

class VirtualRegionFragmentViewModel : BaseViewModel() {

    private val virtualPractice = MutableLiveData<VirtualPractice>()

    fun getVirtualPractice(practiceId: String): LiveData<VirtualPractice> {
        return virtualPractice.also {
            loadVirtualPractice(practiceId)
        }
    }

    private fun loadVirtualPractice(practiceId: String) {
        DexCareSDK.practiceService
            .getVirtualPractice(practiceId)
            .subscribe({
                virtualPractice.value = it
            }, {
                Timber.e(it)
            })
    }
}
