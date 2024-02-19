package com.dexcare.sample.presentation.payment

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import com.dexcare.sample.presentation.demographics.GenderSelector
import com.dexcare.sample.presentation.provider.timeslot.InputComponent
import com.dexcare.sample.ui.components.Actionbar
import com.dexcare.sample.ui.components.ClickableTextInput
import com.dexcare.sample.ui.components.DateInput
import com.dexcare.sample.ui.components.InputOptions
import com.dexcare.sample.ui.components.SolidButton
import com.dexcare.sample.ui.components.TextInput
import com.dexcare.sample.ui.theme.Dimens
import com.dexcare.sample.ui.theme.LocalAppColor
import com.dexcare.sample.ui.theme.PreviewUi
import org.dexcare.services.models.InsuranceOther
import org.dexcare.services.models.InsurancePayer
import org.dexcare.services.models.InsuranceSelf
import org.dexcare.services.models.PaymentMethod
import org.dexcare.services.patient.models.Gender
import java.time.LocalDate

@Composable
fun InsuranceInput(
    uiState: PaymentViewModel.UiState,
    onShowPayerOption: () -> Unit,
    onPaymentSubmitted: (insurance: PaymentMethod) -> Unit,
) {
    Column(
        Modifier
            .padding(vertical = Dimens.Spacing.large)
            .verticalScroll(rememberScrollState())
    ) {
        Text(text = "Are you the primary subscriber of insurance?")

        val isSelfInsurance = remember {
            mutableStateOf(false)
        }

        val memberId = remember {
            mutableStateOf("")
        }

        val insuranceGroupNumber = remember {
            mutableStateOf("")
        }

        val subscriberFirstName = remember {
            mutableStateOf("")
        }

        val subscriberLastName = remember {
            mutableStateOf("")
        }

        val subscriberGender = remember {
            mutableStateOf<Gender?>(null)
        }

        val subscriberDateOfBirth = remember {
            mutableStateOf<LocalDate?>(null)
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
                selectedValue = subscriberGender.value ?: Gender.Other,
                onSelectGender = {
                    subscriberGender.value = it
                }
            )
        }

        if (showBirthdayPicker.value) {
            DateInput(
                isEnabled = showBirthdayPicker,
                defaultDate = LocalDate.now().minusYears(50),
                onDateSelected = {
                    showBirthdayPicker.value = false
                    subscriberDateOfBirth.value = it
                }
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Yes")
            RadioButton(
                selected = isSelfInsurance.value,
                onClick = { isSelfInsurance.value = true })
            Spacer(modifier = Modifier.width(Dimens.Spacing.large))
            Text(text = "No")
            RadioButton(
                selected = !isSelfInsurance.value,
                onClick = { isSelfInsurance.value = false })
        }

        if (!uiState.selectedPayer?.name.isNullOrBlank()) {
            Text(text = "Insurance payer")
        }
        InputComponent(
            value = uiState.selectedPayer?.name ?: "Select insurance",
            modifier = Modifier.padding(vertical = Dimens.Spacing.large),
            onClick = {
                onShowPayerOption()
            }
        )

        AnimatedVisibility(visible = uiState.insurancePayers.isEmpty()) {
            Text(
                text = "There was error loading the list of insurance payers. Please try again later.",
                style = MaterialTheme.typography.bodyMedium,
                color = LocalAppColor.current.error
            )
        }

        if (!isSelfInsurance.value) {
            TextInput(
                input = subscriberFirstName,
                label = "Subscriber's Firstname",
                keyboardOptions = InputOptions.name
            )

            TextInput(
                input = subscriberLastName,
                label = "Subscriber's Lastname",
                keyboardOptions = InputOptions.name
            )

            ClickableTextInput(
                input = subscriberGender.value?.name.orEmpty(),
                label = "Subscriber's Gender"
            ) {
                showGenderOption.value = true
            }

            ClickableTextInput(
                input = subscriberDateOfBirth.value?.toString().orEmpty(),
                label = "Subscriber's Date of Birth"
            ) {
                showBirthdayPicker.value = true
            }
        }
        TextInput(
            input = memberId,
            label = "Member Id",
            keyboardOptions = InputOptions.name
        )

        TextInput(
            input = insuranceGroupNumber,
            label = "Insurance Group Number",
            keyboardOptions = InputOptions.name
        )

        SolidButton(
            text = "Book a visit",
            Modifier
                .padding(vertical = Dimens.Spacing.small)
                .fillMaxWidth()
        ) {
            val insurance = if (isSelfInsurance.value) {
                InsuranceSelf(
                    memberId = memberId.value,
                    insuranceGroupNumber = insuranceGroupNumber.value,
                    payorId = uiState.selectedPayer?.payerId.orEmpty(),
                    payorName = uiState.selectedPayer?.name.orEmpty()
                )
            } else {
                InsuranceOther(
                    firstName = subscriberFirstName.value,
                    lastName = subscriberLastName.value,
                    gender = subscriberGender.value,
                    dateOfBirth = subscriberDateOfBirth.value!!,
                    memberId = memberId.value,
                    insuranceGroupNumber = insuranceGroupNumber.value,
                    payorId = uiState.selectedPayer?.payerId.orEmpty(),
                    payorName = uiState.selectedPayer?.name.orEmpty(),
                    subscriberId = null
                )
            }
            onPaymentSubmitted(insurance)
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun hideKeyBoard() {
    LocalSoftwareKeyboardController.current?.hide()
    LocalFocusManager.current.clearFocus()
}

@Composable
fun InsurancePayerOptions(
    payers: List<InsurancePayer>,
    onSelectPayer: (InsurancePayer) -> Unit,
    onDismiss: () -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        hideKeyBoard()
        BackHandler {
            onDismiss()
        }
        Actionbar(
            title = "Select insurance payer",
            icon = Icons.Default.Close,
            actionIconClick = onDismiss
        )
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = Dimens.Spacing.large)
        ) {
            payers.forEach {
                Text(
                    text = it.name,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .clickable {
                            onSelectPayer(it)
                            onDismiss()
                        }
                        .padding(
                            vertical = Dimens.Spacing.medium,
                            horizontal = Dimens.Spacing.large
                        )
                )
            }
        }
    }
}


@Preview
@Composable
private fun PreviewInsuranceInput() {
    PreviewUi {
        val uiState = PaymentViewModel.UiState()
        Box(
            Modifier
                .fillMaxSize()
                .padding(Dimens.Spacing.small)
        ) {
            InsuranceInput(uiState = uiState, onShowPayerOption = {}, onPaymentSubmitted = {})
        }
    }
}

@Preview
@Composable
private fun PreviewInsurancePayer() {
    PreviewUi {
        val payers = listOf(
            InsurancePayer(
                payerId = "pmaor",
                name = "OR - Providence Medicare Advantage"
            ),
            InsurancePayer(
                payerId = "bcbsorma",
                name = "OR - Regence BCBS of Oregon Med Adavantage"
            ),
            InsurancePayer(
                payerId = "agmwa",
                name = "WA - Amerigroup Medicaid"
            ),
            InsurancePayer(
                payerId = "rppowa",
                name = "WA - Regence PPO"
            ),
            InsurancePayer(
                payerId = "uhcppowa",
                name = "WA - United HealthCare PPO"
            ),
        )
        InsurancePayerOptions(payers = payers, onSelectPayer = {}, onDismiss = {})
    }
}



