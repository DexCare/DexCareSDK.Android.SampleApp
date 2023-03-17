package org.dexcare.sampleapp.ui.virtual.region

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import org.dexcare.sampleapp.R
import org.dexcare.sampleapp.databinding.VirtualRegionFragmentBinding
import org.dexcare.sampleapp.ext.showMaterialDialog
import org.dexcare.sampleapp.ui.virtual.region.regionadapter.VirtualPracticeRegionViewModel
import org.dexcare.sampleapp.ui.virtual.region.regionadapter.VirtualRegionAdapter
import org.koin.android.ext.android.get

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
            adapter = VirtualRegionAdapter(get(), mutableListOf())
            layoutManager = GridLayoutManager(requireContext(), 2)
        }

        viewModel.getVirtualPractice(getString(R.string.virtual_practice_id))
            .observe(viewLifecycleOwner) { virtualPractice ->
                (binding.recyclerRegions.adapter as? VirtualRegionAdapter)?.items =
                    virtualPractice.practiceRegions.map { region ->
                        VirtualPracticeRegionViewModel(region)
                    }.toMutableList()
            }

        viewModel.errorLiveData.observe(viewLifecycleOwner) {
            it?.let {
                showMaterialDialog(message = it.message)
            }
        }
    }
}
