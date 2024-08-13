package com.dexcare.sample.data.repository

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.dexcare.sample.data.virtualvisit.VirtualVisitStorage
import org.dexcare.DexCareSDK
import org.dexcare.services.models.PaymentMethod
import org.dexcare.services.patient.models.DexCarePatient
import org.dexcare.services.patient.models.Patient
import org.dexcare.services.virtualvisit.models.RegisterPushNotification
import org.dexcare.services.virtualvisit.models.VirtualPractice
import org.dexcare.services.virtualvisit.models.VirtualPracticeRegion
import org.dexcare.services.virtualvisit.models.VirtualVisitDetails
import org.dexcare.services.virtualvisit.models.isVisitStatusActive
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class VirtualVisitRepository @Inject constructor(private val storage: VirtualVisitStorage) {

    fun scheduleVisit(
        activity: FragmentActivity,
        patient: Patient,
        virtualVisitDetails: VirtualVisitDetails,
        paymentMethod: PaymentMethod,
        fcmToken: String?,
        fcmPlatformIdentifier: String?,
        onComplete: (Intent?, Throwable?) -> Unit,
    ) {
        Timber.d("Firebase token=$fcmToken")
        DexCareSDK.virtualService.createVirtualVisitWithPatientActor(
            activity = activity,
            patient = patient,
            virtualVisitDetails = virtualVisitDetails,
            paymentMethod = paymentMethod,
            virtualActor = null,
            registerPushNotification = if (fcmToken != null) {
                RegisterPushNotification(
                    appId = fcmPlatformIdentifier.orEmpty(),
                    fcmToken = fcmToken,
                )
            } else {
                null
            }
        ).subscribe(
            onSuccess = {
                val visitType = it.first
                val visitId = it.second
                val intent = it.third
                Timber.d("visit created; type=$visitType and visitId=$visitId")
                storage.saveVisit(visitId)
                onComplete(intent, null)
            },
            onError = {
                Timber.e(it)
                onComplete(null, it)
            }
        )
    }

    fun rejoinVisit(
        activity: FragmentActivity,
        dexCarePatient: DexCarePatient,
        onComplete: (Intent?, Throwable?) -> Unit,
    ) {
        val visitId = storage.getVisitId()
        if (visitId.isNullOrEmpty()) {
            onComplete(
                null,
                Throwable("VisitId is not available. We couldn't find information about your last visit. Please create a new visit.")
            )
            return
        }

        DexCareSDK.virtualService.resumeVirtualVisit(visitId, activity, null, dexCarePatient)
            .subscribe(
                onSuccess = {
                    Timber.d("visit rejoined")
                    onComplete(it, null)
                }, onError = {
                    Timber.e(it)
                    onComplete(null, it)
                }
            )
    }

    fun getPracticeRegion(
        practiceId: String,
        onSuccess: (VirtualPractice) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        DexCareSDK.practiceService
            .getVirtualPractice(practiceId)
            .subscribe(onSuccess, onError)
    }

    fun savePracticeRegion(virtualPracticeRegion: VirtualPracticeRegion) {
        storage.saveRegion(virtualPracticeRegion)
    }

    fun findPreviousRegionId(): String {
        return storage.getRegionId()
    }

    fun clear() {
        storage.clearData()
    }

    suspend fun isVisitActive(visitId: String): Boolean {
        return suspendCoroutine { coroutine ->
            DexCareSDK.virtualService.getVirtualVisitStatus(visitId).subscribe(onSuccess = {
                coroutine.resume(isVisitStatusActive(it))
            }, onError = {
                coroutine.resume(false)
            })
        }
    }

    fun updateVisitId(visitId: String) {
        storage.saveVisit(visitId)
    }
}
