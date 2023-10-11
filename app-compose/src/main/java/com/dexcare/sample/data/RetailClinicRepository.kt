package com.dexcare.sample.data

import org.dexcare.DexCareSDK
import org.dexcare.dal.DataObserver
import org.dexcare.services.retail.models.RetailAppointmentTimeSlot
import org.dexcare.services.retail.models.RetailDepartment
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
}
