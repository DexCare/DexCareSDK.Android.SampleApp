package com.dexcare.sample.presentation.demographics

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.dexcare.sample.ui.components.ActionBarScreen
import com.dexcare.sample.ui.components.DateInput
import com.dexcare.sample.ui.components.SimpleAlert
import com.dexcare.sample.ui.theme.Dimens
import com.dexcare.acme.android.R
import org.dexcare.services.models.PatientDeclaration
import org.dexcare.services.patient.models.Gender
import java.time.LocalDate

@Composable
fun DemographicsScreen(
    viewModel: DemographicsViewModel,
    navContinue: () -> Unit,
    onBackPressed: () -> Unit
) {
    ActionBarScreen(
        title = "Patient information",
        onBackPressed = onBackPressed
    ) {
        val uiState = viewModel.uiState.collectAsState().value
        val context = LocalContext.current
        viewModel.setBrandName(context.getString(R.string.brand))
        DemographicsContent(
            uiState = uiState,
            onSubmitForSelf = {
                viewModel.onSubmit(it, null)
            },
            onNavContinue = {
                viewModel.onNavigationComplete()
                navContinue()
            },
            onSelectTab = {
                viewModel.onTabSelected(it)
            },
            onSelectGender = { gender, isPatient ->
                viewModel.onSelectGender(gender, isPatient)
            },
            onSelectBirthDate = { date, isPatient ->
                viewModel.onSelectBirthDate(date, isPatient)
            },
            onSubmitForSomeoneElse = { patientInput, actorInput ->
                viewModel.onSubmit(patientInput, actorInput)
            },
            onClearError = {
                viewModel.setError(null)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DemographicsContent(
    uiState: DemographicsViewModel.UiState,
    onSubmitForSelf: (input: DemographicsInput) -> Unit,
    onSubmitForSomeoneElse: (patientInput: DemographicsInput, actorInput: DemographicsInput) -> Unit,
    onSelectTab: (PatientDeclaration) -> Unit,
    onSelectGender: (Gender, Boolean) -> Unit,
    onSelectBirthDate: (LocalDate, Boolean) -> Unit,
    onClearError: () -> Unit,
    onNavContinue: () -> Unit,
) {
    if (uiState.demographicsComplete) {
        LaunchedEffect(Unit) {
            onNavContinue()
        }
    }
    Box(
        Modifier
            .padding(Dimens.Spacing.large)
    ) {
        if (uiState.inProgress) {
            ProgressScreen()
        }

        val showGenderOption = remember { mutableStateOf(false) }
        val showBirthdayPicker = remember { mutableStateOf(false) }
        val showErrorAlert = remember { mutableStateOf(false) }
        val isPatientGenderOption = remember { mutableStateOf(true) }
        val isPatientBirthdayPicker = remember { mutableStateOf(true) }

        if (showGenderOption.value) {
            GenderSelector(
                enableDialog = showGenderOption,
                selectedValue = uiState.selfPatientDemographicsInput.gender.input ?: Gender.Unknown,
                onSelectGender = { onSelectGender(it, isPatientGenderOption.value) }
            )
        }

        if (showBirthdayPicker.value) {
            DateInput(
                isEnabled = showBirthdayPicker,
                onDateSelected = {
                    showBirthdayPicker.value = false
                    onSelectBirthDate(it, isPatientBirthdayPicker.value)
                }
            )
        }

        if (uiState.errorMessage != null) {
            showErrorAlert.value = true
            SimpleAlert(
                title = "Error",
                message = uiState.errorMessage,
                buttonText = "Ok",
                enabledState = showErrorAlert,
                actionAlertClosed = {
                    onClearError()
                }
            )
        }


        Column {
            Text(
                text = "Who is this visit for?",
                modifier = Modifier.padding(bottom = Dimens.Spacing.medium),
                style = MaterialTheme.typography.titleMedium
            )

            Tabs(
                selectedTabPosition = if (uiState.patientDeclaration == PatientDeclaration.Self) 0 else 1,
                onSelfPatientSelect = {
                    onSelectTab(PatientDeclaration.Self)
                },
                onOtherPatientSelect = {
                    onSelectTab(PatientDeclaration.Other)
                }
            )

            if (uiState.patientDeclaration == PatientDeclaration.Self) {
                MySelfTab(
                    uiState.selfPatientDemographicsInput,
                    onSubmit = onSubmitForSelf,
                    onShowGenderOption = {
                        isPatientGenderOption.value = true
                        showGenderOption.value = true
                    },
                    onShowBirthdayPicker = {
                        isPatientBirthdayPicker.value = true
                        showBirthdayPicker.value = true
                    }
                )
            } else {
                SomeoneElseTab(
                    uiState.otherPatientDemographicsInput,
                    uiState.actorDemographicsInput,
                    onSubmit = { patientInput, actorInput ->
                        onSubmitForSomeoneElse(patientInput, actorInput)
                    },
                    onShowGenderOption = {
                        isPatientGenderOption.value = it
                        showGenderOption.value = true
                    },
                    onShowBirthdayPicker = {
                        isPatientBirthdayPicker.value = it
                        showBirthdayPicker.value = true
                    }
                )
            }
        }
    }
}

@Composable
fun ProgressScreen() {
    Box(Modifier.fillMaxSize()) {
        CircularProgressIndicator(Modifier.align(Alignment.Center))
    }
}

