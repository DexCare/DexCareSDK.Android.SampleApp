package com.dexcare.sample.presentation.practiceregion

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import com.dexcare.sample.ui.components.AcmeCircularProgress
import com.dexcare.sample.ui.components.ActionBarScreen
import com.dexcare.sample.ui.components.applyWhen
import com.dexcare.sample.ui.theme.Dimens
import com.dexcare.sample.ui.theme.LocalAppColor
import com.dexcare.sample.ui.theme.PreviewUi
import org.dexcare.services.virtualvisit.models.VirtualPracticeRegion

@Composable
fun PracticeRegionScreen(
    viewModel: PracticeRegionViewModel,
    onContinue: () -> Unit,
    onBackPressed: () -> Unit
) {
    ActionBarScreen(
        title = "Practice Regions",
        onBackPressed = onBackPressed
    ) {
        val uiState = viewModel.uiState.collectAsState().value
        PracticeRegionContent(
            uiState = uiState,
            onSelectRegion = {
                viewModel.selectRegion(it)
                onContinue()
            }
        )
    }
}

@Composable
private fun PracticeRegionContent(
    uiState: PracticeRegionViewModel.UiState,
    onSelectRegion: (VirtualPracticeRegion) -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        if (uiState.inProgress) {
            AcmeCircularProgress(Modifier.align(Alignment.Center))
        }

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(Dimens.Spacing.large)
        ) {
            Text(
                text = "Select a region for your visit",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = Dimens.Spacing.medium)
            )

            uiState.practiceRegions.forEach { region ->
                val isEnabled = !region.busy
                val colors = LocalAppColor.current
                Card(
                    Modifier
                        .padding(vertical = Dimens.Spacing.small)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .applyWhen(isEnabled) {
                                background(colors.primary)
                            }
                            .applyWhen(isEnabled) {
                                clickable {
                                    onSelectRegion(region)
                                }
                            }
                            .padding(
                                vertical = Dimens.Spacing.medium,
                                horizontal = Dimens.Spacing.large
                            )
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = region.displayName,
                                color = if (isEnabled) colors.light else Color.Unspecified,
                            )

                            Text(
                                text = "$ ${region.visitPrice}",
                                color = if (isEnabled) colors.light else Color.Unspecified,
                            )

                            if (region.busy) {
                                Text(
                                    text = region.busyMessage,
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            }
                        }
                        Image(
                            painter = rememberVectorPainter(image = Icons.AutoMirrored.Filled.KeyboardArrowRight),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(
                                color = if (isEnabled) colors.light else Color.Unspecified
                            ),
                        )
                    }

                }
            }
        }
    }
}


@Preview
@Composable
private fun PreviewPracticeRegionContent() {
    PreviewUi {
        val uiState = PracticeRegionViewModel.UiState(
            practiceRegions = listOf(
                VirtualPracticeRegion(
                    practiceRegionId = "",
                    displayName = "Montana",
                    regionCode = "",
                    pediatricsAgeRange = null,
                    practiceRegionAvailability = listOf(),
                    priceInCents = 0,
                    active = true,
                    busy = false,
                    busyMessage = "Providers are currently at capacity and closed",
                    departments = emptyList(),
                ),
                VirtualPracticeRegion(
                    practiceRegionId = "",
                    displayName = "Virginia",
                    regionCode = "",
                    pediatricsAgeRange = null,
                    practiceRegionAvailability = listOf(),
                    priceInCents = 0,
                    active = true,
                    busy = true,
                    busyMessage = "Providers are currently at capacity and closed",
                    departments = emptyList(),
                )
            )
        )
        PracticeRegionContent(uiState = uiState, onSelectRegion = {})
    }
}
