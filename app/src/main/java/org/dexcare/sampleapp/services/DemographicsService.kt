package org.dexcare.sampleapp.services

import org.dexcare.services.patient.models.DexCarePatient

interface DemographicsService {
    fun hasDemographicLinks(): Boolean
    fun getDemographics(): DexCarePatient?
    fun setDemographics(dexCarePatient: DexCarePatient)
}
