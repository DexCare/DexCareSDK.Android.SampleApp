package com.dexcare.sample.presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.fragment.app.FragmentActivity
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
import com.dexcare.sample.presentation.dashboard.DashboardViewModel
import com.dexcare.sample.presentation.demographics.DemographicsScreen
import com.dexcare.sample.presentation.payment.PaymentScreen
import com.dexcare.sample.presentation.practiceregion.PracticeRegionScreen
import com.dexcare.sample.presentation.practiceregion.PracticeRegionViewModel
import com.dexcare.sample.presentation.provider.ProviderScreen
import com.dexcare.sample.presentation.provider.ProviderViewModel
import com.dexcare.sample.presentation.provider.timeslot.ProviderTimeSlotScreen
import com.dexcare.sample.presentation.provider.timeslot.ProviderTimeSlotViewModel
import com.dexcare.sample.presentation.reasonforvisit.ReasonForVisitScreen
import com.dexcare.sample.presentation.reasonforvisit.ReasonForVisitViewModel
import com.dexcare.sample.presentation.retailclinic.RetailClinicScreen
import com.dexcare.sample.presentation.retailclinic.timeslot.RetailTimeSlotScreen
import com.dexcare.sample.ui.components.FullScreen
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

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
            setContent {
                CompositionLocalProvider(LocalActivity provides this) {
                    MainContent(viewModel)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume(authProvider)
    }
}

val LocalActivity = staticCompositionLocalOf<FragmentActivity> {
    noLocalProvidedFor()
}

private fun noLocalProvidedFor(): Nothing {
    error("noLocalProvidedFor LocalActivity")
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
            val viewModel = hiltViewModel<DashboardViewModel>()
            DashboardScreen(
                viewModel,
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
            PaymentScreen(
                hiltViewModel(),
                onBackPressed = { navController.popBackStack() },
                onExit = {
                    navController.popBackStack("providerFlow/provider", true)
                }
            )
        }
    }
}

fun NavGraphBuilder.virtualNavigation(navController: NavController) {
    navigation(startDestination = "virtualFlow/practiceRegions", route = "virtualFlow") {

        composable("virtualFlow/practiceRegions") {
            val viewModel = hiltViewModel<PracticeRegionViewModel>()
            PracticeRegionScreen(
                viewModel = viewModel,
                onBackPressed = {
                    navController.popBackStack()
                }, onContinue = {
                    navController.navigate("virtualFlow/reason")
                }
            )
        }

        composable("virtualFlow/reason") {
            val viewModel = hiltViewModel<ReasonForVisitViewModel>()
            ReasonForVisitScreen(
                viewModel = viewModel,
                onBackPressed = {
                    navController.popBackStack()
                },
                onContinue = {
                    navController.navigate("virtualFlow/demographics")
                    Timber.d("virtualNavigation Navigate to demographics from reason for visit")
                }
            )
        }

        composable("virtualFlow/demographics") {
            DemographicsScreen(
                hiltViewModel(),
                navContinue = {
                    navController.navigate("virtualFlow/payments")
                }
            )
        }

        composable("virtualFlow/payments") {
            PaymentScreen(
                hiltViewModel(),
                onBackPressed = { navController.popBackStack() },
                onExit = {

                }
            )
        }
    }
}

fun NavGraphBuilder.retailNavigation(navController: NavController) {
    navigation(startDestination = "retailFlow/clinics", route = "retailFlow") {
        composable("retailFlow/clinics") {
            RetailClinicScreen(
                hiltViewModel(),
                onContinue = {
                    navController.navigate("retailFlow/timeSlot")
                },
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }
        composable("retailFlow/timeSlot") {
            RetailTimeSlotScreen(
                viewModel = hiltViewModel(),
                onBackPressed = { navController.popBackStack() },
                onContinue = {
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
                    navController.navigate("retailFlow/payments")
                }
            )
        }

        composable("retailFlow/demographics") {
            DemographicsScreen(
                hiltViewModel(),
                navContinue = {
                    navController.navigate("retailFlow/reason")
                }
            )
        }

        composable("retailFlow/payments") {
            PaymentScreen(hiltViewModel(),
                onBackPressed = { navController.popBackStack() },
                onExit = {

                }
            )
        }
    }
}


