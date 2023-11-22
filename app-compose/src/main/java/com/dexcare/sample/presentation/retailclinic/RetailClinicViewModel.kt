package com.dexcare.sample.presentation.retailclinic

import androidx.lifecycle.ViewModel
import com.dexcare.sample.common.toError
import com.dexcare.sample.data.ErrorResult
import com.dexcare.sample.data.RetailClinicRepository
import com.dexcare.sample.data.SchedulingDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.dexcare.services.retail.models.RetailDepartment
import javax.inject.Inject

@HiltViewModel
class RetailClinicViewModel @Inject constructor(
    private val retailClinicRepository: RetailClinicRepository,
    private val schedulingDataStore: SchedulingDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _state

    fun initialize(brandName: String) {
        if (_state.value.clinics.isNotEmpty()) {
            return
        }
        setLoading(true)
        retailClinicRepository.getClinics(brandName).subscribe({ clinics ->
            _state.update { it.copy(clinics = clinics, isLoading = false) }
        }, { err ->
            _state.update { it.copy(error = err.toError(), isLoading = false) }
        })
    }

    fun onClinicSelected(clinic: RetailDepartment) {
        schedulingDataStore.setRetailClinic(clinic)
    }

    private fun setLoading(loading: Boolean) {
        _state.update { it.copy(isLoading = loading) }
    }

    data class UiState(
        val isLoading: Boolean = false,
        val clinics: List<RetailDepartment> = emptyList(),
        val error: ErrorResult? = null,
    )
}
