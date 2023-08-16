package com.dexcare.sample.demographics

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dexcare.sample.ui.components.ActionBarScreen
import com.dexcare.sample.ui.components.SolidButton

@Composable
fun DemographicsScreen(navContinue: () -> Unit) {
    ActionBarScreen(
        title = "Demographics",
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            SolidButton(text = "Submit demographics", onClick = navContinue)
        }
    }
}
