package com.dexcare.sample.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.dexcare.sample.areConfigValuesSetUp
import com.dexcare.sample.auth.AuthProvider
import com.dexcare.sample.presentation.dashboard.DashboardScreen
import com.dexcare.sample.presentation.demographics.DemographicsScreen
import com.dexcare.sample.presentation.payment.PaymentScreen
import com.dexcare.sample.presentation.provider.ProviderScreen
import com.dexcare.sample.presentation.provider.ProviderViewModel
import com.dexcare.sample.presentation.provider.timeslot.ProviderTimeSlotScreen
import com.dexcare.sample.presentation.provider.timeslot.ProviderTimeSlotViewModel
import com.dexcare.sample.presentation.reasonforvisit.ReasonForVisitScreen
import com.dexcare.sample.presentation.reasonforvisit.ReasonForVisitViewModel
import com.dexcare.sample.ui.components.FullScreen
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authProvider: AuthProvider

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!areConfigValuesSetUp(this)) {
            setContent {
                LoginError(message = "Required values in config.xml are missing. Make sure the values are properly set, otherwise the app will not work.")
            }
        } else {
            setContent { MainContent(viewModel) }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume(authProvider)
    }
}

@Composable
fun MainContent(viewModel: MainViewModel) {
    val uiState = viewModel.uiState.collectAsState().value
    if (uiState.loginComplete) {
        MainNavigation()
    } else if (uiState.errorMessage != null) {
        LoginError(message = uiState.errorMessage)
    }
}

@Composable
fun LoginError(message: String) {
    FullScreen {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = message,
            style = MaterialTheme.typography.displayMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "dashboard") {
        composable("dashboard") {
            DashboardScreen(
                navLaunchRetail = {
                    navController.navigate("retailFlow")
                },
                navLaunchVirtual = {
                    navController.navigate("virtualFlow")
                },
                navLaunchProvider = {
                    navController.navigate("providerFlow")
                }
            )
        }

        virtualNavigation(navController = navController)
        retailNavigation(navController = navController)
        providerNavigation(navController = navController)

    }
}

fun NavGraphBuilder.providerNavigation(navController: NavController) {
    navigation(startDestination = "providerFlow/provider", route = "providerFlow") {
        composable("providerFlow/provider") {
            val providerViewModel: ProviderViewModel = hiltViewModel()
            ProviderScreen(
                providerViewModel,
                onBackPressed = {
                    navController.popBackStack()
                }, navContinue = {
                    navController.navigate("providerFlow/providerSlot")
                }
            )
        }


        composable("providerFlow/providerSlot") {
            val timeSlotViewModel: ProviderTimeSlotViewModel = hiltViewModel()
            ProviderTimeSlotScreen(
                viewModel = timeSlotViewModel,
                onBackPressed = { navController.popBackStack() },
                onContinue = {
                    navController.navigate("providerFlow/reason")
                }
            )
        }

        composable("providerFlow/reason") {
            val viewModel = hiltViewModel<ReasonForVisitViewModel>()
            ReasonForVisitScreen(
                viewModel = viewModel,
                onBackPressed = {
                    navController.popBackStack()
                },
                onContinue = {
                    Timber.d("providerNavigation Navigate to demographics from reason for visit")
                    navController.navigate("providerFlow/demographics")
                }
            )
        }

        composable("providerFlow/demographics") {
            DemographicsScreen(
                hiltViewModel(),
                navContinue = {
                    navController.navigate("providerFlow/payment")
                }
            )
        }

        composable("providerFlow/payment") {
            PaymentScreen()
        }
    }
}

fun NavGraphBuilder.virtualNavigation(navController: NavController) {
    navigation(startDestination = "virtualFlow/reason", route = "virtualFlow") {
        composable("virtualFlow/reason") {
            val viewModel = hiltViewModel<ReasonForVisitViewModel>()
            ReasonForVisitScreen(
                viewModel = viewModel,
                onBackPressed = {
                    navController.popBackStack()
                },
                onContinue = {
                    Timber.d("virtualNavigation Navigate to demographics from reason for visit")
                }
            )
        }
    }
}

fun NavGraphBuilder.retailNavigation(navController: NavController) {
    navigation(startDestination = "retailFlow/demographics", route = "retailFlow") {
        composable("retailFlow/demographics") {
            DemographicsScreen(
                hiltViewModel(),
                navContinue = {
                    navController.navigate("retailFlow/reason")
                }
            )
        }
        composable("retailFlow/reason") {
            val viewModel = hiltViewModel<ReasonForVisitViewModel>()
            ReasonForVisitScreen(
                viewModel = viewModel,
                onBackPressed = {
                    navController.popBackStack()
                },
                onContinue = {
                    Timber.d("retailNavigation Navigate to demographics from reason for visit")
                }
            )
        }
    }
}


