package org.dexcare.sampleapp.ui.virtual.region.regionadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import org.dexcare.sampleapp.databinding.RecycleritemRegionBinding
import org.dexcare.sampleapp.ui.common.SchedulingFlow
import org.dexcare.sampleapp.ui.common.SchedulingInfo
import org.dexcare.sampleapp.ui.common.recyclerview.ViewModelAdapter
import org.dexcare.sampleapp.ui.virtual.region.VirtualRegionFragmentDirections

class VirtualRegionAdapter(
    private val schedulingInfo: SchedulingInfo,
    regionsList: MutableList<VirtualRegionViewModel>
) : ViewModelAdapter<VirtualRegionViewModel>(regionsList) {
    override fun getViewDataBinding(
        objectInstance: ViewModel,
        inflater: LayoutInflater,
        parent: ViewGroup,
        attachToRoot: Boolean
    ): ViewDataBinding? {
        return RecycleritemRegionBinding.inflate(inflater).also { binding ->
            binding.btnRegion.setOnClickListener {
                schedulingInfo.region = binding.viewModel?.region
                it.findNavController().navigate(
                    VirtualRegionFragmentDirections.toReasonForVisitFragment(
                        SchedulingFlow.Virtual
                    )
                )
            }
        }
    }
}