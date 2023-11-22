package com.dexcare.sample.data

import org.dexcare.DexCareSDK
import org.dexcare.dal.DataObserver
import org.dexcare.services.models.InsurancePayer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentRepository @Inject constructor(private val dexCareConfig: DexCareConfig) {

    fun getInsurancePayers(): DataObserver<List<InsurancePayer>> {
        val brand = dexCareConfig.tenant()
        return DexCareSDK.paymentService.getInsurancePayers(brand)
    }

    fun verifyCouponCode(couponCode: String): DataObserver<Double> {
        return DexCareSDK.paymentService.verifyCouponCode(couponCode)
    }
}
