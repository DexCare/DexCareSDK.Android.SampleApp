package com.dexcare.sample.presentation.retailclinic.timeslot

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dexcare.sample.presentation.provider.ProgressMessage
import com.dexcare.sample.presentation.provider.timeslot.InputComponent
import com.dexcare.sample.presentation.provider.timeslot.NoSchedulesMessage
import com.dexcare.sample.presentation.provider.timeslot.TimeSlotUi
import com.dexcare.sample.ui.components.ActionBarScreen
import com.dexcare.sample.ui.components.InformationScreen
import com.dexcare.sample.ui.components.SolidButton
import com.dexcare.sample.ui.components.TertiaryButton
import com.dexcare.sample.ui.theme.Dimens
import java.time.LocalDate

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
    onTimeSlotSelected: (slot: TimeSlotUi) -> Unit,
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
            Text(
                text = "Select one of schedules",
                style = MaterialTheme.typography.bodyMedium,
            )

            val showDayOptions = remember {
                mutableStateOf(false)
            }

            val showSlotOptions = remember {
                mutableStateOf(false)
            }

            InputComponent(
                value = uiState.selectedDate?.toString() ?: "Select date",
                modifier = Modifier.padding(vertical = Dimens.Spacing.large),
                onClick = {
                    showDayOptions.value = !showDayOptions.value
                }
            )
            AnimatedVisibility(visible = showDayOptions.value) {
                FlowRow {
                    uiState.dateOptions.forEach { date ->
                        TertiaryButton(
                            text = date.toString(),
                            onClick = {
                                onDaySelected(date)
                                showDayOptions.value = false
                            }
                        )
                    }
                }
            }

            InputComponent(
                value = uiState.selectedSlot?.time ?: "Select time",
                modifier = Modifier.padding(vertical = Dimens.Spacing.large),
                isEnabled = uiState.selectedDate != null,
                onClick = {
                    showSlotOptions.value = !showSlotOptions.value
                }
            )

            AnimatedVisibility(visible = showSlotOptions.value) {
                FlowRow {
                    uiState.timeSlots.forEach { slot ->
                        TertiaryButton(
                            text = slot.time,
                            onClick = {
                                onTimeSlotSelected(slot)
                                showSlotOptions.value = false
                            }
                        )
                    }
                }
            }

            SolidButton(text = "Next", modifier = Modifier.fillMaxWidth()) {
                onNextClick()
            }
        }
    }
}
