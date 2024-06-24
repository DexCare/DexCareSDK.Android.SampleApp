package com.dexcare.sample.presentation.retailclinic.timeslot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dexcare.sample.common.toError
import com.dexcare.sample.data.ErrorResult
import com.dexcare.sample.data.RetailClinicRepository
import com.dexcare.sample.data.SchedulingDataStore
import com.dexcare.sample.presentation.provider.timeslot.TimeSlotUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.dexcare.services.retail.models.RetailAppointmentTimeSlot
import org.dexcare.services.retail.models.ScheduleDay
import timber.log.Timber
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class RetailTimeSlotViewModel @Inject constructor(
    private val retailClinicRepository: RetailClinicRepository,
    private val schedulingDataStore: SchedulingDataStore,
) : ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _state

    init {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val departmentName = schedulingDataStore.scheduleRequest.retailClinic?.departmentName
            if (!departmentName.isNullOrEmpty()) {
                retailClinicRepository.getTimeSlots(departmentName).subscribe({ retailTimeSlot ->
                    setData(retailTimeSlot)
                }, { err ->
                    _state.update { it.copy(isLoading = false, error = err.toError()) }
                }
                )
            }
        }
    }

    private fun setData(timeSlot: RetailAppointmentTimeSlot) {
        Timber.i("TimeSlotViewModel setData:: $timeSlot")
        val filteredData = timeSlot.scheduleDays.filter { it.timeSlots.isNotEmpty() }

        _state.update { oldState ->
            oldState.copy(
                retailTimeSlot = timeSlot,
                start = timeSlot.startDate.toLocalDate(),
                end = timeSlot.endDate.toLocalDate(),
                scheduleDays = timeSlot.scheduleDays,
                error = null,
                isLoading = false,
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
        val newTimeSlots = _state.value.timeSlots.map {
            it.copy(isSelected = it.slotId == slot.slotId)
        }
        _state.update {
            it.copy(
                selectedSlot = slot.copy(isSelected = true),
                timeSlots = newTimeSlots
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
        val isLoading: Boolean = false,
        val retailTimeSlot: RetailAppointmentTimeSlot? = null,
        val noSchedulesAvailable: Boolean = false,
        val error: ErrorResult? = null,
        val selectedDate: LocalDate? = null,
        val selectedSlot: TimeSlotUiState? = null,
        val timeSlots: List<TimeSlotUiState> = emptyList(),
        val dateOptions: List<LocalDate> = emptyList(),
        val scheduleDays: List<ScheduleDay> = emptyList(),
        val start: LocalDate? = null,
        val end: LocalDate? = null,
    )
}
