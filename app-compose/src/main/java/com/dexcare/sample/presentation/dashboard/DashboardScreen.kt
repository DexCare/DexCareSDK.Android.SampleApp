package com.dexcare.sample.presentation.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dexcare.sample.data.VisitType
import com.dexcare.sample.presentation.main.LocalActivity
import com.dexcare.sample.ui.components.ActionBarScreen
import com.dexcare.sample.ui.components.SelectionOption
import com.dexcare.sample.ui.theme.Dimens
import com.dexcare.sample.ui.theme.PreviewUi

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    navLaunchRetail: () -> Unit,
    navLaunchVirtual: () -> Unit,
    navLaunchProvider: () -> Unit,
    onLogOut: () -> Unit,
) {
    val activity = LocalActivity.current
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
            viewModel.onRejoinVisit(activity, "")
        },
        onLogOut = {
            viewModel.logOut()
            onLogOut()
        }
    )
}


@Composable
fun DashboardContent(
    navLaunchRetail: () -> Unit,
    navLaunchVirtual: () -> Unit,
    navLaunchProvider: () -> Unit,
    onRejoinVirtualVisit: () -> Unit,
    onLogOut: () -> Unit,
) {
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

            SelectionOption(
                Modifier.padding(top = Dimens.Spacing.large),
                title = "Log out",
                description = ""
            ) {
                onLogOut()
            }

        }
    }
}

@Preview
@Composable
private fun PreviewDashboard() {
    PreviewUi {
        DashboardContent({}, {}, {}, {}, {})
    }
}
