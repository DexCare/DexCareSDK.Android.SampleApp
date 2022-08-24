package org.dexcare.sampleapp.ui.retail.adapter

import org.dexcare.sampleapp.ui.common.viewmodel.BaseTimeSlotViewModel
import org.dexcare.services.retail.models.RetailDepartment
import org.dexcare.services.retail.models.TimeSlot

class RetailTimeSlotViewModel(
    val timeSlot: TimeSlot? = null,
    val clinic: RetailDepartment? = null
) : BaseTimeSlotViewModel(timeSlot)
