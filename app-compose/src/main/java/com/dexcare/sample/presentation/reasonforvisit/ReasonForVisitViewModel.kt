package com.dexcare.sample.presentation.reasonforvisit

import androidx.lifecycle.ViewModel
import com.dexcare.sample.data.SchedulingDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ReasonForVisitViewModel @Inject constructor(private val schedulingDataStore: SchedulingDataStore) :
    ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _state

    fun onReasonInput(reason: String): Boolean {
        _state.update { it.copy(reason = reason) }
        return if (validateReason(reason)) {
            schedulingDataStore.setReasonForVisit(reason)
            true
        } else {
            false
        }
    }

    private fun validateReason(reason: String): Boolean {
        val isValid = reason.isNotEmpty()
        _state.update {
            it.copy(error = if (isValid) null else "This is required")
        }
        return isValid
    }

    data class UiState(
        val reason: String = "",
        val error: String? = null,
    )
}
