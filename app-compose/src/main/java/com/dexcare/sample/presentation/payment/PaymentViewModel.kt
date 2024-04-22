package com.dexcare.sample.presentation.payment

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dexcare.sample.common.toError
import com.dexcare.sample.data.ErrorResult
import com.dexcare.sample.data.PaymentRepository
import com.dexcare.sample.data.ProviderRepository
import com.dexcare.sample.data.RetailClinicRepository
import com.dexcare.sample.data.SchedulingDataStore
import com.dexcare.sample.data.VirtualVisitRepository
import com.dexcare.sample.data.VisitType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.dexcare.services.models.InsurancePayer
import org.dexcare.services.models.PaymentMethod
import org.dexcare.services.virtualvisit.errors.InvalidCouponCodeError
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val scheduleDataStore: SchedulingDataStore,
    private val virtualVisitRepository: VirtualVisitRepository,
    private val providerRepository: ProviderRepository,
    private val paymentRepository: PaymentRepository,
    private val retailClinicRepository: RetailClinicRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _state

    private val visitType by lazy {
        scheduleDataStore.scheduleRequest.visitType
    }

    init {
        viewModelScope.launch {
            setLoading(true)
            paymentRepository.getInsurancePayers().subscribe({ payers ->
                _state.update { it.copy(loading = false, insurancePayers = payers) }
            }, { throwable ->
                _state.update { it.copy(loading = false, error = throwable.toError()) }
            })
        }
        if (visitType != null) {
            setUpSupportedPayments(visitType!!)
        } else {
            Timber.e("Selected VisitType is Invalid. Make sure VisitType is initialized in SchedulingDataStore.")
        }
    }

    private fun setUpSupportedPayments(visitType: VisitType) {
        val supportedPayments = when (visitType) {
            VisitType.Virtual -> {
                listOf(
                    PaymentMethod.PaymentMethod.CreditCard,
                    PaymentMethod.PaymentMethod.Insurance,
                    PaymentMethod.PaymentMethod.CouponCode
                )
            }

            VisitType.Provider -> {
                listOf(
                    PaymentMethod.PaymentMethod.Self,
                    PaymentMethod.PaymentMethod.Insurance,
                )
            }

            VisitType.Retail -> {
                listOf(
                    PaymentMethod.PaymentMethod.Self,
                    PaymentMethod.PaymentMethod.Insurance,
                )
            }
        }

        val visitCost = scheduleDataStore.scheduleRequest.virtualPracticeRegion?.let {
            "$ ${it.visitPrice}"
        }

        _state.update {
            it.copy(
                supportedPaymentMethods = supportedPayments,
                selectedPaymentType = supportedPayments.first(),
                originalVisitCost = visitCost
            )
        }
    }

    fun onPaymentTypeSelected(type: PaymentMethod.PaymentMethod) {
        _state.update { it.copy(selectedPaymentType = type) }
    }

    fun onSubmit(activity: FragmentActivity, paymentMethod: PaymentMethod) {
        setLoading(true)
        when (visitType) {
            VisitType.Retail -> {
                retailClinicRepository.scheduleClinicVisit(
                    paymentMethod = paymentMethod,
                    visitInformation = scheduleDataStore.retailInformation(),
                    timeSlot = scheduleDataStore.scheduleRequest.selectedTimeSlot!!,
                    ehrSystemName = scheduleDataStore.scheduleRequest.retailClinic!!.ehrSystemName,
                    patient = scheduleDataStore.scheduleRequest.patient!!,
                    actor = null
                ).subscribe({
                    _state.update { it.copy(loading = false, retailBookingComplete = true) }
                }, { error ->
                    _state.update { it.copy(error = error.toError(), loading = false) }
                })
            }

            VisitType.Virtual -> {
                virtualVisitRepository.scheduleVisit(
                    activity,
                    scheduleDataStore.scheduleRequest.patient!!,
                    scheduleDataStore.createVirtualVisitDetails(activity),
                    paymentMethod
                ) { intent, throwable ->
                    _state.update {
                        it.copy(
                            visitIntent = intent,
                            error = throwable?.toError()
                        )
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

    fun onSelectInsurancePayer(insurancePayer: InsurancePayer) {
        _state.update { it.copy(selectedPayer = insurancePayer) }
    }

    fun onApplyCouponCode(code: String) {
        if (code.isEmpty()) {
            return
        }
        _state.update { it.copy(isCouponVerificationInProgress = true) }
        paymentRepository.verifyCouponCode(code).subscribe({ discount ->
            var newCost = (scheduleDataStore.scheduleRequest.virtualPracticeRegion?.visitPrice
                ?: 0.0) - discount
            if (newCost < 0) {
                newCost = 0.0
            }

            _state.update {
                it.copy(
                    isCouponVerificationInProgress = false,
                    couponCodeError = null,
                    visitCostAfterCoupon = "$ $newCost",
                    couponCodeInput = code
                )
            }
        }, { error ->
            val message = if (error is InvalidCouponCodeError) {
                "Invalid Service key"
            } else {
                "Failed to verify service key"
            }
            _state.update {
                it.copy(
                    isCouponVerificationInProgress = false,
                    couponCodeError = message,
                    visitCostAfterCoupon = null
                )
            }
        })
    }

    private fun setLoading(isLoading: Boolean) {
        _state.update { it.copy(loading = isLoading) }
    }

    data class UiState(
        val loading: Boolean = false,
        val error: ErrorResult? = null,
        val providerBookingComplete: Boolean = false,
        val retailBookingComplete: Boolean = false,
        val insurancePayers: List<InsurancePayer> = emptyList(),
        val selectedPayer: InsurancePayer? = null,
        val selectedPaymentType: PaymentMethod.PaymentMethod = PaymentMethod.PaymentMethod.CreditCard,
        val visitIntent: Intent? = null,
        val supportedPaymentMethods: List<PaymentMethod.PaymentMethod> = emptyList(),
        val couponCodeInput: String = "",
        val isCouponVerificationInProgress: Boolean = false,
        val originalVisitCost: String? = null,
        val visitCostAfterCoupon: String? = null,
        val couponCodeError: String? = null,
    )
}

fun PaymentMethod.PaymentMethod.displayLabel(): String =
    when (this) {
        PaymentMethod.PaymentMethod.CreditCard -> "Credit Card"
        PaymentMethod.PaymentMethod.Insurance -> "Insurance"
        PaymentMethod.PaymentMethod.CouponCode -> "Service Key"
        PaymentMethod.PaymentMethod.Self -> "In Person"
    }
