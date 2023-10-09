package com.dexcare.sample.presentation.payment

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.dexcare.sample.data.ErrorResult
import com.dexcare.sample.data.ProviderRepository
import com.dexcare.sample.data.SchedulingDataStore
import com.dexcare.sample.data.VirtualVisitRepository
import com.dexcare.sample.data.VisitType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.dexcare.services.models.PaymentMethod
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val scheduleDataStore: SchedulingDataStore,
    private val virtualVisitRepository: VirtualVisitRepository,
    private val providerRepository: ProviderRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _state

    fun onSubmit(activity: FragmentActivity, paymentMethod: PaymentMethod) {
        val visitType = scheduleDataStore.scheduleRequest.visitType
        Timber.d("scheduleDataStore.scheduleRequest:${scheduleDataStore.scheduleRequest}")
        setLoading(true)
        when (visitType) {
            VisitType.Retail -> {

            }

            VisitType.Virtual -> {
                virtualVisitRepository.scheduleVisit(
                    activity,
                    scheduleDataStore.scheduleRequest.patient!!,
                    scheduleDataStore.createVirtualVisitDetails(activity),
                    paymentMethod
                ) { intent, throwable ->
                    setLoading(false)
                    if (intent != null) {
                        activity.startActivity(intent)
                    }

                    if (throwable != null) {
                        Timber.e(throwable)
                    }
                }
            }

            VisitType.Provider -> {
                val selectedTimeSlot = scheduleDataStore.scheduleRequest.selectedTimeSlot!!
                val ehrSystemName =
                    scheduleDataStore.scheduleRequest.provider?.departments?.firstOrNull {
                        it.departmentId == selectedTimeSlot.departmentId
                    }?.ehrSystemName.orEmpty()

                providerRepository.scheduleVisit(
                    paymentMethod,
                    scheduleDataStore.providerInformation(),
                    selectedTimeSlot,
                    ehrSystemName,
                    scheduleDataStore.scheduleRequest.patient!!,
                    actor = null,
                ) { visitId, error ->
                    setLoading(false)
                    Timber.d("visitId:$visitId, error:$error")
                    if (visitId != null) {
                        _state.update { it.copy(providerBookingComplete = true, loading = false) }
                    } else if (error != null) {
                        _state.update { it.copy(error = error, loading = false) }
                    }
                }
            }

            null -> {
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        _state.update { it.copy(loading = isLoading) }
    }

    data class UiState(
        val loading: Boolean = false,
        val error: ErrorResult? = null,
        val providerBookingComplete: Boolean = false,
        val retailBookingComplete: Boolean = false
    )

}
