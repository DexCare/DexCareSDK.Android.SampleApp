package com.dexcare.sample.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
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
    Card(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .clickable { onSelect() }
                .padding(Dimens.Spacing.medium)
                .semantics(mergeDescendants = true) { }) {
            Text(text = title, style = MaterialTheme.typography.titleLarge)
            Text(text = description, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun ListItem(title: String, description: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxWidth()
    ) {
        Row(
            Modifier
                .padding(Dimens.Spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null
            )
        }

        Spacer(modifier = Modifier.heightIn(Dimens.Spacing.medium))
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

@Preview
@Composable
private fun PreviewListItem() {
    PreviewUi {
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens.Spacing.medium),
            modifier = Modifier.padding(Dimens.Spacing.large)
        ) {
            ListItem(
                title = "Provider",
                description = "Schedule your appointment with the provider"
            ) { }

            ListItem(
                title = "Retail visit",
                description = "Schedule your at one of the physical clinics. Select a date and time that works for you."
            ) { }
        }
    }
}
