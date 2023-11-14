package com.dexcare.sample.presentation.dashboard

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.dexcare.sample.data.PatientRepository
import com.dexcare.sample.data.SchedulingDataStore
import com.dexcare.sample.data.VirtualVisitRepository
import com.dexcare.sample.data.VisitType
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val patientRepository: PatientRepository,
    private val virtualVisitRepository: VirtualVisitRepository,
    private val schedulingDataStore: SchedulingDataStore
) : ViewModel() {

    init {
        schedulingDataStore.reset()
    }

    fun onVisitType(visitType: VisitType) {
        schedulingDataStore.setVisitType(visitType)
    }

    fun onRejoinVisit(activity: FragmentActivity, visitId: String) {
        patientRepository.findPatient(onSuccess = { patient ->
            virtualVisitRepository.rejoinVisit(
                visitId,
                activity,
                patient,
                onComplete = { intent, error ->
                    if (intent != null) {
                        activity.startActivity(intent)
                    } else if (error != null) {
                        Timber.d("error $error")
                    }
                })
        })
    }

}
