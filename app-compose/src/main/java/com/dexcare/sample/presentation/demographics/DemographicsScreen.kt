package com.dexcare.sample.presentation.demographics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dexcare.sample.ui.components.ActionBarScreen
import com.dexcare.sample.ui.components.InputOptions
import com.dexcare.sample.ui.components.SolidButton
import com.dexcare.sample.ui.components.TextInput
import com.dexcare.sample.ui.theme.Dimens
import com.dexcare.sample.ui.theme.LocalColorScheme
import com.dexcare.sample.ui.theme.PreviewUi

@Composable
fun DemographicsScreen(viewModel: DemographicsViewModel, navContinue: () -> Unit) {
    ActionBarScreen(
        title = "Demographics",
    ) {
        val uiState = viewModel.uiState.collectAsState().value
        DemographicsContent(
            uiState = uiState,
            onSubmit = {
                viewModel.onSubmit(it)
            },
            onNavContinue = {
                viewModel.onNavigationComplete()
                navContinue()
            }
        )
    }
}

@Composable
fun DemographicsContent(
    uiState: DemographicsViewModel.UiState,
    onSubmit: (input: DemographicsInput) -> Unit,
    onNavContinue: () -> Unit,
) {
    if (uiState.inputComplete) {
        LaunchedEffect(Unit) {
            onNavContinue()
        }
    }
    Column(
        Modifier
            .padding(Dimens.Spacing.large)
    ) {
        Text(
            text = "Who is this visit for?",
            modifier = Modifier.padding(vertical = Dimens.Spacing.small),
            style = MaterialTheme.typography.bodyMedium
        )

        val selectedPosition = rememberSaveable { mutableStateOf(0) }

        Tabs(
            0,
            onSelfPatientSelect = {
                selectedPosition.value = 0
            },
            onOtherPatientSelect = {
                selectedPosition.value = 1
            }
        )

        if (selectedPosition.value == 0) {
            MySelfTab(uiState.patientDemographicsInput, onSubmit)
        } else {
            SomeoneElseTab()
        }
    }
}


@Composable
fun Tabs(
    selectedTabPosition: Int,
    onSelfPatientSelect: () -> Unit,
    onOtherPatientSelect: () -> Unit,
) {
    val colors = LocalColorScheme.current

    TabRow(
        selectedTabIndex = selectedTabPosition,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedTabPosition]),
                height = 2.dp,
                color = colors.primaryContainer
            )
        },
        divider = {
            Divider(modifier = Modifier.shadow(4.dp), color = colors.primaryContainer)
        }
    ) {
        val isSelfPatient = selectedTabPosition == 0
        val isPatientSomeoneElse = selectedTabPosition == 1
        Tab(
            modifier = Modifier.background(
                color = if (isSelfPatient) {
                    colors.primary
                } else {
                    Color.White
                }
            ),
            selected = isSelfPatient,
            onClick = { onSelfPatientSelect() },
            text = {
                TabTitle(
                    "My Self",
                )
            },
            selectedContentColor = Color.White,
            unselectedContentColor = colors.tertiary
        )

        Tab(
            modifier = Modifier.background(
                color = if (isPatientSomeoneElse) {
                    colors.primary
                } else {
                    Color.White
                }
            ),
            selected = isPatientSomeoneElse,
            onClick = { onOtherPatientSelect() },
            text = {
                TabTitle(
                    "Someone else",
                )
            },
            selectedContentColor = Color.White,
            unselectedContentColor = colors.tertiary
        )


    }
}

@Composable
fun TabTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.bodySmall,
    )
}

@Composable
fun MySelfTab(currentInput: DemographicsInput, onSubmit: (DemographicsInput) -> Unit) {
    Column {
        val firstName = rememberSaveable {
            mutableStateOf(currentInput.firstName.input.orEmpty())
        }
        val lastName = rememberSaveable {
            mutableStateOf(currentInput.lastName.input.orEmpty())
        }
        val email = rememberSaveable {
            mutableStateOf("")
        }
        val dateOfBirth = rememberSaveable {
            mutableStateOf("")
        }
        val gender = rememberSaveable {
            mutableStateOf("")
        }
        val last4Ssn = rememberSaveable {
            mutableStateOf("")
        }
        val phoneNumber = rememberSaveable {
            mutableStateOf("")
        }
        val address = rememberSaveable {
            mutableStateOf("")
        }
        val address2 = rememberSaveable {
            mutableStateOf("")
        }
        val city = rememberSaveable {
            mutableStateOf("")
        }
        val state = rememberSaveable {
            mutableStateOf("")
        }
        val zipCode = rememberSaveable {
            mutableStateOf("")
        }
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {

            TextInput(
                input = firstName,
                hint = "First Name",
                error = currentInput.firstName.error,
                keyboardOptions = InputOptions.name,
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
                keyboardOptions = InputOptions.email
            )
            TextInput(
                input = dateOfBirth,
                hint = "Date of Birth",
            )
            TextInput(
                input = gender,
                hint = "Gender"
            )
            TextInput(
                input = last4Ssn,
                hint = "Last 4 SSN",
                keyboardOptions = InputOptions.ssn
            )
            TextInput(
                input = phoneNumber,
                hint = "Phone Number",
                keyboardOptions = InputOptions.phoneNumber,
            )

            //Address
            TextInput(
                input = address,
                hint = "Address",
                keyboardOptions = InputOptions.address
            )
            TextInput(
                input = address2,
                hint = "Address  Line 2 (Optional)",
                keyboardOptions = InputOptions.address,
            )
            TextInput(
                input = city,
                hint = "City",
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
                    keyboardOptions = InputOptions.address,
                    coverMaxWidth = false
                )
                TextInput(
                    modifier = Modifier.weight(1f),
                    input = zipCode,
                    hint = "Zip Code",
                    keyboardOptions = InputOptions.zipCode,
                    coverMaxWidth = false
                )
            }
        }

        SolidButton(text = "Next", modifier = Modifier.fillMaxWidth()) {
            val input = DemographicsInput.initialize()
                .withFirstName(firstName.value)
                .withLastName(lastName.value)
            onSubmit(input)
        }
    }
}

@Composable
fun SomeoneElseTab() {

}

@Preview
@Composable
fun PreviewDemographicsContent() {
    PreviewUi {
        val uiState = DemographicsViewModel.UiState()
        DemographicsContent(
            uiState,
            onSubmit = {},
            onNavContinue = {}
        )
    }
}
