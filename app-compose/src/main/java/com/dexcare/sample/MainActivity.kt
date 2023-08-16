package com.dexcare.sample

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
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.dexcare.sample.auth.AuthProvider
import com.dexcare.sample.dashboard.DashboardScreen
import com.dexcare.sample.demographics.DemographicsScreen
import com.dexcare.sample.payment.PaymentScreen
import com.dexcare.sample.provider.ProviderScreen
import com.dexcare.sample.reasonforvisit.ReasonForVisitScreen
import com.dexcare.sample.ui.components.FullScreen
import dagger.hilt.android.AndroidEntryPoint
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
    navigation(startDestination = "provider", route = "providerFlow") {
        composable("provider") {
            ProviderScreen(
                onBackPressed = {
                    navController.popBackStack()
                }, navContinue = {
                    navController.navigate("demographics")
                }
            )
        }

        composable("demographics") {
            DemographicsScreen(
                navContinue = {
                    navController.navigate("reason")
                }
            )
        }
        composable("reason") {
            ReasonForVisitScreen(
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }

        composable("payment") {
            PaymentScreen()
        }
    }
}

fun NavGraphBuilder.virtualNavigation(navController: NavController) {
    navigation(startDestination = "reason", route = "virtualFlow") {
        composable("reason") {
            ReasonForVisitScreen(
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }
    }
}

fun NavGraphBuilder.retailNavigation(navController: NavController) {
    navigation(startDestination = "reason", route = "retailFlow") {
        composable("reason") {
            ReasonForVisitScreen(
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }
    }
}


