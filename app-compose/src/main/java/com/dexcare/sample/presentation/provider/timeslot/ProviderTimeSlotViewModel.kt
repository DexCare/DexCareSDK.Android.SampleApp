package com.dexcare.sample.presentation.provider.timeslot

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dexcare.sample.data.ErrorResult
import com.dexcare.sample.data.ProviderRepository
import com.dexcare.sample.data.ResultState
import com.dexcare.sample.data.SchedulingDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.dexcare.services.provider.models.ProviderTimeSlot
import org.dexcare.services.retail.models.ScheduleDay
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ProviderTimeSlotViewModel @Inject constructor(
    private val providerRepository: ProviderRepository,
    private val schedulingDataStore: SchedulingDataStore,
) : ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _state

    init {
        _state.update { it.copy(inProgress = true) }
        viewModelScope.launch {
            providerRepository.observeTimeSlot().onEach { timeSlot ->
                when (timeSlot) {
                    is ResultState.Complete -> {
                        setData(timeSlot.data)
                    }

                    is ResultState.Error -> {
                        _state.update { it.copy(error = timeSlot.errorResult) }
                    }

                    else -> {

                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun setData(timeSlot: ProviderTimeSlot) {
        val filteredData = timeSlot.scheduleDays.filter { it.timeSlots.isNotEmpty() }

        _state.update { oldState ->
            oldState.copy(
                providerTimeSlot = timeSlot,
                start = timeSlot.startDate,
                end = timeSlot.endDate,
                scheduleDays = filteredData,
                errorMessage = null,
                inProgress = false,
                noSchedulesAvailable = filteredData.isEmpty(),
                dateOptions = filteredData.map { it.localDate }
            )
        }
    }

    fun onDateSelected(date: LocalDate) {
        val scheduleDay = _state.value.scheduleDays.firstOrNull { it.localDate == date }
        val newSlots = scheduleDay?.timeSlots?.map {
            TimeSlotUiState(
                slotId = it.slotId,
                time = it.slotDateTime.format(DateTimeFormatter.ofPattern("hh:mm a")),
                isSelected = false
            )
        }.orEmpty()

        _state.update { it.copy(selectedDate = date, timeSlots = newSlots, selectedSlot = null) }
    }

    fun onSlotSelected(slot: TimeSlotUiState) {
        val timeSlots = _state.value.timeSlots.map {
            it.copy(isSelected = it.slotId == slot.slotId)
        }
        _state.update {
            it.copy(
                selectedSlot = slot.copy(isSelected = true),
                timeSlots = timeSlots
            )
        }
    }

    fun onContinue() {
        val slotId = _state.value.selectedSlot?.slotId.orEmpty()
        val selectedSlot = _state.value.scheduleDays.flatMap {
            it.timeSlots
        }.first { it.slotId == slotId }
        schedulingDataStore.setTimeSlot(selectedSlot)
    }

    data class UiState(
        val noSchedulesAvailable: Boolean = false,
        val errorMessage: String? = null,
        val scheduleDays: List<ScheduleDay> = emptyList(),
        val dateOptions: List<LocalDate> = emptyList(),
        val selectedDate: LocalDate? = null,
        val selectedSlot: TimeSlotUiState? = null,
        val timeSlots: List<TimeSlotUiState> = emptyList(),
        val providerTimeSlot: ProviderTimeSlot? = null,
        val inProgress: Boolean = false,
        val start: LocalDate? = null,
        val end: LocalDate? = null,
        val error: ErrorResult? = null,
    )
}


@Stable
data class TimeSlotUiState(val slotId: String, val time: String, val isSelected: Boolean)
