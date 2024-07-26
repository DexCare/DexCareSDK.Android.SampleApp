package com.dexcare.sample.presentation.main

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
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
import timber.log.Timber

@Composable
fun MainNavigation(onReloadApp: () -> Unit) {
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
                },
                onLogOut = onReloadApp
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
                },
                onBackPressed = {
                    navController.popBackStack()
                },
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
                },
                onBackPressed = {
                    navController.popBackStack()
                },
            )
        }

        composable("virtualFlow/payments") {
            PaymentScreen(
                hiltViewModel(),
                onBackPressed = { navController.popBackStack() },
                onExit = {
                    navController.popBackStack("virtualFlow/practiceRegions", inclusive = true)
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
                    navController.navigate("retailFlow/demographics")
                }
            )
        }

        composable("retailFlow/demographics") {
            DemographicsScreen(
                hiltViewModel(),
                navContinue = {
                    navController.navigate("retailFlow/reason")
                },
                onBackPressed = {
                    navController.popBackStack()
                },
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

        composable("retailFlow/payments") {
            PaymentScreen(
                hiltViewModel(),
                onBackPressed = { navController.popBackStack() },
                onExit = {
                    navController.popBackStack("retailFlow/clinics", inclusive = true)
                }
            )
        }
    }
}
