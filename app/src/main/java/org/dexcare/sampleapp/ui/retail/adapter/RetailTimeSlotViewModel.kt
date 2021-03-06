package org.dexcare.sampleapp.ui.retail.adapter

import org.dexcare.sampleapp.ui.common.viewmodel.BaseTimeSlotViewModel
import org.dexcare.services.retail.models.Clinic
import org.dexcare.services.retail.models.TimeSlot

class RetailTimeSlotViewModel(
    val timeSlot: TimeSlot? = null,
    val clinic: Clinic? = null
) : BaseTimeSlotViewModel(timeSlot)
