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
import org.koin.core.KoinComponent
import org.koin.core.get

class RetailTimeSlotAdapter(viewModels: MutableList<RetailTimeSlotViewModel>) :
    ViewModelAdapter<RetailTimeSlotViewModel>(viewModels),
    KoinComponent {
    override fun getViewDataBinding(
        objectInstance: ViewModel,
        inflater: LayoutInflater,
        parent: ViewGroup,
        attachToRoot: Boolean
    ): ViewDataBinding? {
        return RecycleritemTimeslotBinding.inflate(inflater).also { binding ->
            binding.btnTimeSlot.setOnClickListener {
                get<SchedulingInfo>().apply {
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

