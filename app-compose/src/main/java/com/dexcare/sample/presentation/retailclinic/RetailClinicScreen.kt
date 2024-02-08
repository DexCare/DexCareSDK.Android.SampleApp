package com.dexcare.sample.presentation.retailclinic

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.dexcare.sample.ui.components.ActionBarScreen
import com.dexcare.sample.ui.components.InformationScreen
import com.dexcare.sample.ui.theme.Dimens
import com.dexcare.acme.android.R
import org.dexcare.services.retail.models.RetailDepartment

@Composable
fun RetailClinicScreen(
    viewModel: RetailClinicViewModel,
    onContinue: () -> Unit,
    onBackPressed: () -> Unit
) {
    ActionBarScreen(
        title = "Retail Clinic",
        onBackPressed = onBackPressed
    ) {
        val context = LocalContext.current
        viewModel.initialize(context.getString(R.string.brand))
        val uiState = viewModel.uiState.collectAsState().value
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else if (uiState.error != null) {
                InformationScreen(
                    title = uiState.error.title,
                    message = uiState.error.message,
                    showTopBar = false
                ) {
                    onBackPressed()
                }
            } else {
                RetailContent(
                    uiState = uiState,
                    onSelect = {
                        viewModel.onClinicSelected(it)
                        onContinue()
                    }
                )
            }
        }
    }
}


@Composable
fun RetailContent(uiState: RetailClinicViewModel.UiState, onSelect: (RetailDepartment) -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        uiState.clinics.forEach {
            Clinic(clinic = it, onSelect)
        }
    }

}


@Composable
fun Clinic(clinic: RetailDepartment, onSelect: (RetailDepartment) -> Unit) {
    Card(Modifier.padding(Dimens.Spacing.medium)) {
        Column(
            Modifier
                .fillMaxWidth()
                .clickable { onSelect(clinic) }
                .padding(Dimens.Spacing.medium)
        ) {
            val typography = MaterialTheme.typography
            Text(text = clinic.displayName, style = typography.bodyLarge)
            Text(
                text = clinic.address.line1.plus(" ${clinic.address.line2}"),
                style = typography.bodyMedium
            )
            Text(
                text = clinic.address.city.plus(" ")
                    .plus(clinic.address.state).plus(", ")
                    .plus(clinic.address.postalCode),
                style = typography.bodyMedium
            )
            Text(text = clinic.phone, style = typography.labelMedium)
        }
    }
}
