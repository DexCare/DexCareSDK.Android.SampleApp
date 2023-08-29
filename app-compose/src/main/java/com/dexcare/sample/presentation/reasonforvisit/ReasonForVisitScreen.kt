package com.dexcare.sample.presentation.reasonforvisit

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dexcare.sample.ui.components.ActionBarScreen
import com.dexcare.sample.ui.components.SolidButton
import com.dexcare.sample.ui.theme.Dimens
import com.dexcare.sample.ui.theme.LocalColorScheme
import com.dexcare.sample.ui.theme.PreviewUi
import timber.log.Timber

@Composable
fun ReasonForVisitScreen(
    viewModel: ReasonForVisitViewModel,
    onBackPressed: () -> Unit,
    onContinue: () -> Unit
) {
    ActionBarScreen(
        title = "Reason for visit",
        onBackPressed = onBackPressed
    ) {
        ReasonForVisitContent(
            onReasonUpdate = {
                viewModel.onReasonInput(it)
            },
            onNext = {
                Timber.d("onContinue")
                onContinue()
            }
        )
    }
}

@Composable
fun ReasonForVisitContent(
    onReasonUpdate: (String) -> Unit,
    onNext: () -> Unit,
) {
    Column(Modifier.padding(Dimens.Spacing.large)) {
        Text(
            text = "Tell us your reason for visit today",
            modifier = Modifier.padding(top = Dimens.Spacing.large)
        )
        val reason = remember {
            mutableStateOf("")
        }
        LongTextInput(
            modifier = Modifier
                .padding(vertical = Dimens.Spacing.large)
                .fillMaxWidth(),
            value = reason.value,
            onValueChange = {
                reason.value = it
            }
        )

        SolidButton(
            text = "Next",
            onClick = {
                onReasonUpdate(reason.value)
                onNext()
                Timber.d("onNext")
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(Dimens.Spacing.large)
                .fillMaxWidth()
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LongTextInput(value: String, modifier: Modifier = Modifier, onValueChange: (String) -> Unit) {
    val colors = LocalColorScheme.current
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
        textStyle = MaterialTheme.typography.bodySmall
    )
}


@Preview
@Composable
fun PreviewReasonForVisitContent() {
    PreviewUi {
        ReasonForVisitContent({}, {})
    }
}
