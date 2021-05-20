package org.dexcare.sampleapp.ui.common

import org.dexcare.sampleapp.ui.payment.PaymentOption
import org.dexcare.services.models.PatientDeclaration
import org.dexcare.services.models.PaymentMethod
import org.dexcare.services.models.RelationshipToPatient
import org.dexcare.services.patient.models.DexCarePatient
import org.dexcare.services.patient.models.PatientDemographics
import org.dexcare.services.provider.models.Provider
import org.dexcare.services.retail.models.Clinic
import org.dexcare.services.retail.models.TimeSlot
import org.dexcare.services.virtualvisit.models.CatchmentArea
import org.dexcare.services.virtualvisit.models.VirtualPracticeRegion

// This should be replaced by something better in your app
class SchedulingInfo {
    var virtualPracticeRegion: VirtualPracticeRegion? = null
    var clinic: Clinic? = null
    var provider: Provider? = null
    var timeSlot: TimeSlot? = null
    var reasonForVisit: String = ""
    var patientDemographics: PatientDemographics? = null
    var paymentMethod: PaymentMethod? = null
    var catchmentArea: CatchmentArea? = null
    var patientDeclaration: PatientDeclaration = PatientDeclaration.Self
    var dependentPatient: DexCarePatient? = null
    var actorRelationshipToPatient: RelationshipToPatient? = null
    var selectedPaymentOption: PaymentOption = PaymentOption.INSURANCE

    fun clear() {
        virtualPracticeRegion = null
        clinic = null
        provider = null
        timeSlot = null
        reasonForVisit = ""
        patientDemographics = null
        paymentMethod = null
        catchmentArea = null
        patientDeclaration = PatientDeclaration.Self
        dependentPatient = null
        actorRelationshipToPatient = null
        selectedPaymentOption = PaymentOption.INSURANCE
    }
}
