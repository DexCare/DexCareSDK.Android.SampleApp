package com.dexcare.sample.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.dexcare.DexCareSDK
import org.dexcare.services.provider.models.Provider
import org.dexcare.services.provider.models.ProviderTimeSlot
import org.dexcare.services.provider.models.ProviderVisitType
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProviderRepository @Inject constructor() {

    private var provider: Provider? = null
    private val providerTimeSlot = MutableStateFlow<ProviderTimeSlot?>(null)

    fun getProvider(nationalProviderId: String, onResult: (Result<Provider>) -> Unit) {
        DexCareSDK.providerService.getProvider(nationalProviderId)
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
        providerTimeSlot.value = null
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
                Timber.e(it)
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
                providerTimeSlot.value = timeSlot
            },
            onError = {
                providerTimeSlot.value = null
            }
        )
    }

    fun observeTimeSlot(): StateFlow<ProviderTimeSlot?> = providerTimeSlot
}
