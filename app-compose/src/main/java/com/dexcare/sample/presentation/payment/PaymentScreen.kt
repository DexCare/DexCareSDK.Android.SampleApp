package com.dexcare.sample.presentation.payment

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dexcare.sample.data.virtualvisit.VirtualVisitContract
import com.dexcare.sample.presentation.LocalActivity
import com.dexcare.sample.ui.components.ActionBarScreen
import com.dexcare.sample.ui.components.FullScreenProgress
import com.dexcare.sample.ui.components.InformationScreen
import com.dexcare.sample.ui.theme.Dimens
import com.dexcare.sample.ui.theme.LocalAppColor
import com.dexcare.sample.ui.theme.PreviewUi
import org.dexcare.services.models.PaymentMethod
import timber.log.Timber

/**
 * Screen to collect different types of payment as supported by the system.
 * See here: https://developers.dexcarehealth.com/mobilesdk/paymentmethod/
 * */
@Composable
fun PaymentScreen(
    viewModel: PaymentViewModel,
    onBackPressed: () -> Unit,
    onExit: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState().value
    val showInsurancePayer = remember {
        mutableStateOf(false)
    }

    val visitLauncher =
        rememberLauncherForActivityResult(VirtualVisitContract.LaunchVisit()) { resultCode ->
            Timber.d("visit ended with result code $resultCode")
            onExit()
        }

    if (uiState.visitIntent != null) {
        LaunchedEffect(key1 = uiState.visitIntent) {
            visitLauncher.launch(uiState.visitIntent)
        }
    }

    when {
        uiState.loading -> {
            FullScreenProgress(uiState.loadingMessage.orEmpty())
        }

        uiState.error != null -> {
            InformationScreen(
                title = uiState.error.title,
                message = uiState.error.message,
            ) {
                onBackPressed()
            }
        }

        uiState.retailBookingComplete || uiState.providerBookingComplete -> {
            InformationScreen(
                title = "Booking complete",
                message = "Your appointment has been set up.",
            ) {
                onExit()
            }
        }

        showInsurancePayer.value -> {
            InsurancePayerOptions(
                payers = uiState.insurancePayers,
                onSelectPayer = {
                    viewModel.onSelectInsurancePayer(it)
                }, onDismiss = {
                    showInsurancePayer.value = false
                }
            )
        }

        else -> {
            val activity = LocalActivity.current
            ActionBarScreen(
                title = "Payment",
                onBackPressed = onBackPressed
            ) {
                Box {
                    PaymentContent(
                        uiState = uiState,
                        onSubmit = {
                            viewModel.onSubmit(activity, it)
                        },
                        onShowInsurancePayers = {
                            showInsurancePayer.value = true
                        },
                        onPaymentTypeSelected = {
                            viewModel.onPaymentTypeSelected(it)
                        },
                        onApplyCouponCode = {
                            viewModel.onApplyCouponCode(it)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PaymentContent(
    uiState: PaymentViewModel.UiState,
    onShowInsurancePayers: () -> Unit,
    onSubmit: (PaymentMethod) -> Unit,
    onPaymentTypeSelected: (PaymentMethod.PaymentMethod) -> Unit,
    onApplyCouponCode: (String) -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(Dimens.Spacing.large)
    ) {

        PaymentOptionInput(
            supportedMethods = uiState.supportedPaymentMethods,
            selectedMethod = uiState.selectedPaymentType,
            onInputSelected = { method ->
                onPaymentTypeSelected(method)
            }
        )

        when (uiState.selectedPaymentType) {
            PaymentMethod.PaymentMethod.CreditCard -> {
                CreditCardInput(
                    onCardSubmit = {
                        onSubmit(it)
                    }
                )
            }

            PaymentMethod.PaymentMethod.Insurance -> {
                hideKeyBoard()
                InsuranceInput(
                    uiState = uiState,
                    onShowPayerOption = onShowInsurancePayers,
                    onPaymentSubmitted = {
                        onSubmit(it)
                    }
                )
            }

            PaymentMethod.PaymentMethod.CouponCode -> {
                CouponCodeInput(
                    uiState = uiState,
                    onApplyCode = onApplyCouponCode,
                    onSubmitPayment = onSubmit,
                )
            }

            else -> {

            }
        }
    }
}

@Composable
fun PaymentOptionInput(
    supportedMethods: List<PaymentMethod.PaymentMethod>,
    selectedMethod: PaymentMethod.PaymentMethod,
    onInputSelected: (PaymentMethod.PaymentMethod) -> Unit
) {
    Column {
        val colors = LocalAppColor.current
        Text(text = "Select a payment method")

        val showDropdown = remember {
            mutableStateOf(false)
        }

        Row(Modifier
            .padding(vertical = Dimens.Spacing.medium)
            .fillMaxWidth()
            .clickable { showDropdown.value = !showDropdown.value }
            .border(width = 1.dp, color = colors.primary, shape = RoundedCornerShape(8.dp))
            .padding(Dimens.Spacing.small)

        ) {

            Text(
                text = selectedMethod.displayLabel(),
                modifier = Modifier
                    .padding(start = Dimens.Spacing.medium)
                    .weight(1f)
            )
            Image(
                painter = rememberVectorPainter(image = Icons.Default.KeyboardArrowDown),
                contentDescription = null
            )
        }

        AnimatedVisibility(visible = showDropdown.value) {
            Column(Modifier.padding(start = Dimens.Spacing.medium.plus(Dimens.Spacing.small))) {
                supportedMethods.forEach { paymentMethodType ->
                    PaymentOption(
                        title = paymentMethodType.displayLabel(),
                        isSelected = selectedMethod == paymentMethodType
                    ) {
                        showDropdown.value = false
                        onInputSelected(paymentMethodType)
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentOption(title: String, isSelected: Boolean, onSelect: () -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable { onSelect() }
        .padding(horizontal = Dimens.Spacing.small, vertical = Dimens.Spacing.xSmall),
        verticalAlignment = Alignment.CenterVertically) {
        Text(text = title, modifier = Modifier.padding(end = Dimens.Spacing.x2Large))
        if (isSelected) {
            Image(
                painter = rememberVectorPainter(image = Icons.Default.Check),
                colorFilter = ColorFilter.tint(LocalAppColor.current.success),
                contentDescription = null
            )
        }
    }
}


@Preview
@Composable
fun PreviewPaymentContent() {
    PreviewUi {
        PaymentContent(
            uiState = PaymentViewModel.UiState(
                supportedPaymentMethods = listOf(
                    PaymentMethod.PaymentMethod.CreditCard,
                    PaymentMethod.PaymentMethod.Insurance,
                    PaymentMethod.PaymentMethod.CouponCode
                ),
                selectedPaymentType = PaymentMethod.PaymentMethod.CouponCode,
            ),
            onShowInsurancePayers = {},
            onSubmit = {},
            onPaymentTypeSelected = {},
            onApplyCouponCode = {}
        )
    }
}
