package org.dexcare.sampleapp.ui.common

import org.dexcare.services.models.PaymentMethod
import org.dexcare.services.patient.models.PatientDemographics
import org.dexcare.services.retail.models.Clinic
import org.dexcare.services.retail.models.TimeSlot
import org.dexcare.services.virtualvisit.models.CatchmentArea
import org.dexcare.services.virtualvisit.models.VirtualPracticeRegion

class SchedulingInfo {
    var virtualPracticeRegion: VirtualPracticeRegion? = null
    var clinic: Clinic? = null
    var timeSlot: TimeSlot? = null
    var reasonForVisit: String = ""
    var patientDemographics: PatientDemographics? = null
    var paymentMethod: PaymentMethod? = null
    var catchmentArea: CatchmentArea? = null

    fun clear() {
        virtualPracticeRegion = null
        clinic = null
        timeSlot = null
        reasonForVisit = ""
        patientDemographics = null
        paymentMethod = null
        catchmentArea = null
    }
}
