package com.dexcare.sample.presentation.payment

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import com.dexcare.acme.android.R
import com.dexcare.sample.ui.components.AcmeCircularProgress
import com.dexcare.sample.ui.components.InputOptions
import com.dexcare.sample.ui.components.SolidButton
import com.dexcare.sample.ui.components.TertiaryButton
import com.dexcare.sample.ui.components.TextInput
import com.dexcare.sample.ui.theme.Dimens
import com.dexcare.sample.ui.theme.LocalAppColor
import com.dexcare.sample.ui.theme.PreviewUi
import org.dexcare.services.models.CouponCode

@Composable
fun CouponCodeInput(
    uiState: PaymentViewModel.UiState,
    onApplyCode: (String) -> Unit,
    onSubmitPayment: (CouponCode) -> Unit
) {
    Column(Modifier.padding(vertical = Dimens.Spacing.large)) {
        val serviceKeyInput = remember {
            mutableStateOf(uiState.couponCodeInput)
        }
        val requiredKeyMessage = remember {
            mutableStateOf<String?>(null)
        }
        Text(
            text = "A service key is a code provided by your employer, insurance or another group.",
            style = MaterialTheme.typography.bodyMedium
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.Spacing.xSmall),
        ) {
            TextInput(
                modifier = Modifier.weight(1f),
                input = serviceKeyInput,
                label = "Service Key",
                fillMaxWidth = false,
                error = requiredKeyMessage.value,
                keyboardOptions = InputOptions.text
            )
            if (uiState.isCouponVerificationInProgress) {
                AcmeCircularProgress(Modifier.padding(start = Dimens.Spacing.xLarge))
                hideKeyBoard()
            } else {
                TertiaryButton(
                    text = "Apply",
                    modifier = Modifier
                        .padding(start = Dimens.Spacing.xLarge),
                    onClick = {
                        if (serviceKeyInput.value.isNotBlank()) {
                            onApplyCode(serviceKeyInput.value)
                        } else {
                            requiredKeyMessage.value = "This is required"
                        }
                    })
            }
        }

        if (uiState.originalVisitCost != null && uiState.visitCostAfterCoupon !== null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_circle_check),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(LocalAppColor.current.success)
                )
                Text(
                    text = "Service key applied",
                    modifier = Modifier.padding(start = Dimens.Spacing.small),
                    style = MaterialTheme.typography.labelMedium,
                    color = LocalAppColor.current.success
                )
            }

            val cost = buildAnnotatedString {
                append("Your cost: ")
                pushStyle(SpanStyle(textDecoration = TextDecoration.LineThrough))
                append(uiState.originalVisitCost)
                pop()
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                append("  ")
                append(uiState.visitCostAfterCoupon)
            }
            Text(
                text = cost,
                modifier = Modifier.padding(top = Dimens.Spacing.small),
                style = MaterialTheme.typography.bodySmall
            )
        } else if (uiState.couponCodeError != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_error),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(LocalAppColor.current.error)
                )
                Text(
                    text = uiState.couponCodeError,
                    modifier = Modifier.padding(start = Dimens.Spacing.small),
                    style = MaterialTheme.typography.labelMedium,
                    color = LocalAppColor.current.error
                )
            }
        }

        SolidButton(
            text = "Continue",
            isEnabled = uiState.couponCodeInput.isNotBlank(),
            modifier = Modifier
                .padding(top = Dimens.Spacing.medium)
                .align(Alignment.CenterHorizontally),
        ) {
            onSubmitPayment(CouponCode(serviceKeyInput.value))
        }
    }
}


@Preview
@Composable
fun PreviewCouponCodeInput() {
    PreviewUi(
        Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(Dimens.Spacing.large)
    ) {
        CouponCodeInput(
            uiState = PaymentViewModel.UiState(
                couponCodeInput = "abc",
                isCouponVerificationInProgress = false,
                originalVisitCost = "\$50.00",
                visitCostAfterCoupon = "\$30.00",
                couponCodeError = "Invalid Service Key"
            ),
            onApplyCode = {},
            onSubmitPayment = {}
        )
    }
}
