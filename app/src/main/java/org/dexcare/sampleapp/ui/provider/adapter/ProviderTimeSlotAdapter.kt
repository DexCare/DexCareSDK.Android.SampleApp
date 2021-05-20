package org.dexcare.sampleapp.ui.provider.adapter

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

class ProviderTimeSlotAdapter(
    private val schedulingInfo: SchedulingInfo,
    viewModels: MutableList<ProviderTimeSlotViewModel>
) :
    ViewModelAdapter<ProviderTimeSlotViewModel>(viewModels) {
    override fun getViewDataBinding(
        objectInstance: ViewModel,
        inflater: LayoutInflater,
        parent: ViewGroup,
        attachToRoot: Boolean
    ): ViewDataBinding {
        return RecycleritemTimeslotBinding.inflate(inflater).also { binding ->
            binding.btnTimeSlot.setOnClickListener {
                (binding.viewModel as? ProviderTimeSlotViewModel)?.let { viewModel ->

                    schedulingInfo.apply {
                        timeSlot = viewModel.timeSlot
                        provider = viewModel.provider
                    }

                    it.findNavController().navigate(
                        VirtualRegionFragmentDirections.toReasonForVisitFragment(
                            SchedulingFlow.Provider
                        )
                    )
                }
            }
        }
    }
}

