package com.dexcare.sample.presentation.provider

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.dexcare.sample.ui.components.ActionBarScreen
import com.dexcare.sample.ui.components.InformationScreen
import com.dexcare.sample.ui.theme.Dimens
import org.dexcare.services.provider.models.ProviderVisitType

@Composable
fun ProviderScreen(
    viewModel: ProviderViewModel,
    onBackPressed: () -> Unit,
    navContinue: () -> Unit
) {
    ActionBarScreen(
        title = "Provider",
        onBackPressed = onBackPressed
    ) {
        val uiState = viewModel.uiState.collectAsState().value
        ProviderContent(
            uiState = uiState,
            onSelectVisitType = {
                viewModel.onVisitTypeSelected(it)
                navContinue()
            },
            onGoBack = onBackPressed
        )
    }
}

@Composable
fun ProviderContent(
    uiState: ProviderViewModel.UiState,
    onSelectVisitType: (ProviderVisitType) -> Unit,
    onGoBack: () -> Unit,
) {
    Column(Modifier.padding(Dimens.Spacing.large)) {
        if (uiState.errorMessage != null) {
            ErrorMessage(message = uiState.errorMessage)
        } else if (!uiState.isProviderActive) {
            InformationScreen(
                title = "Provider is not open for scheduling.",
                message = "Please try again later or select different provider.",
                showTopBar = false,
                onDismiss = onGoBack
            )
        } else {
            uiState.provider?.let { provider ->
                Card {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(Dimens.Spacing.medium)
                    ) {
                        Text(
                            text = "${provider.name} ${provider.credentials.orEmpty()}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        provider.departments.forEach { department ->
                            Column {
                                Text(
                                    text = department.name,
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                            Spacer(modifier = Modifier.height(Dimens.Spacing.medium))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.Spacing.x2Large))

                provider.visitTypes.forEach { visitType ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectVisitType(visitType) }
                            .padding(Dimens.Spacing.small),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(text = visitType.name, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = visitType.description.orEmpty(),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = null
                        )
                    }

                    Spacer(modifier = Modifier.height(Dimens.Spacing.medium))
                }
            }
        }

        if (uiState.inProgress) {
            ProgressMessage()
        }
    }
}

@Composable
fun ErrorMessage(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = message, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun ProgressMessage(message: String? = null) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Text(
                text = message ?: "Loading",
                modifier = Modifier.padding(vertical = Dimens.Spacing.large),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
