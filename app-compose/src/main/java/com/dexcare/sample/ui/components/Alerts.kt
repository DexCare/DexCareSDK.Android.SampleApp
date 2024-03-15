package com.dexcare.sample.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.dexcare.sample.ui.theme.Dimens
import com.dexcare.sample.ui.theme.PreviewUi

/**
 * Simplified version of [Alert] where title, message and a single button is used.
 *
 * */
@Composable
fun SimpleAlert(
    title: String,
    message: String,
    buttonText: String,
    enabledState: MutableState<Boolean>,
    actionAlertClosed: (() -> Unit) = {}
) {
    Alert(
        title = title,
        message = message,
        confirmText = buttonText,
        enabledState = enabledState,
        onConfirmAction = actionAlertClosed,
        onDismiss = actionAlertClosed
    )
}

/**
 * Shows an AlertDialog.
 * Title and message both left aligned.
 *
 * @param title the title for alert
 * @param message the message body for alert
 * @param confirmText the CTA text for confirm option.
 * @param dismissText the CTA text for dismiss option. This is optional.
 * @param enabledState mutable boolean state which controls whether alert should be shown/dismissed.
 * @param dialogProperties [DialogProperties]
 * @param onConfirmAction the click listener for confirm action.
 * @param onDismissAction the click listener for dismiss action.
 * [dismissText] is required for this to work.
 * @param onDismiss Callback when dialog is dismissed. e.g with touch outside of dialog.
 *
 */
@Composable
fun Alert(
    title: String,
    message: String,
    confirmText: String,
    dismissText: String? = null,
    enabledState: MutableState<Boolean>,
    dialogProperties: DialogProperties = DialogProperties(),
    onConfirmAction: (() -> Unit) = {},
    onDismissAction: (() -> Unit) = {},
    onDismiss: () -> Unit = {},
) {
    if (enabledState.value) {
        val typography = MaterialTheme.typography
        AlertDialog(
            onDismissRequest = {
                enabledState.value = false
                onDismiss()
            },
            confirmButton = {
                TertiaryButton(
                    text = confirmText,
                    onClick = onConfirmAction
                )
            },
            dismissButton = {
                if (dismissText != null) {
                    TertiaryButton(
                        text = dismissText,
                        onClick = onDismissAction
                    )
                }
            },
            title = {
                Text(
                    text = title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.Spacing.medium),
                    textAlign = TextAlign.Start,
                    style = typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text(
                    text = message,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.Spacing.medium),
                    textAlign = TextAlign.Left,
                    style = typography.bodyMedium,
                )
            },
            properties = dialogProperties
        )
    }
}

@Composable
@Preview
fun PreviewAlert() {
    PreviewUi(
        Modifier
            .size(width = 550.dp, height = 350.dp)
            .padding(20.dp)) {
        val enabled = remember { mutableStateOf(true) }
        SimpleAlert(
            title = "Missing information",
            message = "Your date of birth is required",
            buttonText = "Ok",
            enabledState = enabled
        )
    }
}
