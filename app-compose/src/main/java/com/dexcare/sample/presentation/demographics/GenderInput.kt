package com.dexcare.sample.presentation.demographics

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.dexcare.sample.ui.theme.Dimens
import com.dexcare.sample.ui.theme.LocalAppColor
import com.dexcare.sample.ui.theme.PreviewUi
import org.dexcare.services.patient.models.Gender

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
                .background(LocalAppColor.current.surface)
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
