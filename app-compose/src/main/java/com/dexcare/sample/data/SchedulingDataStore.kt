package com.dexcare.sample.data

import android.content.Context
import org.dexcare.sampleapp.android.R
import org.dexcare.services.models.PatientDeclaration
import org.dexcare.services.models.RelationshipToPatient
import org.dexcare.services.patient.models.DexCarePatient
import org.dexcare.services.patient.models.PatientDemographics
import org.dexcare.services.virtualvisit.models.DefaultVirtualVisitTypes
import org.dexcare.services.virtualvisit.models.DefaultVisitStatus
import org.dexcare.services.virtualvisit.models.VirtualPracticeRegion
import org.dexcare.services.virtualvisit.models.VirtualVisitDetails
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SchedulingDataStore @Inject constructor() {

    var scheduleRequest = ScheduleRequest()

    fun setVisitType(visitType: VisitType) {
        scheduleRequest = scheduleRequest.copy(visitType = visitType)
        Timber.d("Updated visit type : $scheduleRequest")
    }

    fun setReasonForVisit(reason: String) {
        scheduleRequest = scheduleRequest.copy(reasonForVisit = reason)
        Timber.d("Updated reason for visit : $scheduleRequest")
    }

    fun setPatient(patient: DexCarePatient) {
        scheduleRequest = scheduleRequest.copy(patient = patient)
        Timber.d("Updated patient for visit : $patient")
    }

    fun setAppUserDemographics(appUserDemographics: PatientDemographics) {
        scheduleRequest = scheduleRequest.copy(appUserDemographics = appUserDemographics)
    }

    fun setVirtualPracticeRegion(virtualPracticeRegion: VirtualPracticeRegion) {
        scheduleRequest = scheduleRequest.copy(virtualPracticeRegion = virtualPracticeRegion)
    }

    fun setPatientDeclaration(declaration: PatientDeclaration) {
        scheduleRequest = scheduleRequest.copy(patientDeclaration = declaration)
    }

    fun reset() {
        scheduleRequest = ScheduleRequest()
    }

    fun createVirtualVisitDetails(context: Context): VirtualVisitDetails {
        val stateLicensure =
            scheduleRequest.appUserDemographics?.addresses?.firstOrNull()?.state.orEmpty()
        return VirtualVisitDetails(
            acceptedTerms = true, // patient has accepted terms of service
            assignmentQualifiers = null, // qualifications to assign visit to a provider - DefaultVirtualVisitAssignmentQualifiers.Adult.qualifier
            patientDeclaration = scheduleRequest.patientDeclaration, // is this visit being submitted by the patient or by a proxy
            stateLicensure = stateLicensure, // state licensure required for provider to see patient
            visitReason = scheduleRequest.reasonForVisit.orEmpty(),
            visitTypeName = DefaultVirtualVisitTypes.Virtual.type,
            practiceId = context.getString(R.string.virtual_practice_id),
            assessmentToolUsed = "ada", // if patient has done preassessment, which tool was used
            brand = context.getString(R.string.brand),
            interpreterLanguage = Locale.getDefault()
                .toLanguageTag(), // optional language requested if interpreter services are available; ISO 639-3 Individual Language codes
            userEmail = scheduleRequest.appUserDemographics!!.email,
            contactPhoneNumber = scheduleRequest.appUserDemographics!!.homePhone!!,
            preTriageTags = listOf("preTriageTag"), // list of scheduledDepartments
            urgency = 0, // 0 for default urgency
            initialStatus = DefaultVisitStatus.Requested.status,// requested, waitoffline,
            actorRelationshipToPatient = RelationshipToPatient.Brother, // Refer to RelationshipToPatient for the full list
        )
    }
}

data class ScheduleRequest(
    val visitType: VisitType? = null,
    val reasonForVisit: String? = null,
    val virtualPracticeRegion: VirtualPracticeRegion? = null,
    val appUserDemographics: PatientDemographics? = null,
    val patient: DexCarePatient? = null,
    val patientDeclaration: PatientDeclaration = PatientDeclaration.Self,
)


enum class VisitType {
    Retail, Virtual, Provider
}
