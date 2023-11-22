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
import com.dexcare.sample.ui.components.SimpleAlert
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
            onSelectGender = {
                viewModel.onSelectGender(it)
            },
            onSelectBirthDate = {
                viewModel.onSelectBirthDate(it)
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
    onSelectGender: (Gender) -> Unit,
    onSelectBirthDate: (LocalDate) -> Unit,
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

        val showGenderOption = remember {
            mutableStateOf(false)
        }

        val showBirthdayPicker = remember {
            mutableStateOf(false)
        }

        val showErrorAlert = remember {
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

        if (uiState.error != null) {
            showErrorAlert.value = true
            SimpleAlert(
                title = "Error",
                message = uiState.error.message.orEmpty(),
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
                    onSubmit = onSubmitForSelf,
                    onShowGenderOption = {
                        showGenderOption.value = true
                    },
                    onShowBirthdayPicker = {
                        showBirthdayPicker.value = true
                    }
                )
            } else {
                SomeoneElseTab(
                    uiState.patientDemographicsInput,
                    uiState.actorDemographicsInput,
                    onSubmit = { patientInput, actorInput ->
                        onSubmitForSomeoneElse(patientInput, actorInput)
                    }
                )
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
fun SomeoneElseTab(
    patientInput: DemographicsInput,
    actorInput: DemographicsInput,
    onSubmit: (DemographicsInput, DemographicsInput) -> Unit,
    onShowGenderOption: () -> Unit = {},
    onShowBirthdayPicker: () -> Unit = {},
) {

    Column {

        /// Patient input
        val patientFirstName = remember(patientInput.firstName) {
            mutableStateOf(patientInput.firstName.input.orEmpty())
        }
        val patientLastName = remember(patientInput.lastName) {
            mutableStateOf(patientInput.lastName.input.orEmpty())
        }
        val patientEmail = remember(patientInput.email) {
            mutableStateOf(patientInput.email.input.orEmpty())
        }
        val patientDateOfBirth = remember(patientInput.dateOfBirth) {
            mutableStateOf(patientInput.dateOfBirth.input)
        }

        val patientLast4Ssn = remember(patientInput.last4Ssn) {
            mutableStateOf(patientInput.last4Ssn.input.orEmpty())
        }
        val patientPhoneNumber = remember(patientInput.phone) {
            mutableStateOf(patientInput.phone.input.orEmpty())
        }

        val patientAddress = remember(patientInput.streetAddress) {
            mutableStateOf(patientInput.streetAddress.input.orEmpty())
        }
        val patientAddress2 = remember(patientInput.addressLine2) {
            mutableStateOf(patientInput.addressLine2.input.orEmpty())
        }
        val patientCity = remember(patientInput.city) {
            mutableStateOf(patientInput.city.input.orEmpty())
        }
        val patientState = remember(patientInput.state) {
            mutableStateOf(patientInput.state.input.orEmpty())
        }
        val patientZipCode = remember(patientInput.zipCode) {
            mutableStateOf(patientInput.zipCode.input.orEmpty())
        }


        /////// App user input
        val actorFirstName = remember(actorInput.firstName) {
            mutableStateOf(actorInput.firstName.input.orEmpty())
        }
        val actorLastName = remember(actorInput.lastName) {
            mutableStateOf(actorInput.lastName.input.orEmpty())
        }
        val actorEmail = remember(actorInput.email) {
            mutableStateOf(actorInput.email.input.orEmpty())
        }
        val actorDateOfBirth = remember(actorInput.dateOfBirth) {
            mutableStateOf(actorInput.dateOfBirth.input)
        }
        val actorLast4Ssn = remember(actorInput.last4Ssn) {
            mutableStateOf(actorInput.last4Ssn.input.orEmpty())
        }
        val actorPhoneNumber = remember(actorInput.phone) {
            mutableStateOf(actorInput.phone.input.orEmpty())
        }
        val actorAddress = remember(actorInput.streetAddress) {
            mutableStateOf(actorInput.streetAddress.input.orEmpty())
        }
        val actorAddress2 = remember(actorInput.addressLine2) {
            mutableStateOf(actorInput.addressLine2.input.orEmpty())
        }
        val actorCity = remember(actorInput.city) {
            mutableStateOf(actorInput.city.input.orEmpty())
        }
        val actorState = remember(actorInput.state) {
            mutableStateOf(actorInput.state.input.orEmpty())
        }
        val actorZipCode = remember(actorInput.zipCode) {
            mutableStateOf(actorInput.zipCode.input.orEmpty())
        }
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {

            Text(
                text = "Patient's Information",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = Dimens.Spacing.small),
            )

            TextInput(
                input = patientFirstName,
                label = "Patient's First Name",
                error = patientInput.firstName.error,
                keyboardOptions = InputOptions.name,
            )

            TextInput(
                input = patientLastName,
                label = "Patient's Last Name",
                error = patientInput.lastName.error,
                keyboardOptions = InputOptions.name,
            )
            TextInput(
                input = patientEmail,
                label = "Patient's Email",
                error = patientInput.email.error,
                keyboardOptions = InputOptions.email
            )

            ClickableTextInput(
                input = patientDateOfBirth.value?.toString().orEmpty(),
                error = patientInput.dateOfBirth.error,
                label = "Patient's Date of birth",
                onClick = {
                    onShowBirthdayPicker()
                }
            )


            ClickableTextInput(
                input = patientInput.gender.input?.name.orEmpty(),
                error = patientInput.gender.error,
                label = "Patient's Gender",
                onClick = {
                    Timber.d("Gender clicked")
                    onShowGenderOption()
                }
            )
            TextInput(
                input = patientLast4Ssn,
                label = "Patient's Last 4 SSN",
                error = patientInput.last4Ssn.error,
                keyboardOptions = InputOptions.ssn
            )
            TextInput(
                input = patientPhoneNumber,
                label = "Patient's Phone Number",
                error = patientInput.phone.error,
                keyboardOptions = InputOptions.phoneNumber,
            )

            TextInput(
                input = patientAddress,
                label = "Patient's Address",
                error = patientInput.streetAddress.error,
                keyboardOptions = InputOptions.address
            )
            TextInput(
                input = patientAddress2,
                label = "Patient's Address  Line 2 (Optional)",
                error = patientInput.addressLine2.error,
                keyboardOptions = InputOptions.address,
            )
            TextInput(
                input = patientCity,
                label = "Patient's City",
                error = patientInput.city.error,
                keyboardOptions = InputOptions.address
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing.large)
            ) {
                TextInput(
                    modifier = Modifier.weight(1f),
                    input = patientState,
                    label = "Patient's Sate",
                    error = patientInput.state.error,
                    keyboardOptions = InputOptions.address,
                    fillMaxWidth = false
                )
                TextInput(
                    modifier = Modifier.weight(1f),
                    input = patientZipCode,
                    label = "Patient's Zip Code",
                    error = patientInput.zipCode.error,
                    keyboardOptions = InputOptions.zipCode,
                    fillMaxWidth = false
                )
            }

            //Actor information
            Text(
                text = "Your Information",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = Dimens.Spacing.small),
            )

            TextInput(
                input = actorFirstName,
                label = "Your First Name",
                error = actorInput.firstName.error,
                keyboardOptions = InputOptions.name,
            )

            TextInput(
                input = actorLastName,
                label = "Your Last Name",
                error = actorInput.lastName.error,
                keyboardOptions = InputOptions.name,
            )
            TextInput(
                input = actorEmail,
                label = "Your Email",
                error = actorInput.email.error,
                keyboardOptions = InputOptions.email
            )

            ClickableTextInput(
                input = actorDateOfBirth.value?.toString().orEmpty(),
                error = actorInput.dateOfBirth.error,
                label = "Your Date of birth",
                onClick = {
                    onShowBirthdayPicker()
                }
            )


            ClickableTextInput(
                input = actorInput.gender.input?.name.orEmpty(),
                error = actorInput.gender.error,
                label = "Your Gender",
                onClick = {
                    Timber.d("Gender clicked")
                    onShowGenderOption()
                }
            )
            TextInput(
                input = actorLast4Ssn,
                label = "Your Last 4 SSN",
                error = actorInput.last4Ssn.error,
                keyboardOptions = InputOptions.ssn
            )
            TextInput(
                input = actorPhoneNumber,
                label = "Your Phone Number",
                error = actorInput.phone.error,
                keyboardOptions = InputOptions.phoneNumber,
            )

            TextInput(
                input = actorAddress,
                label = "Your Address",
                error = actorInput.streetAddress.error,
                keyboardOptions = InputOptions.address
            )
            TextInput(
                input = actorAddress2,
                label = "Your Address  Line 2 (Optional)",
                error = actorInput.addressLine2.error,
                keyboardOptions = InputOptions.address,
            )
            TextInput(
                input = actorCity,
                label = "Your City",
                error = actorInput.city.error,
                keyboardOptions = InputOptions.address
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing.large)
            ) {
                TextInput(
                    modifier = Modifier.weight(1f),
                    input = actorState,
                    label = "Your Sate",
                    error = actorInput.state.error,
                    keyboardOptions = InputOptions.address,
                    fillMaxWidth = false
                )
                TextInput(
                    modifier = Modifier.weight(1f),
                    input = actorZipCode,
                    label = "Your Zip Code",
                    error = actorInput.zipCode.error,
                    keyboardOptions = InputOptions.zipCode,
                    fillMaxWidth = false
                )
            }
        }

        SolidButton(text = "Next", modifier = Modifier.fillMaxWidth()) {
            val patientNewInput = DemographicsInput.initialize()
                .withFirstName(patientFirstName.value.trim())
                .withLastName(patientLastName.value.trim())
                .withEmail(patientEmail.value.trim())
                .withPhone(patientPhoneNumber.value.trim())
                .withLast4Ssn(patientLast4Ssn.value.trim())
                .withStreetAddress(patientAddress.value.trim())
                .withAddressLine2(patientAddress2.value.trim())
                .withCity(patientCity.value.trim())
                .withState(patientState.value.trim())
                .withZipCode(patientZipCode.value.trim())
                .withGender(patientInput.gender.input ?: Gender.Unknown)
                .withDateOfBirth(patientInput.dateOfBirth.input)

            val actorNewInput = DemographicsInput.initialize()
                .withFirstName(actorFirstName.value.trim())
                .withLastName(actorLastName.value.trim())
                .withEmail(actorEmail.value.trim())
                .withPhone(actorPhoneNumber.value.trim())
                .withLast4Ssn(actorLast4Ssn.value.trim())
                .withStreetAddress(actorAddress.value.trim())
                .withAddressLine2(actorAddress2.value.trim())
                .withCity(actorCity.value.trim())
                .withState(actorState.value.trim())
                .withZipCode(actorZipCode.value.trim())
                .withGender(actorInput.gender.input ?: Gender.Unknown)
                .withDateOfBirth(actorInput.dateOfBirth.input)

            onSubmit(patientNewInput, actorNewInput)
        }
    }
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
                .withEmail("help@dexcarehealth.com"),
            patientDeclaration = PatientDeclaration.Other
        )
        DemographicsContent(
            uiState,
            onSubmitForSelf = {},
            onNavContinue = {},
            onSelectTab = {},
            onSelectGender = {},
            onSelectBirthDate = {},
            onSubmitForSomeoneElse = { _, _ -> },
            onClearError = {}
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
