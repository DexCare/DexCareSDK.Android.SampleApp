package com.dexcare.sample.provider

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dexcare.sample.ui.components.ActionBarScreen
import com.dexcare.sample.ui.components.SolidButton

@Composable
fun ProviderScreen(onBackPressed: () -> Unit, navContinue: () -> Unit) {
    ActionBarScreen(
        title = "Provider",
        onBackPressed = onBackPressed
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            SolidButton(text = "Select Provider", onClick = navContinue)
        }
    }
}
