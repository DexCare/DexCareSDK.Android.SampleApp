package com.dexcare.sample.presentation.payment

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.dexcare.sample.data.SchedulingDataStore
import com.dexcare.sample.data.VirtualVisitRepository
import com.dexcare.sample.data.VisitType
import dagger.hilt.android.lifecycle.HiltViewModel
import org.dexcare.services.models.PaymentMethod
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val scheduleDataStore: SchedulingDataStore,
    private val virtualVisitRepository: VirtualVisitRepository
) : ViewModel() {

    fun onSubmit(activity: FragmentActivity, paymentMethod: PaymentMethod) {
        val visitType = scheduleDataStore.scheduleRequest.visitType
        Timber.d("scheduleDataStore.scheduleRequest:${scheduleDataStore.scheduleRequest}")

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
                    if (intent != null) {
                        activity.startActivity(intent)
                    }

                    if (throwable != null) {
                        Timber.e(throwable)
                    }
                }
            }

            VisitType.Provider -> {
            }

            null -> {
            }
        }
    }


}
