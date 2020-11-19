package org.dexcare.sampleapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.auth0.android.Auth0
import com.auth0.android.lock.*
import com.auth0.android.lock.utils.LockException
import com.auth0.android.result.Credentials
import org.dexcare.DexCareSDK
import org.dexcare.exts.patientService
import org.dexcare.sampleapp.services.AuthService
import org.dexcare.sampleapp.services.DemographicsService
import org.dexcare.sampleapp.ui.virtual.VirtualSchedulingFlow
import org.koin.android.ext.android.inject
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    companion object {
        const val VIRTUAL_REQUEST_CODE = 5231
    }

    private val authService: AuthService by inject()
    private val demographicsService: DemographicsService by inject()
    private val virtualSchedulingFlow: VirtualSchedulingFlow by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_sample)
    }

    override fun onResume() {
        super.onResume()
        if (!authService.hasToken()) {
            launchAuth0LockWidget()
        }
    }

    private fun launchAuth0LockWidget() {
        val auth0 = Auth0(
            getString(R.string.auth0_client_id),
            getString(R.string.auth0_domain)
        ).apply {
            isOIDCConformant = true
            isLoggingEnabled = BuildConfig.DEBUG
        }

        val builder = Lock.newBuilder(auth0, lockWidgetCallback).apply {
            withAudience("https://${getString(R.string.auth0_domain)}/api/v2/")
            withScope("openid offline_access")
            closable(true)
            useLabeledSubmitButton(true)
            loginAfterSignUp(true)
            withUsernameStyle(UsernameStyle.EMAIL)
            allowLogIn(true)
            allowSignUp(true)
            allowForgotPassword(true)
            initialScreen(InitialScreen.LOG_IN)
            allowedConnections(
                listOf(
                    "Username-Password-Authentication",
                    "mfa-connection",
                    "with-strength"
                )
            )
            hideMainScreenTitle(true)
        }

        builder.build(applicationContext)?.let {
            startActivity(it.newIntent(applicationContext))
        }
    }

    private fun navigateToDashboardFragment() {
        findNavController(R.id.navHost).navigate(R.id.dashboardFragment)
    }

    private val lockWidgetCallback: LockCallback = object : AuthenticationCallback() {
        override fun onAuthentication(credentials: Credentials) {
            val accessToken: String? = credentials.accessToken
            val refreshToken: String? = credentials.refreshToken
            if (accessToken != null && refreshToken != null) {
                DexCareSDK.signIn(accessToken)
                authService.saveToken(accessToken)

                getPatient()
            }
        }

        override fun onCanceled() {
            // do nothing
        }

        override fun onError(error: LockException) {
        }
    }

    fun getPatient() {
        DexCareSDK.patientService
            .getPatient()
            .subscribe({
                demographicsService.setDemographics(it)
                navigateToDashboardFragment()
            }, {
                Timber.e(it)
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            VIRTUAL_REQUEST_CODE -> {
                // Currently resultCode will be either Activity.RESULT_OK or RESULT_CANCELED
                // For this sample app, we can just return to the dashboard in both cases.
                navigateToDashboardFragment()
                virtualSchedulingFlow.clear()
            }
        }
    }
}
