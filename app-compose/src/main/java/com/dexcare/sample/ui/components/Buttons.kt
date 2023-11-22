package com.dexcare.sample.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dexcare.sample.ui.theme.Dimens
import com.dexcare.sample.ui.theme.PreviewUi

@Composable
fun SolidButton(
    text: String,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier.sizeIn(Dimens.accessibleSize),
        enabled = isEnabled,
        onClick = onClick
    ) {
        Text(text = text)
    }
}

@Composable
fun SecondaryButton(
    text: String,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.sizeIn(Dimens.accessibleSize),
        enabled = isEnabled
    ) {
        Text(text = text)
    }
}

@Composable
fun TertiaryButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(text = text)
    }
}

@Preview
@Composable
private fun PreviewButtons() {
    PreviewUi {
        Column(
            Modifier
                .fillMaxSize()
                .padding(Dimens.Spacing.medium)
        ) {
            SolidButton("Primary Button", onClick = {})
            SolidButton("Primary Button Disabled", isEnabled = false, onClick = {})

            SecondaryButton(text = "Secondary Button", onClick = {})
            SecondaryButton(text = "Secondary Button Disabled", isEnabled = false, onClick = {})

            TertiaryButton(text = "Tertiary Button", onClick = {})
        }
    }
}
