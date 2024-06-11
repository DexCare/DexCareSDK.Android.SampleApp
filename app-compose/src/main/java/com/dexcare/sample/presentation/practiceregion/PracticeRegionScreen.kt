package com.dexcare.sample.presentation.practiceregion

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dexcare.sample.ui.components.AcmeCircularProgress
import com.dexcare.sample.ui.components.ActionBarScreen
import com.dexcare.sample.ui.components.Actionbar
import com.dexcare.sample.ui.components.FullScreen
import com.dexcare.sample.ui.components.SolidButton
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
    val uiState = viewModel.uiState.collectAsState().value

    Box(Modifier.fillMaxSize()) {
        ActionBarScreen(
            title = "Practice Regions",
            onBackPressed = onBackPressed
        ) {
            PracticeRegionContent(
                uiState = uiState,
                onToggleList = {
                    viewModel.onToggleListDisplay(it)
                },
                onContinue = onContinue
            )
        }

        AnimatedVisibility(
            visible = uiState.displaySelectionList,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            RegionSelectionView(
                practiceRegions = uiState.practiceRegions,
                selectedRegion = uiState.selectedRegion,
                onSelect = {
                    viewModel.selectRegion(it)
                },
                onBackPressed = {
                    viewModel.onToggleListDisplay(false)
                }
            )
        }
    }
}

@Composable
private fun PracticeRegionContent(
    uiState: PracticeRegionViewModel.UiState,
    onToggleList: (Boolean) -> Unit,
    onContinue: () -> Unit,
) {
    val colors = LocalAppColor.current
    Box(Modifier.fillMaxSize()) {
        if (uiState.inProgress) {
            AcmeCircularProgress(Modifier.align(Alignment.Center))
        } else {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(Dimens.Spacing.large)
            ) {
                Text(
                    text = "Requesting a Virtual Visit",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = Dimens.Spacing.medium),
                )

                Text(
                    text = "To continue, please choose the state where you're currently located:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = Dimens.Spacing.medium)
                )

                Text(
                    text = "Choose current state (required)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.primary,
                    modifier = Modifier.padding(bottom = Dimens.Spacing.medium)
                )

                DropDownInput(
                    modifier = Modifier.fillMaxWidth(),
                    selectedText = uiState.selectedRegion?.displayName ?: "Select state",
                    isEnabled = true,
                    onClick = {
                        onToggleList(true)
                    })
                SolidButton(
                    text = "Continue",
                    isEnabled = uiState.selectedRegion != null,
                    modifier = Modifier
                        .padding(
                            vertical = Dimens.Spacing.large,
                            horizontal = Dimens.Spacing.x1Large
                        )
                        .fillMaxWidth()
                ) {
                    onContinue()
                }
            }
        }
    }
}

@Composable
fun DropDownInput(
    modifier: Modifier = Modifier,
    selectedText: String,
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    val colors = LocalAppColor.current
    val inputShape = RoundedCornerShape(8.dp)
    Row(
        modifier = modifier
            .sizeIn(Dimens.accessibleSize)
            .background(
                color = if (isEnabled) colors.accent.copy(alpha = 0.4f) else
                    colors.accent.copy(alpha = 0.2f), shape = inputShape
            )
            .clip(inputShape)
            .applyWhen(isEnabled) {
                clickable { onClick() }
            }
            .padding(horizontal = Dimens.Spacing.small, vertical = Dimens.Spacing.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = selectedText,
            modifier = Modifier.padding(start = Dimens.Spacing.xSmall),
            style = MaterialTheme.typography.bodyLarge,
            color = if (isEnabled) colors.textPrimary else colors.textPrimary.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Filled.KeyboardArrowDown,
            contentDescription = "",
            tint = if (isEnabled) colors.textPrimary else colors.textPrimary.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun RegionSelectionView(
    practiceRegions: List<VirtualPracticeRegion>,
    selectedRegion: VirtualPracticeRegion?,
    onSelect: (VirtualPracticeRegion) -> Unit,
    onBackPressed: () -> Unit
) {
    FullScreen {
        BackHandler {
            onBackPressed()
        }
        Column(Modifier.fillMaxSize()) {
            Actionbar(
                title = "Requesting a Virtual Visit",
                icon = Icons.Default.Close,
                actionIconClick = onBackPressed
            )
            Column(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(
                        vertical = Dimens.Spacing.medium,
                        horizontal = Dimens.Spacing.medium
                    )
            ) {
                Text(
                    text = "Please choose the state where you're currently located",
                    style = MaterialTheme.typography.titleMedium
                )
                HorizontalDivider(Modifier.padding(vertical = Dimens.Spacing.small))
                practiceRegions.forEach { region ->
                    val isEnabled = !region.busy
                    val colors = LocalAppColor.current
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .applyWhen(isEnabled) {
                                clickable {
                                    onSelect(region)
                                }
                            }
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = region.displayName,
                                color = if (isEnabled) colors.primaryDark else Color.Gray,
                                style = MaterialTheme.typography.titleSmall
                            )

                            Text(
                                text = "$ ${region.visitPrice}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isEnabled) colors.primaryDark else Color.Gray,
                            )

                            if (region.busy) {
                                Text(
                                    text = region.busyMessage,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray,
                                )
                            }
                        }
                        if (selectedRegion?.practiceRegionId == region.practiceRegionId) {
                            Image(
                                painter = rememberVectorPainter(image = Icons.Filled.Check),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(
                                    color = if (isEnabled) colors.primaryDark else Color.Gray,
                                ),
                            )
                        }
                    }
                    HorizontalDivider(Modifier.padding(vertical = Dimens.Spacing.xSmall))
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewRegionSelectionView() {
    PreviewUi {
        val practiceRegions = listOf(
            VirtualPracticeRegion(
                practiceRegionId = "Montana",
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
                practiceRegionId = "Wa",
                displayName = "Washington",
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
                practiceRegionId = "Virginia",
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
        RegionSelectionView(practiceRegions, practiceRegions[1], onSelect = {}, onBackPressed = {})
    }
}

@Preview
@Composable
private fun PreviewPracticeRegionContent() {
    PreviewUi {
        val uiState = PracticeRegionViewModel.UiState()
        PracticeRegionContent(uiState = uiState, {}, {})
    }
}
