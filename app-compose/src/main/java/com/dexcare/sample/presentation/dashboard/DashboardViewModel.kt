package com.dexcare.sample.presentation.dashboard

import androidx.lifecycle.ViewModel
import com.dexcare.sample.data.SchedulingDataStore
import com.dexcare.sample.data.VisitType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(private val schedulingDataStore: SchedulingDataStore) :
    ViewModel() {

    init {
        schedulingDataStore.reset()
    }

    fun onVisitType(visitType: VisitType) {
        schedulingDataStore.setVisitType(visitType)
    }

}
