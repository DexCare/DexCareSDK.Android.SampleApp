package com.dexcare.sample.presentation.provider.timeslot

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dexcare.sample.presentation.provider.ProgressMessage
import com.dexcare.sample.ui.components.ActionBarScreen
import com.dexcare.sample.ui.components.InformationScreen
import com.dexcare.sample.ui.components.SolidButton
import com.dexcare.sample.ui.components.TertiaryButton
import com.dexcare.sample.ui.theme.Dimens
import com.dexcare.sample.ui.theme.LocalColorScheme
import com.dexcare.sample.ui.theme.PreviewUi
import java.time.LocalDate

@Composable
fun ProviderTimeSlotScreen(
    viewModel: ProviderTimeSlotViewModel,
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
                if (uiState.inProgress) {
                    ProgressMessage()
                } else {
                    ProviderTimeSlotContent(
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
private fun ProviderTimeSlotContent(
    uiState: ProviderTimeSlotViewModel.UiState,
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
                text = "Select one of schedules between ${uiState.start} and ${uiState.end}",
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

@Composable
fun NoSchedulesMessage() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "There are no schedules available for selected visit type. Please select another visit type and try again.")
    }
}


@Composable
fun InputComponent(
    value: String,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    onClick: () -> Unit
) {
    val colors = LocalColorScheme.current
    val color = if (isEnabled) {
        colors.primary
    } else {
        colors.primary.copy(alpha = .5f)
    }

    Text(
        text = value,
        modifier = modifier
            .fillMaxWidth()
            .border(
                1.dp,
                color = color,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable {
                if (isEnabled) {
                    onClick()
                }
            }
            .padding(Dimens.Spacing.medium),
        style = MaterialTheme.typography.bodyMedium.copy(color = color),
    )
}

@Preview
@Composable
fun PreviewProviderTimeSlotContent() {
    val today = LocalDate.now()
    val uiState = ProviderTimeSlotViewModel.UiState(
        start = today,
        end = today.plusMonths(3),
        noSchedulesAvailable = false,
        dateOptions = listOf(
            today,
            today.plusDays(1),
            today.plusDays(2),
            today.plusDays(3),
            today.plusDays(4),
        )
    )
    PreviewUi {
        ProviderTimeSlotContent(uiState, {}, {}, {})
    }
}
