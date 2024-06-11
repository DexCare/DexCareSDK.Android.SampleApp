package com.dexcare.sample.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dexcare.sample.ui.theme.Dimens
import com.dexcare.sample.ui.theme.LocalAppColor
import com.dexcare.sample.ui.theme.PreviewUi

@Composable
fun SolidButton(
    text: String,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    onClick: () -> Unit
) {
    val colors = LocalAppColor.current
    Button(
        modifier = modifier.sizeIn(Dimens.accessibleSize),
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.primary
        ),
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
    val colors = LocalAppColor.current
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .sizeIn(Dimens.accessibleSize),
        enabled = isEnabled,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = colors.primary
        ),
        border = if (isEnabled) BorderStroke(
            width = 1.dp,
            color = colors.primary
        ) else ButtonDefaults.outlinedButtonBorder
    ) {
        Text(text = text)
    }
}

@Composable
fun TertiaryButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val colors = LocalAppColor.current
    TextButton(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.textButtonColors(
            contentColor = colors.primary
        ),
        contentPadding = PaddingValues()
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
