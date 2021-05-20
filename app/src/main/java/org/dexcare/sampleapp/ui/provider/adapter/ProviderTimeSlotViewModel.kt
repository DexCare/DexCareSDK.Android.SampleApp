package org.dexcare.sampleapp.ui.provider.adapter

import org.dexcare.sampleapp.ui.common.viewmodel.BaseTimeSlotViewModel
import org.dexcare.services.provider.models.Provider
import org.dexcare.services.retail.models.TimeSlot

class ProviderTimeSlotViewModel(
    val timeSlot: TimeSlot? = null,
    val provider: Provider? = null
) : BaseTimeSlotViewModel(timeSlot)
