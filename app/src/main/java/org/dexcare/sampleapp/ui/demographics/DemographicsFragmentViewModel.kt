package org.dexcare.sampleapp.ui.demographics

import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import org.dexcare.sampleapp.ui.common.viewmodel.BaseViewModel
import org.koin.core.component.inject

class DemographicsFragmentViewModel : BaseViewModel() {
    @get:Bindable
    // This is the patient for "myself" visits, the actor for "someone else" visits.
    val appUserDemographics: DemographicsViewModel by inject()

    @get:Bindable
    // This is the patient for "someone else" visits.  Not used for "myself" visits.
    val someoneElseDemographics: DemographicsViewModel by inject()

    @Bindable
    var loading: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.loading)
        }
}
