package com.dexcare.sample.presentation.demographics

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class DemographicsViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _state

    fun onSubmit(demographicsInput: DemographicsInput) {
        val validatedInput = demographicsInput.validate()
        if (validatedInput.isValid()) {
            _state.update {
                it.copy(patientDemographicsInput = validatedInput, inputComplete = true)
            }
        } else {
            _state.update {
                it.copy(patientDemographicsInput = validatedInput, inputComplete = false)
            }
        }
    }

    fun onNavigationComplete() {
        _state.update {
            it.copy(inputComplete = false)
        }
    }

    data class UiState(
        /**
         * Person receiving the care.
         * */
        val patientDemographicsInput: DemographicsInput = DemographicsInput.initialize(),

        /**
         * App user booking the care for someone else.
         * */
        val actorDemographicsInput: DemographicsInput = DemographicsInput.initialize(),
        val inputComplete: Boolean = false,
    )

}
