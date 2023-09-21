package com.dexcare.sample.presentation.reasonforvisit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dexcare.sample.ui.components.ActionBarScreen
import com.dexcare.sample.ui.components.LongTextInput
import com.dexcare.sample.ui.components.SolidButton
import com.dexcare.sample.ui.theme.Dimens
import com.dexcare.sample.ui.theme.PreviewUi
import timber.log.Timber

@Composable
fun ReasonForVisitScreen(
    viewModel: ReasonForVisitViewModel, onBackPressed: () -> Unit, onContinue: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState().value
    ActionBarScreen(
        title = "Reason for visit", onBackPressed = onBackPressed
    ) {

        if (uiState.isValidReason) {
            LaunchedEffect(Unit) {
                onContinue()
            }
        }

        ReasonForVisitContent(uiState = uiState, onNext = {
            Timber.d("onContinue")
            viewModel.onReasonInput(it)
        })
    }
}

@Composable
fun ReasonForVisitContent(
    uiState: ReasonForVisitViewModel.UiState,
    onNext: (String) -> Unit,
) {
    Column(Modifier.padding(Dimens.Spacing.large)) {
        Text(
            text = "Tell us your reason for visit today",
            modifier = Modifier.padding(top = Dimens.Spacing.large)
        )
        val reason = remember {
            mutableStateOf(uiState.reason)
        }
        LongTextInput(
            modifier = Modifier
                .padding(vertical = Dimens.Spacing.large)
                .fillMaxWidth(),
            value = reason.value,
            error = uiState.error,
            onValueChange = {
                reason.value = it
            }
        )

        SolidButton(
            text = "Next",
            onClick = {
                onNext(reason.value)
                Timber.d("onNext")
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(Dimens.Spacing.large)
                .fillMaxWidth()
        )
    }
}

@Preview
@Composable
fun PreviewReasonForVisitContent() {
    PreviewUi {
        ReasonForVisitContent(ReasonForVisitViewModel.UiState(), onNext = {})
    }
}