package com.dexcare.sample.presentation.dashboard

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.dexcare.sample.common.getPackageInfo
import com.dexcare.sample.data.VisitType
import com.dexcare.sample.data.virtualvisit.VirtualVisitContract
import com.dexcare.sample.presentation.LocalActivity
import com.dexcare.sample.ui.components.AcmeCircularProgress
import com.dexcare.sample.ui.components.ActionBarScreen
import com.dexcare.sample.ui.components.SelectionOption
import com.dexcare.sample.ui.components.SimpleAlert
import com.dexcare.sample.ui.theme.Dimens
import com.dexcare.sample.ui.theme.PreviewUi
import timber.log.Timber

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    navLaunchRetail: () -> Unit,
    navLaunchVirtual: () -> Unit,
    navLaunchProvider: () -> Unit,
) {
    val activity = LocalActivity.current
    val uiState = viewModel.uiState.collectAsState().value
    val showErrorAlert = remember { mutableStateOf(false) }

    val visitLauncher =
        rememberLauncherForActivityResult(VirtualVisitContract.LaunchVisit()) { resultCode ->
            Timber.d("visit ended with result code $resultCode")
        }

    if (uiState.visitIntent != null) {
        LaunchedEffect(key1 = uiState.visitIntent) {
            visitLauncher.launch(uiState.visitIntent)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        DashboardContent(
            navLaunchRetail = {
                viewModel.onVisitType(VisitType.Retail)
                navLaunchRetail()
            },
            navLaunchVirtual = {
                viewModel.onVisitType(VisitType.Virtual)
                navLaunchVirtual()
            },
            navLaunchProvider = {
                viewModel.onVisitType(VisitType.Provider)
                navLaunchProvider()
            },
            onRejoinVirtualVisit = {
                viewModel.onRejoinVisit(activity)
            }
        )

        if (uiState.isLoading) {
            AcmeCircularProgress(Modifier.align(Alignment.Center))
        }

        if (uiState.error != null) {
            showErrorAlert.value = true
            SimpleAlert(
                title = uiState.error.title,
                message = uiState.error.message,
                buttonText = "Got it",
                enabledState = showErrorAlert,
                actionAlertClosed = {
                    viewModel.clearError()
                }
            )
        }

    }
}


@Composable
fun DashboardContent(
    navLaunchRetail: () -> Unit,
    navLaunchVirtual: () -> Unit,
    navLaunchProvider: () -> Unit,
    onRejoinVirtualVisit: () -> Unit,
) {
    val appInfo = LocalContext.current.getPackageInfo()

    ActionBarScreen(title = "") {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(Dimens.Spacing.large)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Acme Health is here to help you. Please select one of the services to begin.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = Dimens.Spacing.large)
            )

            SelectionOption(
                title = "Virtual Visit",
                description = "Schedule an online visit with one of our providers. We are here to help 24/7."
            ) {
                navLaunchVirtual()
            }

            SelectionOption(
                Modifier.padding(top = Dimens.Spacing.large),
                title = "Provider Booking",
                description = "Make an appointment with your provider. Select the date and time that works for you."
            ) {
                navLaunchProvider()
            }

            SelectionOption(
                Modifier.padding(top = Dimens.Spacing.large),
                title = "Retail Visit",
                description = "Schedule an appointment at one of the physical locations. Select the date and time that works for you."
            ) {
                navLaunchRetail()
            }

            SelectionOption(
                Modifier.padding(top = Dimens.Spacing.large),
                title = "Rejoin the virtual Visit",
                description = "Join back to the virtual visit you have scheduled. You can't rejoin a visit if it has already ended or has been canceled."
            ) {
                onRejoinVirtualVisit()
            }


            SelectionContainer {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Dimens.Spacing.x2Large)
                ) {
                    Text(text = "App Info:", style = MaterialTheme.typography.titleSmall)
                    Text(
                        text = "Version name:${appInfo.versionName}, Version code:${appInfo.versionCode}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = Dimens.Spacing.xSmall)
                    )
                }
            }

        }
    }
}

@Preview
@Composable
private fun PreviewDashboard() {
    PreviewUi {
        DashboardContent({}, {}, {}, {})
    }
}

