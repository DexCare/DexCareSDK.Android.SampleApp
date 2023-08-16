package com.dexcare.sample.reasonforvisit

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.dexcare.sample.ui.components.ActionBarScreen

@Composable
fun ReasonForVisitScreen(onBackPressed: () -> Unit) {
    ActionBarScreen(
        title = "Reason for visit",
        onBackPressed = onBackPressed
    ) {
        Column {
            Text(text = "Reason for visit")
        }
    }
}
