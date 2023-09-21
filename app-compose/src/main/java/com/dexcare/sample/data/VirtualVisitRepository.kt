package com.dexcare.sample.data

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import org.dexcare.DexCareSDK
import org.dexcare.services.models.PaymentMethod
import org.dexcare.services.patient.models.Patient
import org.dexcare.services.virtualvisit.models.VirtualPractice
import org.dexcare.services.virtualvisit.models.VirtualVisitDetails
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VirtualVisitRepository @Inject constructor() {

    fun scheduleVisit(
        activity: FragmentActivity,
        patient: Patient,
        virtualVisitDetails: VirtualVisitDetails,
        paymentMethod: PaymentMethod,
        onComplete: (Intent?, Throwable?) -> Unit,
    ) {
        DexCareSDK.virtualService.createVirtualVisitWithPatientActor(
            activity = activity,
            patient = patient,
            virtualVisitDetails = virtualVisitDetails,
            paymentMethod = paymentMethod,
            virtualActor = null,
            registerPushNotification = null
        ).subscribe(
            onSuccess = {
                val visitType = it.first
                val visitId = it.second
                val intent = it.third
                Timber.d("visit created; type=$visitType and visitId=$visitId")
                onComplete(intent, null)
            },
            onError = {
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
}
