package com.dexcare.sample.data

import org.dexcare.DexCareSDK
import org.dexcare.dal.DataObserver
import org.dexcare.services.patient.models.DexCarePatient
import org.dexcare.services.patient.models.PatientDemographics
import org.dexcare.services.virtualvisit.models.CatchmentArea
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PatientRepository @Inject constructor() {

    fun findPatient(onSuccess: (DexCarePatient) -> Unit = {}, onError: () -> Unit = {}) {
        DexCareSDK.patientService.getPatient().subscribe(
            onSuccess = {
                Timber.d("Patient found")
                onSuccess(it)
            },
            onError = {
                Timber.d("Error getting Patient")
                onError()
            }
        )
    }

    fun findCatchmentArea(
        visitState: String,
        residenceState: String,
        residenceZipCode: String,
        brandName: String,
    ): DataObserver<CatchmentArea> {
        return DexCareSDK.patientService.getCatchmentArea(
            visitState,
            residenceState,
            residenceZipCode,
            brandName
        )
    }

    fun findOrCreateDependentPatient(
        ehrSystem: String,
        demographics: PatientDemographics
    ): DataObserver<DexCarePatient> {
        return DexCareSDK.patientService.findOrCreateDependentPatient(ehrSystem, demographics)
    }

    fun findOrCreateAppUserPatient(
        ehrSystem: String,
        appUserDemographics: PatientDemographics
    ): DataObserver<DexCarePatient> {
        return DexCareSDK.patientService
            .findOrCreatePatient(ehrSystem, appUserDemographics)
    }
}
