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
                    val practiceRegions = practice.practiceRegions.filter {
                        it.active
                    }.sortedBy { region ->
                        region.busy
                    }
                    _state.update { oldState ->
                        oldState.copy(
                            practiceRegions = practiceRegions,
                            inProgress = false,
                            selectedRegion = practiceRegions.firstOrNull {
                                //map to the one selected in last visit if available.
                                it.practiceRegionId == virtualVisitRepository.findPreviousRegionId()
                            }
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
        _state.update { it.copy(selectedRegion = region, displaySelectionList = false) }
        schedulingDataStore.setVirtualPracticeRegion(region)
        virtualVisitRepository.savePracticeRegion(region)
    }

    fun onToggleListDisplay(display: Boolean) {
        _state.update { it.copy(displaySelectionList = display) }
    }

    data class UiState(
        val practiceRegions: List<VirtualPracticeRegion> = emptyList(),
        val inProgress: Boolean = false,
        val displaySelectionList: Boolean = false,
        val selectedRegion: VirtualPracticeRegion? = null
    )
}
