package com.dexcare.sample.presentation.reasonforvisit

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReasonForVisitViewModel @Inject constructor() : ViewModel() {

    fun onReasonInput(reason: String) {
        //todo
    }

}
