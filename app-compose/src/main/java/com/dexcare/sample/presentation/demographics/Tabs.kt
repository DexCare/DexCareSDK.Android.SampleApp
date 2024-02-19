package com.dexcare.sample.presentation.demographics

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dexcare.sample.ui.theme.LocalAppColor

@Composable
fun Tabs(
    selectedTabPosition: Int,
    onSelfPatientSelect: () -> Unit,
    onOtherPatientSelect: () -> Unit,
) {
    val colors = LocalAppColor.current

    TabRow(
        selectedTabIndex = selectedTabPosition,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedTabPosition]),
                height = 2.dp,
                color = colors.primary
            )
        },
        divider = {
            Divider(modifier = Modifier.shadow(4.dp), color = colors.primary)
        },
        modifier = Modifier.clip(RoundedCornerShape(8.dp)),
    ) {
        val isSelfPatient = selectedTabPosition == 0
        val isPatientSomeoneElse = selectedTabPosition == 1
        Tab(
            modifier = Modifier.background(
                color = if (isSelfPatient) {
                    colors.primaryDark
                } else {
                    colors.primary
                }
            ),
            selected = isSelfPatient,
            onClick = { onSelfPatientSelect() },
            text = {
                TabTitle(
                    "My Self",
                )
            },
            selectedContentColor = Color.White,
            unselectedContentColor = Color.White,
        )

        Tab(
            modifier = Modifier.background(
                color = if (isPatientSomeoneElse) {
                    colors.primaryDark
                } else {
                    colors.primary
                }
            ),
            selected = isPatientSomeoneElse,
            onClick = { onOtherPatientSelect() },
            text = {
                TabTitle(
                    "Someone else",
                )
            },
            selectedContentColor = Color.White,
            unselectedContentColor = Color.White
        )

    }
}

@Composable
fun TabTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.bodySmall,
    )
}
