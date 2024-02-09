package com.dexcare.sample.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.dexcare.sample.ui.theme.Dimens
import com.dexcare.sample.ui.theme.PreviewUi

@Composable
fun SelectionOption(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    onSelect: () -> Unit
) {
    Card(modifier = modifier
        .fillMaxWidth()
    ) {
        Column(
            Modifier
                .clickable { onSelect() }
                .padding(Dimens.Spacing.medium)
                .semantics(mergeDescendants = true) { }) {
            Text(text = title, style = MaterialTheme.typography.titleLarge)
            Text(text = description, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Preview
@Composable
private fun PreviewSelectionOption() {
    PreviewUi {
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens.Spacing.medium),
            modifier = Modifier.padding(Dimens.Spacing.large)
        ) {
            SelectionOption(
                title = "Provider",
                description = "Schedule your appointment with the provider"
            ) { }

            SelectionOption(
                title = "Retail visit",
                description = "Schedule your at one of the physical clinics. Select a date and time that works for you."
            ) { }
        }
    }

}
