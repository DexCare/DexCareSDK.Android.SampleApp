package com.dexcare.sample.presentation.payment

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dexcare.sample.presentation.LocalActivity
import com.dexcare.sample.presentation.provider.ProgressMessage
import com.dexcare.sample.ui.components.ActionBarScreen
import com.dexcare.sample.ui.components.InformationScreen
import com.dexcare.sample.ui.theme.Dimens
import com.dexcare.sample.ui.theme.LocalColorScheme
import com.dexcare.sample.ui.theme.PreviewUi
import org.dexcare.services.models.PaymentMethod

@Composable
fun PaymentScreen(
    viewModel: PaymentViewModel,
    onBackPressed: () -> Unit,
    onExit: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState().value
    ActionBarScreen(
        title = "Payment",
        onBackPressed = onBackPressed
    ) {
        val activity = LocalActivity.current
        if (uiState.error != null) {
            InformationScreen(
                title = uiState.error.title,
                message = uiState.error.message,
                showTopBar = false
            ) {
                onBackPressed()
            }
        } else if (uiState.providerBookingComplete) {
            InformationScreen(
                title = "Booking complete",
                message = "Your appointment has been set up.",
                showTopBar = false
            ) {
                onExit()
            }
        } else {
            Box {
                PaymentContent(
                    onSubmit = {
                        viewModel.onSubmit(activity, it)
                    }
                )
                if (uiState.loading) {
                    ProgressMessage()
                }
            }
        }
    }
}

@Composable
fun PaymentContent(onSubmit: (PaymentMethod) -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(Dimens.Spacing.large)
    ) {
        val selectedMethod = remember {
            mutableStateOf(PaymentMethod.PaymentMethod.CreditCard)
        }
        PaymentOptionInput(onInputSelected = { method ->
            selectedMethod.value = method
        })

        when (selectedMethod.value) {
            PaymentMethod.PaymentMethod.CreditCard -> {
                CreditCardInput(
                    onCardSubmit = {
                        onSubmit(it)
                    }
                )
            }

            PaymentMethod.PaymentMethod.Insurance -> {

            }

            PaymentMethod.PaymentMethod.CouponCode -> {

            }

            else -> {

            }
        }
    }
}

@Composable
fun PaymentOptionInput(onInputSelected: (PaymentMethod.PaymentMethod) -> Unit) {
    Column {
        val colors = LocalColorScheme.current
        Text(text = "Select a payment method")

        val showDropdown = remember {
            mutableStateOf(false)
        }

        val selectedMethod = remember {
            mutableStateOf(PaymentMethod.PaymentMethod.CreditCard)
        }

        Row(Modifier
            .padding(vertical = Dimens.Spacing.medium)
            .fillMaxWidth()
            .clickable { showDropdown.value = !showDropdown.value }
            .border(width = 1.dp, color = colors.primary, shape = RoundedCornerShape(8.dp))
            .padding(Dimens.Spacing.small)

        ) {

            val text = when (selectedMethod.value) {
                PaymentMethod.PaymentMethod.CreditCard -> "Credit Card"
                PaymentMethod.PaymentMethod.Insurance -> "Insurance"
                PaymentMethod.PaymentMethod.CouponCode -> "Coupon Code"
                PaymentMethod.PaymentMethod.Self -> "In Person"
            }

            Text(
                text = text, modifier = Modifier
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
                PaymentOption(
                    title = "Credit Card",
                    isSelected = selectedMethod.value == PaymentMethod.PaymentMethod.CreditCard
                ) {
                    selectedMethod.value = PaymentMethod.PaymentMethod.CreditCard
                    showDropdown.value = false
                    onInputSelected(PaymentMethod.PaymentMethod.CreditCard)
                }
                PaymentOption(
                    title = "Insurance",
                    isSelected = selectedMethod.value == PaymentMethod.PaymentMethod.Insurance
                ) {
                    selectedMethod.value = PaymentMethod.PaymentMethod.Insurance
                    showDropdown.value = false
                    onInputSelected(PaymentMethod.PaymentMethod.Insurance)
                }
                PaymentOption(
                    title = "Coupon Code",
                    isSelected = selectedMethod.value == PaymentMethod.PaymentMethod.CouponCode
                ) {
                    selectedMethod.value = PaymentMethod.PaymentMethod.CouponCode
                    showDropdown.value = false
                    onInputSelected(PaymentMethod.PaymentMethod.CouponCode)
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
                colorFilter = ColorFilter.tint(Color.Green),
                contentDescription = null
            )
        }
    }
}


@Preview
@Composable
fun PreviewPaymentContent() {
    PreviewUi {
        PaymentContent(onSubmit = {})
    }
}
