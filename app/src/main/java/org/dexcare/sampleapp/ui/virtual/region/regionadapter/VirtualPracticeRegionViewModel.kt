package org.dexcare.sampleapp.ui.virtual.region.regionadapter

import androidx.databinding.Bindable
import org.dexcare.sampleapp.BR
import org.dexcare.sampleapp.ui.common.viewmodel.BaseViewModel
import org.dexcare.services.virtualvisit.models.VirtualPracticeRegion

class VirtualPracticeRegionViewModel(val region: VirtualPracticeRegion? = null) : BaseViewModel() {

    @Bindable
    var regionName: String = region?.displayName ?: ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.regionName)
        }

    @Bindable
    var busy: Boolean = region?.busy ?: false
        set(value) {
            field = value
            notifyPropertyChanged(BR.busy)
        }
}
