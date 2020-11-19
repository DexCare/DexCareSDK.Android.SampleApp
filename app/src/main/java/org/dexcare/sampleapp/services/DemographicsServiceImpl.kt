package org.dexcare.sampleapp.services

import org.dexcare.services.patient.models.DexCarePatient

class DemographicsServiceImpl : DemographicsService {
    private var dexCarePatient: DexCarePatient? = null

    override fun hasDemographicLinks(): Boolean {
        return dexCarePatient != null && dexCarePatient?.demographicsLinks?.isNotEmpty() ?: false
    }

    override fun getDemographics(): DexCarePatient? {
        return dexCarePatient
    }

    override fun setDemographics(dexCarePatient: DexCarePatient) {
        this.dexCarePatient = dexCarePatient
    }
}
