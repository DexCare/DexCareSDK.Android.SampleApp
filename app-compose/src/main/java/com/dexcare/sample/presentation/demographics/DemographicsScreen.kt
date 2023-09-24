package com.dexcare.sample.presentation.demographics

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.dexcare.sample.ui.components.ActionBarScreen
import com.dexcare.sample.ui.components.ClickableTextInput
import com.dexcare.sample.ui.components.DateInput
import com.dexcare.sample.ui.components.InputOptions
import com.dexcare.sample.ui.components.SolidButton
import com.dexcare.sample.ui.components.TextInput
import com.dexcare.sample.ui.theme.Dimens
import com.dexcare.sample.ui.theme.LocalColorScheme
import com.dexcare.sample.ui.theme.PreviewUi
import org.dexcare.sampleapp.android.R
import org.dexcare.services.models.PatientDeclaration
import org.dexcare.services.patient.models.Gender
import timber.log.Timber
import java.time.LocalDate

@Composable
fun DemographicsScreen(viewModel: DemographicsViewModel, navContinue: () -> Unit) {
    ActionBarScreen(
        title = "Demographics",
    ) {
        val uiState = viewModel.uiState.collectAsState().value
        val context = LocalContext.current
        viewModel.setBrandName(context.getString(R.string.brand))
        DemographicsContent(
            uiState = uiState,
            onSubmit = {
                viewModel.onSubmit(it, null)
            },
            onNavContinue = {
                viewModel.onNavigationComplete()
                navContinue()
            },
            onSelectTab = {
                viewModel.onTabSelected(it)
            },
            onSelectGender = {
                viewModel.onSelectGender(it)
            },
            onSelectBirthDate = {
                viewModel.onSelectBirthDate(it)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DemographicsContent(
    uiState: DemographicsViewModel.UiState,
    onSubmit: (input: DemographicsInput) -> Unit,
    onSelectTab: (PatientDeclaration) -> Unit,
    onSelectGender: (Gender) -> Unit,
    onSelectBirthDate: (LocalDate) -> Unit,
    onNavContinue: () -> Unit,
) {
    if (uiState.inputComplete) {
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

        val showGenderOption = remember {
            mutableStateOf(false)
        }

        val showBirthdayPicker = remember {
            mutableStateOf(false)
        }

        if (showGenderOption.value) {
            GenderSelector(
                enableDialog = showGenderOption,
                selectedValue = uiState.patientDemographicsInput.gender.input ?: Gender.Unknown,
                onSelectGender = onSelectGender
            )
        }

        if (showBirthdayPicker.value) {
            DateInput(
                isEnabled = showBirthdayPicker,
                onDateSelected = {
                    showBirthdayPicker.value = false
                    onSelectBirthDate(it)
                }
            )
        }

        Column {
            Text(
                text = "Who is this visit for?",
                modifier = Modifier.padding(bottom = Dimens.Spacing.medium),
                style = MaterialTheme.typography.bodyLarge
            )

            Tabs(
                if (uiState.patientDeclaration == PatientDeclaration.Self) 0 else 1,
                onSelfPatientSelect = {
                    onSelectTab(PatientDeclaration.Self)
                },
                onOtherPatientSelect = {
                    onSelectTab(PatientDeclaration.Other)
                }
            )

            if (uiState.patientDeclaration == PatientDeclaration.Self) {
                MySelfTab(
                    uiState.patientDemographicsInput,
                    onSubmit = onSubmit,
                    onShowGenderOption = {
                        showGenderOption.value = true
                    },
                    onShowBirthdayPicker = {
                        showBirthdayPicker.value = true
                    }
                )
            } else {
                SomeoneElseTab(uiState.patientDemographicsInput, uiState.actorDemographicsInput)
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GenderSelector(
    enableDialog: MutableState<Boolean>,
    selectedValue: Gender,
    onSelectGender: (Gender) -> Unit
) {
    Dialog(
        onDismissRequest = {
            enableDialog.value = false
        }
    ) {
        LocalSoftwareKeyboardController.current?.hide()
        LocalFocusManager.current.clearFocus()
        Column(
            Modifier
                .background(LocalColorScheme.current.surface)
                .padding(Dimens.Spacing.large)
        ) {
            RadioOption(
                text = "Male",
                isSelected = selectedValue == Gender.Male
            ) {
                enableDialog.value = false
                onSelectGender(Gender.Male)
            }

            RadioOption(
                text = "Female",
                isSelected = selectedValue == Gender.Female
            ) {
                enableDialog.value = false
                onSelectGender(Gender.Female)
            }

            RadioOption(
                text = "Other",
                isSelected = selectedValue == Gender.Other
            ) {
                enableDialog.value = false
                onSelectGender(Gender.Other)
            }
        }
    }
}

@Composable
fun RadioOption(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onClick() }) {
        Text(text, modifier = Modifier.weight(1f))
        RadioButton(
            selected = isSelected,
            onClick = onClick
        )
    }
}

@Composable
fun ProgressScreen() {
    Box(Modifier.fillMaxSize()) {
        CircularProgressIndicator(Modifier.align(Alignment.Center))
    }
}


@ExperimentalMaterial3Api
@Composable
fun MySelfTab(
    currentInput: DemographicsInput,
    onSubmit: (DemographicsInput) -> Unit,
    onShowGenderOption: () -> Unit,
    onShowBirthdayPicker: () -> Unit,
) {
    Column {
        val firstName = remember(currentInput.firstName) {
            mutableStateOf(currentInput.firstName.input.orEmpty())
        }
        val lastName = remember(currentInput.lastName) {
            mutableStateOf(currentInput.lastName.input.orEmpty())
        }
        val email = remember(currentInput.email) {
            mutableStateOf(currentInput.email.input.orEmpty())
        }
        val dateOfBirth = remember(currentInput.dateOfBirth) {
            mutableStateOf(currentInput.dateOfBirth.input)
        }

        val last4Ssn = remember(currentInput.last4Ssn) {
            mutableStateOf(currentInput.last4Ssn.input.orEmpty())
        }
        val phoneNumber = remember(currentInput.phone) {
            mutableStateOf(currentInput.phone.input.orEmpty())
        }
        val address = remember(currentInput.streetAddress) {
            mutableStateOf(currentInput.streetAddress.input.orEmpty())
        }
        val address2 = remember(currentInput.addressLine2) {
            mutableStateOf(currentInput.addressLine2.input.orEmpty())
        }
        val city = remember(currentInput.city) {
            mutableStateOf(currentInput.city.input.orEmpty())
        }
        val state = remember(currentInput.state) {
            mutableStateOf(currentInput.state.input.orEmpty())
        }
        val zipCode = remember(currentInput.zipCode) {
            mutableStateOf(currentInput.zipCode.input.orEmpty())
        }
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {

            TextInput(
                input = firstName,
                label = "First Name",
                error = currentInput.firstName.error,
                keyboardOptions = InputOptions.name,
            )

            TextInput(
                input = lastName,
                label = "Last Name",
                error = currentInput.lastName.error,
                keyboardOptions = InputOptions.name,
            )
            TextInput(
                input = email,
                label = "Email",
                error = currentInput.email.error,
                keyboardOptions = InputOptions.email
            )

            ClickableTextInput(
                input = dateOfBirth.value?.toString().orEmpty(),
                error = currentInput.dateOfBirth.error,
                label = "Date of birth",
                onClick = {
                    onShowBirthdayPicker()
                }
            )


            ClickableTextInput(
                input = currentInput.gender.input?.name.orEmpty(),
                error = currentInput.gender.error,
                label = "Gender",
                onClick = {
                    Timber.d("Gender clicked")
                    onShowGenderOption()
                }
            )
            TextInput(
                input = last4Ssn,
                label = "Last 4 SSN",
                error = currentInput.last4Ssn.error,
                keyboardOptions = InputOptions.ssn
            )
            TextInput(
                input = phoneNumber,
                label = "Phone Number",
                error = currentInput.phone.error,
                keyboardOptions = InputOptions.phoneNumber,
            )

            //Address
            TextInput(
                input = address,
                label = "Address",
                error = currentInput.streetAddress.error,
                keyboardOptions = InputOptions.address
            )
            TextInput(
                input = address2,
                label = "Address  Line 2 (Optional)",
                error = currentInput.addressLine2.error,
                keyboardOptions = InputOptions.address,
            )
            TextInput(
                input = city,
                label = "City",
                error = currentInput.city.error,
                keyboardOptions = InputOptions.address
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing.large)
            ) {
                TextInput(
                    modifier = Modifier.weight(1f),
                    input = state,
                    label = "Sate",
                    error = currentInput.state.error,
                    keyboardOptions = InputOptions.address,
                    fillMaxWidth = false
                )
                TextInput(
                    modifier = Modifier.weight(1f),
                    input = zipCode,
                    label = "Zip Code",
                    error = currentInput.zipCode.error,
                    keyboardOptions = InputOptions.zipCode,
                    fillMaxWidth = false
                )
            }
        }

        SolidButton(text = "Next", modifier = Modifier.fillMaxWidth()) {
            val input = DemographicsInput.initialize()
                .withFirstName(firstName.value.trim())
                .withLastName(lastName.value.trim())
                .withEmail(email.value.trim())
                .withPhone(phoneNumber.value.trim())
                .withLast4Ssn(last4Ssn.value.trim())
                .withStreetAddress(address.value.trim())
                .withAddressLine2(address2.value.trim())
                .withCity(city.value.trim())
                .withState(state.value.trim())
                .withZipCode(zipCode.value.trim())
                .withGender(currentInput.gender.input ?: Gender.Unknown)
                .withDateOfBirth(currentInput.dateOfBirth.input)
            onSubmit(input)
        }
    }
}

@Composable
fun SomeoneElseTab(patientInput: DemographicsInput, actorInput: DemographicsInput) {

}

@Preview
@Composable
fun PreviewDemographicsContent() {
    PreviewUi {
        val uiState = DemographicsViewModel.UiState(
            patientDemographicsInput = DemographicsInput.initialize()
                .withFirstName("John")
                .withLastName("Smith")
                .withGender(Gender.Male)
                .withDateOfBirth(LocalDate.now().minusYears(20))
                .withEmail("help@dexcarehealth.com")
        )
        DemographicsContent(
            uiState,
            onSubmit = {},
            onNavContinue = {},
            onSelectTab = {},
            onSelectGender = {},
            onSelectBirthDate = {}
        )
    }
}


@Preview
@Composable
private fun PreviewGenderSelector() {
    PreviewUi {
        val enable = remember {
            mutableStateOf(true)
        }
        GenderSelector(
            enableDialog = enable,
            selectedValue = Gender.Male,
            onSelectGender = {})
    }
}
