package com.dexcare.sample.presentation.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dexcare.sample.data.VisitType
import com.dexcare.sample.presentation.LocalActivity
import com.dexcare.sample.ui.components.ActionBarScreen
import com.dexcare.sample.ui.components.SolidButton
import com.dexcare.sample.ui.theme.Dimens
import com.dexcare.sample.ui.theme.PreviewUi
import com.dexcare.acme.android.R

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    navLaunchRetail: () -> Unit,
    navLaunchVirtual: () -> Unit,
    navLaunchProvider: () -> Unit,
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
        }
    )
}


@Composable
fun DashboardContent(
    navLaunchRetail: () -> Unit,
    navLaunchVirtual: () -> Unit,
    navLaunchProvider: () -> Unit,
    onRejoinVirtualVisit: () -> Unit,
) {
    ActionBarScreen(title = stringResource(id = R.string.app_name)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(Dimens.Spacing.large)
        ) {
            Text(
                text = "Select one of the service options to start",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = Dimens.Spacing.large)
            )

            SolidButton(
                text = "Provider",
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dimens.Spacing.small)
            ) {
                navLaunchProvider()
            }

            SolidButton(
                text = "Retail",
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dimens.Spacing.small)
            ) {
                navLaunchRetail()
            }

            SolidButton(
                text = "Virtual",
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dimens.Spacing.small)
            ) {
                navLaunchVirtual()
            }

            SolidButton(
                text = "Rejoin virtual visit",
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dimens.Spacing.small)
            ) {
                onRejoinVirtualVisit()
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
