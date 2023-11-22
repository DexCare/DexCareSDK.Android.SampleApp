package com.dexcare.sample.data

import org.dexcare.DexCareSDK
import org.dexcare.dal.DataObserver
import org.dexcare.services.models.PaymentMethod
import org.dexcare.services.patient.models.DexCarePatient
import org.dexcare.services.retail.models.RetailAppointmentTimeSlot
import org.dexcare.services.retail.models.RetailDepartment
import org.dexcare.services.retail.models.RetailVisitInformation
import org.dexcare.services.retail.models.TimeSlot
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetailClinicRepository @Inject constructor() {

    fun getClinics(brand: String): DataObserver<List<RetailDepartment>> {
        return DexCareSDK.retailService
            .getRetailDepartments(brand)
    }

    fun getTimeSlots(departmentName: String): DataObserver<RetailAppointmentTimeSlot> {
        return DexCareSDK.retailService.getTimeSlots(departmentName)
    }

    fun scheduleClinicVisit(
        paymentMethod: PaymentMethod,
        visitInformation: RetailVisitInformation,
        timeSlot: TimeSlot,
        ehrSystemName: String,
        patient: DexCarePatient,
        actor: DexCarePatient?
    ): DataObserver<String> {
        return DexCareSDK.retailService.scheduleRetailAppointment(
            paymentMethod,
            visitInformation,
            timeSlot,
            ehrSystemName,
            patient,
            actor
        )
    }
}
