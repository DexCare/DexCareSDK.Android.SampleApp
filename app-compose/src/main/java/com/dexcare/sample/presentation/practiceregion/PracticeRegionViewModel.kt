package com.dexcare.sample.presentation.practiceregion

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val schedulingDataStore: SchedulingDataStore
) : ViewModel(), DefaultLifecycleObserver {


    private val _state = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _state

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        viewModelScope.launch {
            if (!_state.value.inProgress) {
                _state.update { it.copy(inProgress = true) }
                virtualVisitRepository.getPracticeRegion(
                    _state.value.practiceId,
                    onSuccess = { practice ->
                        _state.update {
                            it.copy(
                                practiceRegions = practice.practiceRegions.sortedBy { region -> region.busy },
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
    }

    fun setPracticeId(practiceId: String) {
        _state.update { it.copy(practiceId = practiceId) }
    }

    fun selectRegion(region: VirtualPracticeRegion) {
        schedulingDataStore.setVirtualPracticeRegion(region)
    }

    data class UiState(
        val practiceId: String = "",
        val practiceRegions: List<VirtualPracticeRegion> = emptyList(),
        val inProgress: Boolean = false,
    )
}
