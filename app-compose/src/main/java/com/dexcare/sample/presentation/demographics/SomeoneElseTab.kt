package com.dexcare.sample.presentation.demographics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.dexcare.sample.ui.components.ClickableTextInput
import com.dexcare.sample.ui.components.InputOptions
import com.dexcare.sample.ui.components.SolidButton
import com.dexcare.sample.ui.components.TextInput
import com.dexcare.sample.ui.theme.Dimens
import com.dexcare.sample.ui.theme.PreviewUi
import org.dexcare.services.models.PatientDeclaration
import org.dexcare.services.patient.models.Gender
import timber.log.Timber
import java.time.LocalDate

@Composable
fun SomeoneElseTab(
    patientInput: DemographicsInput,
    actorInput: DemographicsInput,
    onSubmit: (DemographicsInput, DemographicsInput) -> Unit,
    onShowGenderOption: (isPatient: Boolean) -> Unit,
    onShowBirthdayPicker: (isPatient: Boolean) -> Unit,
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

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dimens.Spacing.small)
                    .background(color = Color.White, RoundedCornerShape(Dimens.Spacing.medium))
                    .padding(Dimens.Spacing.small)
            ) {
                Text(
                    text = "Patient Demographics",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
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
                        onShowBirthdayPicker(true)
                    }
                )

                ClickableTextInput(
                    input = patientInput.gender.input?.name.orEmpty(),
                    error = patientInput.gender.error,
                    label = "Patient's Gender",
                    onClick = {
                        Timber.d("Gender clicked")
                        onShowGenderOption(true)
                    }
                )
                TextInput(
                    input = patientLast4Ssn,
                    label = "Patient's Last 4 SSN",
                    error = patientInput.last4Ssn.error,
                    keyboardOptions = InputOptions.ssn
                )
            }

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dimens.Spacing.small)
                    .background(color = Color.White, RoundedCornerShape(Dimens.Spacing.medium))
                    .padding(Dimens.Spacing.small)
            ) {
                Text(
                    text = "Patient Contact Information",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
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
            }

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dimens.Spacing.small)
                    .background(color = Color.White, RoundedCornerShape(Dimens.Spacing.medium))
                    .padding(Dimens.Spacing.small)
            ) {
                //Actor information
                Text(
                    text = "Your Contact Information",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
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
                        onShowBirthdayPicker(false)
                    }
                )


                ClickableTextInput(
                    input = actorInput.gender.input?.name.orEmpty(),
                    error = actorInput.gender.error,
                    label = "Your Gender",
                    onClick = {
                        Timber.d("Gender clicked")
                        onShowGenderOption(false)
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
fun PreviewSomeoneElseTab() {
    PreviewUi {
        val uiState = DemographicsViewModel.UiState(
            selfPatientDemographicsInput = DemographicsInput.initialize()
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
            onSelectGender = { _, _ -> },
            onSelectBirthDate = { _, _ -> },
            onSubmitForSomeoneElse = { _, _ -> },
            onClearError = {}
        )
    }
}
