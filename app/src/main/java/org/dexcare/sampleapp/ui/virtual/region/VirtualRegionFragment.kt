package org.dexcare.sampleapp.ui.virtual.region

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.dexcare.sampleapp.R
import org.dexcare.sampleapp.databinding.RecycleritemRegionBinding
import org.dexcare.sampleapp.databinding.VirtualRegionFragmentBinding
import org.dexcare.sampleapp.ui.common.recyclerview.ViewModelAdapter
import org.dexcare.sampleapp.ui.virtual.region.regionadapter.VirtualRegionAdapter
import org.dexcare.sampleapp.ui.virtual.region.regionadapter.VirtualRegionViewModel

class VirtualRegionFragment : Fragment() {

    private val viewModel: VirtualRegionFragmentViewModel by viewModels()
    private lateinit var binding: VirtualRegionFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = VirtualRegionFragmentBinding.inflate(layoutInflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerRegions.apply {
            adapter = VirtualRegionAdapter(mutableListOf())
            layoutManager = GridLayoutManager(requireContext(), 2)
        }

        viewModel.getRegions(getString(R.string.brand))
            .observe(viewLifecycleOwner, { regionsList ->
                (binding.recyclerRegions.adapter as? VirtualRegionAdapter)?.items =
                    regionsList.map { region ->
                        VirtualRegionViewModel(region)
                    }.toMutableList()
            })
    }
}
