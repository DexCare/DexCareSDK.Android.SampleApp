package com.dexcare.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.dexcare.sample.dashboard.DashboardScreen
import com.dexcare.sample.demographics.DemographicsScreen
import com.dexcare.sample.login.LoginScreen
import com.dexcare.sample.payment.PaymentScreen
import com.dexcare.sample.provider.ProviderScreen
import com.dexcare.sample.reasonforvisit.ReasonForVisitScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainNavigation()
        }
    }
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navToDashBoard = {
                navController.navigate("dashboard") {
                    popUpTo("login") {
                        inclusive = true
                    }
                }
            })
        }

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


