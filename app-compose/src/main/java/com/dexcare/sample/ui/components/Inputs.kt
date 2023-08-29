package com.dexcare.sample.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.dexcare.sample.ui.theme.Dimens
import com.dexcare.sample.ui.theme.LocalColorScheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextInput(
    input: MutableState<String>,
    modifier: Modifier = Modifier,
    coverMaxWidth: Boolean = true,
    hint: String? = null,
    error: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    val colors = LocalColorScheme.current
    Column(
        modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.Spacing.medium)
    ) {
        OutlinedTextField(
            value = input.value,
            onValueChange = {
                input.value = it
            },
            colors = if (error != null) {
                TextFieldDefaults.outlinedTextFieldColors(
                    textColor = colors.error,
                    errorBorderColor = colors.error
                )
            } else {
                TextFieldDefaults.outlinedTextFieldColors()
            },
            placeholder = {
                if (hint != null) {
                    Text(text = hint, style = MaterialTheme.typography.bodyMedium)
                }
            },
            modifier = Modifier.applyWhen(coverMaxWidth) {
                fillMaxWidth()
            },
            maxLines = 1,
            textStyle = MaterialTheme.typography.bodyMedium,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
        )
        if (error != null) {
            Text(
                text = error,
                color = colors.error,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

object InputOptions {
    val name = KeyboardOptions(
        capitalization = KeyboardCapitalization.Words,
        autoCorrect = false,
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Next
    )
    val email = KeyboardOptions(
        capitalization = KeyboardCapitalization.None,
        autoCorrect = true,
        keyboardType = KeyboardType.Email,
        imeAction = ImeAction.Next
    )

    val phoneNumber = KeyboardOptions(
        capitalization = KeyboardCapitalization.None,
        autoCorrect = true,
        keyboardType = KeyboardType.Phone,
        imeAction = ImeAction.Next
    )

    val ssn = KeyboardOptions(
        capitalization = KeyboardCapitalization.None,
        autoCorrect = false,
        keyboardType = KeyboardType.Number,
        imeAction = ImeAction.Next
    )

    val address = KeyboardOptions(
        capitalization = KeyboardCapitalization.None,
        autoCorrect = true,
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Next
    )

    val zipCode = KeyboardOptions(
        capitalization = KeyboardCapitalization.None,
        autoCorrect = true,
        keyboardType = KeyboardType.Number,
        imeAction = ImeAction.Done
    )
}
