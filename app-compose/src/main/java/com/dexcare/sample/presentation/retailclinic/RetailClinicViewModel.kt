package com.dexcare.sample.presentation.retailclinic

import androidx.lifecycle.ViewModel
import com.dexcare.sample.common.toError
import com.dexcare.sample.data.repository.EnvironmentsRepository
import com.dexcare.sample.data.ErrorResult
import com.dexcare.sample.data.repository.RetailClinicRepository
import com.dexcare.sample.data.SchedulingDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.dexcare.services.retail.models.RetailDepartment
import javax.inject.Inject

@HiltViewModel
class RetailClinicViewModel @Inject constructor(
    private val schedulingDataStore: SchedulingDataStore,
    retailClinicRepository: RetailClinicRepository,
    environmentsRepository: EnvironmentsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _state

    init {
        setLoading(true)
        val brandName = environmentsRepository.findSelectedEnvironment()?.brand.orEmpty()
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
