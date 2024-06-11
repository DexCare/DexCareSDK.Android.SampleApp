package com.dexcare.sample.presentation.practiceregion

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dexcare.sample.data.EnvironmentsRepository
import com.dexcare.sample.data.SchedulingDataStore
import com.dexcare.sample.data.VirtualVisitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.dexcare.services.virtualvisit.models.VirtualPracticeRegion
import javax.inject.Inject

@HiltViewModel
class PracticeRegionViewModel @Inject constructor(
    private val virtualVisitRepository: VirtualVisitRepository,
    private val schedulingDataStore: SchedulingDataStore,
    private val environmentsRepository: EnvironmentsRepository,
) : ViewModel(), DefaultLifecycleObserver {

    private val _state = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _state

    init {
        viewModelScope.launch {
            _state.update { it.copy(inProgress = true) }
            virtualVisitRepository.getPracticeRegion(
                environmentsRepository.findSelectedEnvironment()!!.virtualPracticeId,
                onSuccess = { practice ->
                    _state.update {oldState->
                        oldState.copy(
                            practiceRegions = practice.practiceRegions.filter { it.active }.sortedBy { region -> region.busy },
                            inProgress = false
                        )
                    }
                },
                onError = {
                    _state.update { it.copy(inProgress = false) }
                }
            )
        }
    }

    fun selectRegion(region: VirtualPracticeRegion) {
        schedulingDataStore.setVirtualPracticeRegion(region)
    }

    data class UiState(
        val practiceRegions: List<VirtualPracticeRegion> = emptyList(),
        val inProgress: Boolean = false,
    )
}
