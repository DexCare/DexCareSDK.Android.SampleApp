package com.dexcare.sample.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.dexcare.sample.ui.theme.Dimens
import com.dexcare.sample.ui.theme.LocalColorScheme

@Suppress("LongParameterList")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextInput(
    input: MutableState<String>,
    modifier: Modifier = Modifier,
    fillMaxWidth: Boolean = true,
    label: String,
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
            isError = error != null,
            colors = if (error != null) {
                OutlinedTextFieldDefaults.colors(
                    focusedTextColor = colors.error,
                    errorBorderColor = colors.error,
                    errorTextColor = colors.error
                )
            } else {
                OutlinedTextFieldDefaults.colors()
            },
            label = {
                Text(text = label, style = MaterialTheme.typography.bodyMedium)
            },
            modifier = Modifier.applyWhen(fillMaxWidth) {
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


@Composable
fun ClickableTextInput(
    input: String,
    modifier: Modifier = Modifier,
    label: String? = null,
    error: String? = null,
    onClick: () -> Unit,
) {
    val colors = LocalColorScheme.current
    Column(
        modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.Spacing.medium)
    ) {
        OutlinedTextField(
            value = input,
            enabled = false,
            isError = error != null,
            onValueChange = {
            },
            colors = if (error != null) {
                OutlinedTextFieldDefaults.colors(
                    focusedTextColor = colors.error,
                    errorBorderColor = colors.error,
                    errorTextColor = colors.error
                )
            } else {
                OutlinedTextFieldDefaults.colors(
                    disabledTextColor = colors.onSurface,
                    disabledBorderColor = colors.outline,
                    disabledLabelColor = colors.onSurface,
                )
            },
            label = {
                Text(text = label.orEmpty(), style = MaterialTheme.typography.bodyMedium)
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            maxLines = 1,
            textStyle = MaterialTheme.typography.bodyMedium,
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LongTextInput(
    value: String,
    modifier: Modifier = Modifier,
    hint: String? = null,
    error: String? = null,
    onValueChange: (String) -> Unit
) {
    val colors = LocalColorScheme.current
    Column {
        TextField(
            value = value,
            onValueChange = onValueChange,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                cursorColor = colors.primary,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = modifier
                .sizeIn(minHeight = 120.dp)
                .border(width = 2.dp, color = colors.primary, RoundedCornerShape(size = 8.dp)),
            textStyle = MaterialTheme.typography.bodyMedium,
            placeholder = {
                Text(text = hint.orEmpty())
            }
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
