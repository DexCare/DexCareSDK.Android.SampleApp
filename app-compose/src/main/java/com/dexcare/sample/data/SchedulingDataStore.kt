package com.dexcare.sample.data

import android.content.Context
import org.dexcare.sampleapp.android.R
import org.dexcare.services.models.PatientDeclaration
import org.dexcare.services.models.RelationshipToPatient
import org.dexcare.services.patient.models.DexCarePatient
import org.dexcare.services.patient.models.PatientDemographics
import org.dexcare.services.provider.models.Provider
import org.dexcare.services.provider.models.ProviderVisitInformation
import org.dexcare.services.retail.models.RetailDepartment
import org.dexcare.services.retail.models.TimeSlot
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

    fun setProvider(provider: Provider) {
        scheduleRequest = scheduleRequest.copy(provider = provider)
    }

    fun setRetailClinic(retailClinic: RetailDepartment) {
        scheduleRequest = scheduleRequest.copy(retailClinic = retailClinic)
    }

    fun setTimeSlot(timeSlot: TimeSlot) {
        scheduleRequest = scheduleRequest.copy(selectedTimeSlot = timeSlot)
    }

    fun reset() {
        scheduleRequest = ScheduleRequest()
    }

    fun createVirtualVisitDetails(context: Context): VirtualVisitDetails {
        val stateLicensure =
            scheduleRequest.appUserDemographics?.addresses?.firstOrNull()?.state.orEmpty()
        return VirtualVisitDetails(
            // patient has accepted terms of service
            acceptedTerms = true,
            // qualifications to assign visit to a provider - DefaultVirtualVisitAssignmentQualifiers.Adult.qualifier
            assignmentQualifiers = null,
            // is this visit being submitted by the patient or by a proxy
            patientDeclaration = scheduleRequest.patientDeclaration,
            // state licensure required for provider to see patient
            stateLicensure = stateLicensure,
            visitReason = scheduleRequest.reasonForVisit.orEmpty(),
            visitTypeName = DefaultVirtualVisitTypes.Virtual.type,
            practiceId = context.getString(R.string.virtual_practice_id),
            // if patient has done preassessment, which tool was used
            assessmentToolUsed = "ada",
            brand = context.getString(R.string.brand),
            // optional language requested if interpreter services are available; ISO 639-3 Individual Language codes
            interpreterLanguage = Locale.getDefault().toLanguageTag(),
            userEmail = scheduleRequest.appUserDemographics!!.email,
            contactPhoneNumber = scheduleRequest.appUserDemographics!!.homePhone!!,
            // list of scheduledDepartments
            preTriageTags = listOf("preTriageTag"),
            // 0 for default urgency
            urgency = 0,
            // requested, waitoffline,
            initialStatus = DefaultVisitStatus.Requested.status,
            // Refer to RelationshipToPatient for the full list
            actorRelationshipToPatient = scheduleRequest.relationshipToPatient,
        )
    }

    fun providerInformation(): ProviderVisitInformation {
        return ProviderVisitInformation(
            visitReason = scheduleRequest.reasonForVisit!!,
            patientDeclaration = scheduleRequest.patientDeclaration,
            userEmail = scheduleRequest.appUserDemographics!!.email,
            contactPhoneNumber = scheduleRequest.appUserDemographics!!.homePhone!!,
            actorRelationshipToPatient = scheduleRequest.relationshipToPatient,
            listOf(),
            false
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
    val relationshipToPatient: RelationshipToPatient = RelationshipToPatient.LegalGuardian,
    val retailClinic: RetailDepartment? = null,
    val provider: Provider? = null,
    val selectedTimeSlot: TimeSlot? = null,
)


enum class VisitType {
    Retail, Virtual, Provider
}
