package org.dexcare.sampleapp.ui.retail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import org.dexcare.sampleapp.databinding.RecycleritemTimeslotBinding
import org.dexcare.sampleapp.ui.common.SchedulingFlow
import org.dexcare.sampleapp.ui.common.SchedulingInfo
import org.dexcare.sampleapp.ui.common.recyclerview.ViewModelAdapter
import org.dexcare.sampleapp.ui.virtual.region.VirtualRegionFragmentDirections

class RetailTimeSlotAdapter(
    private val schedulingInfo: SchedulingInfo,
    viewModels: MutableList<RetailTimeSlotViewModel>
) :
    ViewModelAdapter<RetailTimeSlotViewModel>(viewModels) {
    override fun getViewDataBinding(
        objectInstance: ViewModel,
        inflater: LayoutInflater,
        parent: ViewGroup,
        attachToRoot: Boolean
    ): ViewDataBinding {
        return RecycleritemTimeslotBinding.inflate(inflater).also { binding ->
            binding.btnTimeSlot.setOnClickListener {
                schedulingInfo.apply {
                    timeSlot = binding.viewModel?.timeSlot
                    clinic = binding.viewModel?.clinic
                }

                it.findNavController().navigate(
                    VirtualRegionFragmentDirections.toReasonForVisitFragment(
                        SchedulingFlow.Retail
                    )
                )
            }
        }
    }
}

