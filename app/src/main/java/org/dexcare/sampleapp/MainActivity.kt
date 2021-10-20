package org.dexcare.sampleapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.findNavController
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.lock.*
import com.auth0.android.result.Credentials
import org.dexcare.DexCareSDK
import org.dexcare.Environment
import org.dexcare.sampleapp.ext.showMaterialDialog
import org.dexcare.sampleapp.services.AuthService
import org.dexcare.sampleapp.services.DemographicsService
import org.dexcare.sampleapp.ui.common.SchedulingInfo
import org.dexcare.services.virtualvisit.VirtualEventListener
import org.dexcare.services.virtualvisit.VirtualService
import org.dexcare.services.virtualvisit.errors.DevicePairFailedReason
import org.dexcare.services.virtualvisit.errors.VirtualVisitError
import org.koin.android.ext.android.inject
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    companion object {
        const val ACTION_VOLUME_CHANGE = "org.dexcare.sampleapp.changevolume"
    }

    private val authService: AuthService by inject()
    private val demographicsService: DemographicsService by inject()
    private val schedulingInfo: SchedulingInfo by inject()
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_sample)
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

                result.resultCode

                // The result codes returned by the SDK are found on VirtualService:
                VirtualService.VISIT_SUCCESS_RESULT_CODE
                VirtualService.VISIT_CANCELLED_RESULT_CODE
                VirtualService.VISIT_DECLINED_BY_PROVIDER_RESULT_CODE
                VirtualService.VISIT_DISCONNECTED_RESULT_CODE
                VirtualService.VISIT_USER_JOINED_ELSEWHERE_RESULT_CODE

                // For this sample app, we can just return to the dashboard in all cases.
                navigateToDashboardFragment()
                schedulingInfo.clear()
            }
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
        )

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
            val accessToken: String = credentials.accessToken
            initializeDexCareSDK()
            DexCareSDK.signIn(accessToken)
            authService.saveToken(accessToken)

            getPatient()
        }

        override fun onError(error: AuthenticationException) {
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
                showMaterialDialog(message = getString(R.string.unexpected_error_message))
            })
    }

    fun initializeDexCareSDK() {
        DexCareSDK.init(this,
            object : Environment {
                override val isProd: Boolean = true
                override val fhirOrchUrl: String = getString(R.string.dexcare_fhirorch_url)
                override val virtualVisitUrl: String = getString(R.string.dexcare_virtualvisit_url)
            }
        )

        // The listener should primarily be used for logging purposes.
        // The SDK is still foregrounded while any of these events are invoked.
        // These events do not mean the SDK cannot continue.
        DexCareSDK.virtualService.virtualEventListener = object : VirtualEventListener {
            override fun onDevicePairInitiated() {
                // The user started the device pairing flow.
                // Currently this is specific to TytoCare, and is invoked when the QR code is displayed.
            }

            override fun onVirtualVisitCancelledByUser() {
                // The user cancelled the virtual visit while in the waiting room.
                // The virtual visit activity will soon finish with result code VirtualService.VISIT_CANCELLED_RESULT_CODE.
            }

            override fun onVirtualVisitCompleted() {
                // The virtual visit was completed successfully.
                // The virtual visit activity will soon finish with result code VirtualService.VISIT_SUCCESS_RESULT_CODE.
            }

            override fun onVirtualVisitDeclinedByProvider() {
                // The virtual visit was declined by the Provider.
                // Users are not charged for incomplete visits.
                // The virtual visit activity will soon finish with result code VirtualService.VISIT_DECLINED_BY_PROVIDER_RESULT_CODE.
            }

            override fun onVirtualVisitDisconnected() {
                // The video conference was disconnected and the SDK's reconnection attempts failed.
                // The virtual visit activity will soon finish with result code VirtualService.VISIT_DISCONNECTED_RESULT_CODE.
            }

            override fun onVirtualVisitError(error: VirtualVisitError) {
                when (error) {
                    is VirtualVisitError.DevicePairError -> {
                        when (error.devicePairFailedReason) {
                            DevicePairFailedReason.ConnectionIsNot24GHz -> {
                                // Specific to TytoCare
                                // User granted permissions to their Wi-Fi network,
                                // but their current connection is not 2.4GHz.
                            }
                            DevicePairFailedReason.UnderAge -> {
                                // Specific to TytoCare
                                // The signed in user is not above 18, so a TytoCare account
                                // could not be created for them.
                            }
                            DevicePairFailedReason.ServiceFailed -> {
                                // General catch-all error during the device pairing flow
                            }
                        }
                    }
                    is VirtualVisitError.WaitingRoomOpenTokError -> {
                        // The error from the TokBox SDK is passed back
                        error.openTokError
                    }
                    is VirtualVisitError.VideoConferenceOpenTokError -> {
                        // The error from the TokBox SDK is passed back
                        error.openTokError
                    }
                    VirtualVisitError.WaitingRoomCameraPreviewFailed -> {
                        // No additional info is passed back
                    }
                    is VirtualVisitError.WaitingRoomYouTubePlayerError -> {
                        // The error from the YouTubePlayer API is passed back
                        error.youTubeError
                    }
                    is VirtualVisitError.WaitingRoomYouTubeInitializationError -> {
                        // The initialization result from the YouTubePlayer API is passed back
                        error.youTubeError
                    }
                    VirtualVisitError.PatientJoinedElsewhere -> {
                        // No additional info is passed back
                    }
                }
            }

            override fun onVirtualVisitReconnected() {
                // The video conference was disconnected, but the SDK was able to reconnect.
            }

            override fun onVirtualVisitReconnecting() {
                // The video conference was disconnected, and the SDK is attempting to reconnect.
            }

            override fun onVirtualVisitStarted() {
                // The provider started the video conference
            }

            override fun onWaitingRoomDisconnected() {
                // The waiting room was disconnected and the SDK's reconnection attempts failed.
                // The virtual visit activity will soon finish with result code VirtualService.VISIT_DISCONNECTED_RESULT_CODE.
            }

            override fun onWaitingRoomLaunched() {
                // The waiting room is now visible to the user.
            }

            override fun onWaitingRoomReconnected() {
                // The waiting room was disconnected, but the SDK was able to reconnect.
            }

            override fun onWaitingRoomReconnecting() {
                // The waiting room was disconnected, and the SDK is attempting to reconnect.
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            LocalBroadcastManager.getInstance(this).sendBroadcast(Intent(ACTION_VOLUME_CHANGE))
        }
        return super.onKeyDown(keyCode, event)
    }
}
