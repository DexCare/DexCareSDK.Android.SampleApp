package com.dexcare.sample.presentation.payment

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dexcare.sample.ui.components.ActionBarScreen
import com.dexcare.sample.ui.components.SolidButton

@Composable
fun PaymentScreen() {
    ActionBarScreen(
        title = "Payment",
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            SolidButton(text = "Submit", onClick = { })
        }
    }
}
