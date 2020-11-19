package org.dexcare.sampleapp.ui.virtual.region.regionadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import org.dexcare.sampleapp.databinding.RecycleritemRegionBinding
import org.dexcare.sampleapp.ui.common.recyclerview.ViewModelAdapter
import org.dexcare.sampleapp.ui.virtual.VirtualSchedulingFlow
import org.dexcare.sampleapp.ui.virtual.region.VirtualRegionFragmentDirections
import org.dexcare.services.virtualvisit.models.Region
import org.koin.core.KoinComponent
import org.koin.core.get

class VirtualRegionAdapter(regionsList: MutableList<VirtualRegionViewModel>) : ViewModelAdapter<VirtualRegionViewModel>(regionsList), KoinComponent {
    override fun getViewDataBinding(
        objectInstance: ViewModel,
        inflater: LayoutInflater,
        parent: ViewGroup,
        attachToRoot: Boolean
    ): ViewDataBinding? {
        return RecycleritemRegionBinding.inflate(inflater).also { binding ->
            binding.btnRegion.setOnClickListener {
                get<VirtualSchedulingFlow>().region = binding.viewModel?.region
                it.findNavController().navigate(VirtualRegionFragmentDirections.toReasonForVisitFragment())
            }
        }
    }
}