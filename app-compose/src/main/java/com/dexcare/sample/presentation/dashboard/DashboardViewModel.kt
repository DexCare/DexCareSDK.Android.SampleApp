package com.dexcare.sample.presentation.dashboard

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.dexcare.sample.auth.LogoutHandler
import com.dexcare.sample.common.toError
import com.dexcare.sample.data.ErrorResult
import com.dexcare.sample.data.SchedulingDataStore
import com.dexcare.sample.data.VisitType
import com.dexcare.sample.data.repository.PatientRepository
import com.dexcare.sample.data.repository.VirtualVisitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val patientRepository: PatientRepository,
    private val virtualVisitRepository: VirtualVisitRepository,
    private val schedulingDataStore: SchedulingDataStore,
    private val logoutHandler: LogoutHandler,
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
                    _state.update {
                        it.copy(
                            isLoading = false,
                            visitIntent = intent,
                            error = error?.toError(title = "Error rejoining visit")
                        )
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

    fun logOut() {
        logoutHandler.logOut()
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    data class UiState(
        val isLoading: Boolean = false,
        val visitIntent: Intent? = null,
        val error: ErrorResult? = null
    )
}
