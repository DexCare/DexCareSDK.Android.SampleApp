package com.dexcare.sample.presentation.main

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.fragment.app.FragmentActivity
import com.dexcare.sample.auth.AuthProvider
import com.dexcare.sample.data.messaging.NotificationManager
import com.dexcare.sample.data.messaging.NotificationMessage
import com.dexcare.sample.data.model.AppEnvironment
import com.dexcare.sample.ui.components.ErrorScreen
import com.dexcare.sample.ui.components.FullScreen
import com.dexcare.sample.ui.components.ListItem
import com.dexcare.sample.ui.theme.DexCareSampleTheme
import com.dexcare.sample.ui.theme.Dimens
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var authProvider: AuthProvider

    @Inject
    lateinit var notificationManager: NotificationManager

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setAuthProvider(authProvider)
        setContent {
            DexCareSampleTheme {
                CompositionLocalProvider(LocalActivity provides this) {
                    MainContent(viewModel)
                }
            }
        }

        if (!notificationManager.hasNotificationPermission() &&
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
        ) {
            notificationManager.requestNotificationPermission(this) {
                Timber.d("Notification permission granted: $it")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkAuthAndNavigate()
    }

    companion object {
        private const val BUNDLE_NOTIFICATION = "MainActivity_NotificationBundle"
        fun createNotificationIntent(
            context: Context,
            notificationMessage: NotificationMessage
        ): Intent {
            val resultIntent = Intent(context, MainActivity::class.java)
            resultIntent.putExtra(BUNDLE_NOTIFICATION, notificationMessage)
            return resultIntent
        }
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
    if (uiState.error != null) {
        ErrorScreen(error = uiState.error)
    } else if (uiState.selectedEnvironment == null) {
        EnvironmentList(environments = uiState.allEnvironments) {
            viewModel.selectEnvironment(it)
        }
    } else if (uiState.loginComplete) {
        MainNavigation(onReloadApp = {
            viewModel.reload()
        }
        )
    }
}

@Composable
fun EnvironmentList(
    environments: List<AppEnvironment>,
    onSelect: (AppEnvironment) -> Unit
) {
    FullScreen {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(
                    top = Dimens.Spacing.x2Large,
                    start = Dimens.Spacing.medium,
                    end = Dimens.Spacing.medium
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Dimens.Spacing.large),
                text = "Select an environment to continue",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )

            Column(verticalArrangement = Arrangement.spacedBy(Dimens.Spacing.large)) {
                environments.forEach {
                    ListItem(
                        title = it.configName,
                        description = it.configDescription,
                        onClick = {
                            onSelect(it)
                        }
                    )
                }
            }
        }
    }
}
