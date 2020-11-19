package org.dexcare.sampleapp.ui.virtual

import org.dexcare.services.models.PaymentMethod
import org.dexcare.services.patient.models.PatientDemographics
import org.dexcare.services.virtualvisit.models.Region

class VirtualSchedulingFlow {
    var region: Region? = null
    var reasonForVisit: String = ""
    var patientDemographics: PatientDemographics? = null
    var paymentMethod: PaymentMethod? = null

    fun clear() {
        region = null
        reasonForVisit = ""
        patientDemographics = null
        paymentMethod = null
    }
}
