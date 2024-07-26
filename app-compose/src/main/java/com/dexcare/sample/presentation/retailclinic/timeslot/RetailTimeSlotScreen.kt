package com.dexcare.sample.presentation.retailclinic.timeslot

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dexcare.sample.presentation.provider.ProgressMessage
import com.dexcare.sample.presentation.provider.timeslot.NoSchedulesMessage
import com.dexcare.sample.presentation.provider.timeslot.TimeSlotUiState
import com.dexcare.sample.ui.components.ActionBarScreen
import com.dexcare.sample.ui.components.InformationScreen
import com.dexcare.sample.ui.components.SolidButton
import com.dexcare.sample.ui.components.applyWhen
import com.dexcare.sample.ui.theme.Dimens
import com.dexcare.sample.ui.theme.LocalAppColor
import com.dexcare.sample.ui.theme.PreviewUi
import timber.log.Timber
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

@Composable
fun RetailTimeSlotScreen(
    viewModel: RetailTimeSlotViewModel,
    onBackPressed: () -> Unit,
    onContinue: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState().value
    if (uiState.error != null) {
        InformationScreen(title = uiState.error.title, message = uiState.error.message) {
            onBackPressed()
        }
    } else {
        ActionBarScreen(
            title = "Time Slot",
            onBackPressed = onBackPressed,
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                if (uiState.isLoading) {
                    ProgressMessage()
                } else {
                    RetailTimeSlotContent(
                        uiState = uiState,
                        onDaySelected = {
                            viewModel.onDateSelected(it)
                        },
                        onTimeSlotSelected = {
                            viewModel.onSlotSelected(it)
                        },
                        onNextClick = {
                            viewModel.onContinue()
                            onContinue()
                        }
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RetailTimeSlotContent(
    uiState: RetailTimeSlotViewModel.UiState,
    onDaySelected: (day: LocalDate) -> Unit,
    onTimeSlotSelected: (slot: TimeSlotUiState) -> Unit,
    onNextClick: () -> Unit,
) {
    if (uiState.noSchedulesAvailable) {
        NoSchedulesMessage()
    } else {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(Dimens.Spacing.large)
        ) {
            val selectedDate = remember {
                mutableStateOf("")
            }

            val minDate = remember {
                mutableStateOf(uiState.start ?: LocalDate.now())
            }

            val maxDate = remember {
                mutableStateOf(uiState.start ?: LocalDate.now())
            }

            DateTimePicker(
                minDate.value,
                maxDate.value,
                uiState.dateOptions,
                onSelect = {
                    selectedDate.value = it.toString()
                    onDaySelected(it)
                })

            if (uiState.timeSlots.isEmpty()) {
                Text(
                    text = "There are no appointments available for this date. Please select another date.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = Dimens.Spacing.small,
                            vertical = Dimens.Spacing.medium
                        )
                )
            } else {
                Text(
                    text = "Select appointment time",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = Dimens.Spacing.small)
                )
                FlowRow(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    uiState.timeSlots.forEach { slot ->
                        TimeOption(timeSlot = slot) {
                            onTimeSlotSelected(slot)
                        }
                    }
                }
            }

            SolidButton(
                text = "Next",
                modifier = Modifier.fillMaxWidth(),
                isEnabled = uiState.selectedSlot != null && uiState.selectedDate != null
            ) {
                onNextClick()
            }
        }
    }
}

@Composable
fun TimeOption(modifier: Modifier = Modifier, timeSlot: TimeSlotUiState, onSelect: () -> Unit) {
    val colors = LocalAppColor.current
    val timeShape = RoundedCornerShape(20.dp)
    Text(
        text = timeSlot.time,
        modifier = modifier
            .padding(
                end = Dimens.Spacing.medium,
                bottom = Dimens.Spacing.medium
            )
            .background(
                if (timeSlot.isSelected) colors.primary else Color.Transparent,
                shape = timeShape
            )
            .applyWhen(!timeSlot.isSelected) {
                border(1.dp, colors.primary, timeShape)
            }
            .clickable {
                onSelect()
            }
            .padding(Dimens.Spacing.small),
        color = if (timeSlot.isSelected) colors.light else colors.primary
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePicker(
    minDate: LocalDate,
    maxDate: LocalDate,
    dateOptions: List<LocalDate>,
    onSelect: (LocalDate) -> Unit
) {
    val state = rememberDatePickerState(
        initialDisplayMode = DisplayMode.Picker,
        initialSelectedDateMillis = ZonedDateTime.of(
            minDate,
            LocalTime.now(),
            ZoneId.systemDefault()
        ).toInstant().toEpochMilli(),
        selectableDates =
        object : SelectableDates {
            // Blocks Sunday and Saturday from being selected.
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val localDate = Instant.ofEpochMilli(utcTimeMillis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                val isSelectable = dateOptions.contains(localDate)
                Timber.i("isSelectableDate $localDate, isSelectable = $isSelectable, Date options: $dateOptions")
                return isSelectable
            }

            override fun isSelectableYear(year: Int): Boolean {
                Timber.i("isSelectableYear start=${minDate}, end=${maxDate}")

                return year in minDate.year..maxDate.year
            }
        })
    DatePicker(
        state,
        title = {
            Text(
                text = "Select appointment date",
                style = MaterialTheme.typography.titleMedium
            )
        })
    val selectedDate = state.selectedDateMillis?.let {
        Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC")).toLocalDate()
    } ?: minDate

    LaunchedEffect(key1 = selectedDate) {
        onSelect(selectedDate)
    }
}


@Preview
@Composable
private fun PreviewRetailTimeSlot() {
    PreviewUi {
        val uiState = RetailTimeSlotViewModel.UiState(
            timeSlots = listOf(
                TimeSlotUiState(
                    slotId = "1",
                    time = "11:00 AM",
                    isSelected = false,
                ),
                TimeSlotUiState(
                    slotId = "2",
                    time = "11:30 AM",
                    isSelected = false,
                ),
                TimeSlotUiState(
                    slotId = "3",
                    time = "12:30 PM",
                    isSelected = true,
                ),
                TimeSlotUiState(
                    slotId = "4",
                    time = "12:30 PM",
                    isSelected = false,
                ),
                TimeSlotUiState(
                    slotId = "5",
                    time = "1:30 PM",
                    isSelected = false,
                ),
            )
        )
        RetailTimeSlotContent(uiState = uiState, { }, {}, {})
    }
}
