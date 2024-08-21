package com.dexcare.sample.data.repository

import com.dexcare.sample.data.model.AppEnvironment
import org.dexcare.DexCareSDK
import org.dexcare.dal.DataObserver
import org.dexcare.services.models.InsurancePayer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentRepository @Inject constructor(private val environmentsRepository: EnvironmentsRepository) {

    private val appEnvironment: AppEnvironment by lazy {
        environmentsRepository.findSelectedEnvironment() ?: run {
            throw IllegalStateException("Can not continue without a tenant. Make sure an AppEnvironment is selected.")
        }
    }

    fun getInsurancePayers(): DataObserver<List<InsurancePayer>> {
        return DexCareSDK.paymentService.getInsurancePayers(appEnvironment.tenant)
    }

    fun verifyCouponCode(couponCode: String): DataObserver<Double> {
        return DexCareSDK.paymentService.verifyCouponCode(couponCode)
    }
}
