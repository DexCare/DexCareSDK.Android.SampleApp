package com.dexcare.sample.presentation.demographics

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.dexcare.sample.ui.components.ActionBarScreen
import com.dexcare.sample.ui.components.InputOptions
import com.dexcare.sample.ui.components.SolidButton
import com.dexcare.sample.ui.components.TextInput
import com.dexcare.sample.ui.theme.Dimens
import com.dexcare.sample.ui.theme.PreviewUi
import org.dexcare.sampleapp.android.R
import org.dexcare.services.models.PatientDeclaration
import org.dexcare.services.patient.models.Gender
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

        Column {
            Text(
                text = "Who is this visit for?",
                modifier = Modifier.padding(vertical = Dimens.Spacing.small),
                style = MaterialTheme.typography.bodyMedium
            )

//            val selectedPosition = rememberSaveable { mutableStateOf(uiState.patientDeclaration) }

            Tabs(
                0,
                onSelfPatientSelect = {
                    onSelectTab(PatientDeclaration.Self)
//                    selectedPosition.value = 0
                },
                onOtherPatientSelect = {
                    onSelectTab(PatientDeclaration.Other)
//                    selectedPosition.value = 1
                }
            )

            if (uiState.patientDeclaration == PatientDeclaration.Self) {
                MySelfTab(uiState.patientDemographicsInput, onSubmit)
            } else {
                SomeoneElseTab(uiState.patientDemographicsInput, uiState.actorDemographicsInput)
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


@ExperimentalMaterial3Api
@Composable
fun MySelfTab(
    currentInput: DemographicsInput,
    onSubmit: (DemographicsInput) -> Unit
) {
    Column {
        var firstName = remember {
            mutableStateOf(currentInput.firstName.input.orEmpty())
        }

        val lastName = rememberSaveable {
            mutableStateOf(currentInput.lastName.input.orEmpty())
        }
        val email = rememberSaveable {
            mutableStateOf(currentInput.email.input.orEmpty())
        }
        val dateOfBirth = rememberSaveable {
            mutableStateOf(currentInput.dateOfBirth.input.toString())
        }
        val gender = rememberSaveable {
            mutableStateOf(currentInput.gender.input?.name.orEmpty())
        }
        val last4Ssn = rememberSaveable {
            mutableStateOf(currentInput.last4Ssn.input.orEmpty())
        }
        val phoneNumber = rememberSaveable {
            mutableStateOf(currentInput.phone.input.orEmpty())
        }
        val address = rememberSaveable {
            mutableStateOf(currentInput.streetAddress.input.orEmpty())
        }
        val address2 = rememberSaveable {
            mutableStateOf(currentInput.addressLine2.input.orEmpty())
        }
        val city = rememberSaveable {
            mutableStateOf(currentInput.city.input.orEmpty())
        }
        val state = rememberSaveable {
            mutableStateOf(currentInput.state.input.orEmpty())
        }
        val zipCode = rememberSaveable {
            mutableStateOf(currentInput.zipCode.input.orEmpty())
        }
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {

            Text(text = currentInput.firstName.input.orEmpty())
            Text(text = currentInput.lastName.input.orEmpty())
            Text(text = currentInput.email.input.orEmpty())

            TextInput(
                input = currentInput.firstName.input.orEmpty(),
                hint = "First Name",
                error = currentInput.firstName.error,
                keyboardOptions = InputOptions.name,
                onValueChanged = {
//                    firstName  = it
                }
            )

            TextInput(
                input = lastName,
                hint = "Last Name",
                error = currentInput.lastName.error,
                keyboardOptions = InputOptions.name
            )
            TextInput(
                input = email,
                hint = "Email",
                error = currentInput.email.error,
                keyboardOptions = InputOptions.email
            )
            TextInput(
                input = dateOfBirth,
                error = currentInput.dateOfBirth.error,
                hint = "Date of Birth",
            )
            TextInput(
                input = gender,
                error = currentInput.gender.error,
                hint = "Gender"
            )
            TextInput(
                input = last4Ssn,
                hint = "Last 4 SSN",
                error = currentInput.last4Ssn.error,
                keyboardOptions = InputOptions.ssn
            )
            TextInput(
                input = phoneNumber,
                hint = "Phone Number",
                error = currentInput.phone.error,
                keyboardOptions = InputOptions.phoneNumber,
            )

            //Address
            TextInput(
                input = address,
                hint = "Address",
                error = currentInput.streetAddress.error,
                keyboardOptions = InputOptions.address
            )
            TextInput(
                input = address2,
                hint = "Address  Line 2 (Optional)",
                error = currentInput.addressLine2.error,
                keyboardOptions = InputOptions.address,
            )
            TextInput(
                input = city,
                hint = "City",
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
                    hint = "Sate",
                    error = currentInput.state.error,
                    keyboardOptions = InputOptions.address,
                    coverMaxWidth = false
                )
                TextInput(
                    modifier = Modifier.weight(1f),
                    input = zipCode,
                    hint = "Zip Code",
                    error = currentInput.zipCode.error,
                    keyboardOptions = InputOptions.zipCode,
                    coverMaxWidth = false
                )
            }
        }

        SolidButton(text = "Next", modifier = Modifier.fillMaxWidth()) {
            val input = DemographicsInput.initialize()
                .withFirstName(firstName.value.trim())
                .withLastName(lastName.value.trim())
                .withEmail(email.value.trim())
                .withPhone(phoneNumber.value.trim())
                .withDateOfBirth(LocalDate.now().minusDays(10))//todo
                .withLast4Ssn(last4Ssn.value.trim())
                .withGender(Gender.Unknown)
                .withStreetAddress(address.value.trim())
                .withAddressLine2(address2.value.trim())
                .withCity(city.value.trim())
                .withState(state.value.trim())
                .withZipCode(zipCode.value.trim())
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
        val uiState = DemographicsViewModel.UiState()
        DemographicsContent(
            uiState,
            onSubmit = {},
            onNavContinue = {},
            onSelectTab = {},
        )
    }
}
