package org.dexcare.sampleapp.ui.reasonforvisit

import androidx.databinding.Bindable
import org.dexcare.sampleapp.BR
import org.dexcare.sampleapp.ui.common.viewmodel.BaseViewModel

class ReasonForVisitFragmentViewModel : BaseViewModel() {

    @Bindable
    var reasonForVisit: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.reasonForVisit)
        }
}
