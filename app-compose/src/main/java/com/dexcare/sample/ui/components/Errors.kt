package com.dexcare.sample.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.dexcare.sample.data.ErrorResult
import com.dexcare.sample.ui.theme.Dimens


@Composable
fun ErrorScreen(error: ErrorResult) {
    FullScreen {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.Spacing.x2Large),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = error.title,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dimens.Spacing.large),
                text = error.message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
@Preview
fun PreviewErrorScreen() {
    ErrorScreen(
        error = ErrorResult(
            title = "Configuration error",
            message = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris maximus quis nisi vitae euismod."
        )
    )
}
