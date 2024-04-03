package com.dexcare.sample.presentation.dashboard

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.dexcare.sample.common.toError
import com.dexcare.sample.data.ErrorResult
import com.dexcare.sample.data.PatientRepository
import com.dexcare.sample.data.SchedulingDataStore
import com.dexcare.sample.data.VirtualVisitRepository
import com.dexcare.sample.data.VisitType
import com.dexcare.sample.presentation.MainActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val patientRepository: PatientRepository,
    private val virtualVisitRepository: VirtualVisitRepository,
    private val schedulingDataStore: SchedulingDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _state

    init {
        schedulingDataStore.reset()
    }

    fun onVisitType(visitType: VisitType) {
        schedulingDataStore.setVisitType(visitType)
    }

    fun onRejoinVisit(activity: FragmentActivity) {
        _state.update { it.copy(isLoading = true) }
        patientRepository.findPatient(onSuccess = { patient ->
            virtualVisitRepository.rejoinVisit(
                activity,
                patient,
                onComplete = { intent, error ->
                    if (intent != null) {
                        _state.update { it.copy(error = null, isLoading = false) }
                        activity.startActivityForResult(
                            intent,
                            MainActivity.REQUEST_CODE_VIRTUAL_VISIT
                        )
                    } else if (error != null) {
                        Timber.d("error $error")
                        _state.update {
                            it.copy(
                                error = error.toError(title = "Error rejoining visit"),
                                isLoading = false
                            )
                        }
                    }
                })
        }, onError = {
            _state.update {
                it.copy(
                    isLoading = false,
                    error = ErrorResult(
                        "Error rejoining visit",
                        "We were unable to load the patient record."
                    )
                )
            }
        })
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    data class UiState(val isLoading: Boolean = false, val error: ErrorResult? = null)

}
