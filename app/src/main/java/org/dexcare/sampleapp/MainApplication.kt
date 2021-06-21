package org.dexcare.sampleapp

import android.app.Application
import com.stripe.android.PaymentConfiguration
import org.dexcare.DexCareSDK
import org.dexcare.Environment
import org.dexcare.sampleapp.modules.serviceModule
import org.dexcare.sampleapp.modules.storageModule
import org.dexcare.sampleapp.modules.utilModule
import org.dexcare.sampleapp.modules.viewModelModule
import org.dexcare.services.virtualvisit.VirtualEventListener
import org.dexcare.services.virtualvisit.VirtualService
import org.dexcare.services.virtualvisit.errors.DevicePairFailedReason
import org.dexcare.services.virtualvisit.errors.VirtualVisitError
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        startKoin {
            androidContext(this@MainApplication)
            modules(
                listOf(
                    serviceModule,
                    storageModule,
                    utilModule,
                    viewModelModule
                )
            )
        }

        DexCareSDK.init(this,
            object : Environment {
                override val isProd: Boolean = false
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

        PaymentConfiguration.init(applicationContext, getString(R.string.stripe_publishable_key))
    }
}
