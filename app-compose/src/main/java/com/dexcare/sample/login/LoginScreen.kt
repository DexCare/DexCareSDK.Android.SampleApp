package com.dexcare.sample.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dexcare.sample.ui.components.FullScreen
import com.dexcare.sample.ui.components.SolidButton

@Composable
fun LoginScreen(navToDashBoard: () -> Unit) {
    FullScreen {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            SolidButton(text = "Login") {
                navToDashBoard()
            }
        }
    }
}
