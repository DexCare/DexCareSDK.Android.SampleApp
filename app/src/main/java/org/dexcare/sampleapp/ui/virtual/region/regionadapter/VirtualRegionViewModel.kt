package org.dexcare.sampleapp.ui.virtual.region.regionadapter

import androidx.databinding.Bindable
import org.dexcare.sampleapp.BR
import org.dexcare.sampleapp.ui.common.viewmodel.BaseViewModel
import org.dexcare.services.virtualvisit.models.Region

class VirtualRegionViewModel(val region: Region? = null) : BaseViewModel() {

    @Bindable
    var regionName: String = region?.name ?: ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.regionName)
        }

    @Bindable
    var busy: Boolean = region?.availability?.busy ?: false
        set(value) {
            field = value
            notifyPropertyChanged(BR.busy)
        }
}
