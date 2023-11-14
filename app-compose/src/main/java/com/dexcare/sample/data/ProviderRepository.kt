package com.dexcare.sample.data

import com.dexcare.sample.common.displayMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.dexcare.DexCareSDK
import org.dexcare.services.models.PaymentMethod
import org.dexcare.services.patient.models.DexCarePatient
import org.dexcare.services.provider.models.Provider
import org.dexcare.services.provider.models.ProviderTimeSlot
import org.dexcare.services.provider.models.ProviderVisitInformation
import org.dexcare.services.provider.models.ProviderVisitType
import org.dexcare.services.retail.models.TimeSlot
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProviderRepository @Inject constructor(private val dexCareConfig: DexCareConfig) {

    private var provider: Provider? = null
    private val providerTimeSlot =
        MutableStateFlow<ResultState<ProviderTimeSlot>>(ResultState.UnInitialized)

    fun getProvider(onResult: (Result<Provider>) -> Unit) {
        DexCareSDK.providerService.getProvider(dexCareConfig.getNationalProviderId())
            .subscribe(
                onSuccess = { provider ->
                    this.provider = provider
                    Timber.d("provider details:${provider}")
                    onResult(Result.success(provider))
                }, onError = { error ->
                    Timber.e(error)
                    onResult(Result.failure(error))
                }
            )
    }

    fun onVisitTypeSelected(visitType: ProviderVisitType) {
        providerTimeSlot.value = ResultState.UnInitialized
        val ehrSystemName = provider?.departments?.firstOrNull()?.ehrSystemName
        DexCareSDK.providerService.getMaxLookaheadDays(
            visitType.shortName.orEmpty(),
            ehrSystemName.orEmpty()
        ).subscribe(
            onSuccess = { days ->
                fetchTimeSlots(
                    provider?.providerNationalId.orEmpty(),
                    visitType.shortName.orEmpty(),
                    LocalDate.now(),
                    LocalDate.now().plusDays(days.toLong())
                )
            },
            onError = {
                providerTimeSlot.value =
                    ResultState.Error(ErrorResult(message = it.displayMessage()))
            }
        )
    }

    private fun fetchTimeSlots(
        providerNationId: String,
        visitTypeName: String,
        startDate: LocalDate,
        endDate: LocalDate
    ) {
        DexCareSDK.providerService.getProviderTimeslotsWithShortName(
            providerNationId,
            visitTypeName,
            startDate,
            endDate
        ).subscribe(
            onSuccess = { timeSlot ->
                providerTimeSlot.value = ResultState.Complete(timeSlot)
            },
            onError = {
                providerTimeSlot.value =
                    ResultState.Error(ErrorResult(message = it.displayMessage()))
            }
        )
    }

    fun observeTimeSlot(): StateFlow<ResultState<ProviderTimeSlot>> = providerTimeSlot

    fun scheduleVisit(
        paymentMethod: PaymentMethod,
        providerVisitInformation: ProviderVisitInformation,
        timeSlot: TimeSlot,
        ehrSystemName: String,
        patient: DexCarePatient,
        actor: DexCarePatient?,
        onComplete: (String?, ErrorResult?) -> Unit,
    ) {
        DexCareSDK.providerService.scheduleProviderVisit(
            paymentMethod,
            providerVisitInformation,
            timeSlot,
            ehrSystemName,
            patient, actor
        ).subscribe(onSuccess = {
            onComplete(it, null)
        }, onError = {
            onComplete(
                null,
                ErrorResult(
                    message = it.displayMessage()
                )
            )
        })
    }
}
