package org.dexcare.sampleapp.ui.virtual.region

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.dexcare.DexCareSDK
import org.dexcare.exts.virtualService
import org.dexcare.sampleapp.ui.common.viewmodel.BaseViewModel
import org.dexcare.services.virtualvisit.models.Region
import timber.log.Timber

class VirtualRegionFragmentViewModel : BaseViewModel() {

    private val regions = MutableLiveData<List<Region>>()

    fun getRegions(brand: String): LiveData<List<Region>> {
        return regions.also {
            loadRegions(brand)
        }
    }

    private fun loadRegions(brand: String) {
        DexCareSDK.virtualService
            .getRegions(brand)
            .subscribe({
                regions.value = it
            }, {
                Timber.e(it)
            })
    }
}
