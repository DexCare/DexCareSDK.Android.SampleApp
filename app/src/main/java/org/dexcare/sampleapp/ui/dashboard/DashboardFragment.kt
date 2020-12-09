package org.dexcare.sampleapp.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import org.dexcare.sampleapp.R
import org.dexcare.sampleapp.databinding.DashboardFragmentBinding

class DashboardFragment : Fragment() {

    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var binding: DashboardFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DashboardFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRetail.setOnClickListener {
            findNavController().navigate(DashboardFragmentDirections.toRetailClinicsFragment())
        }

        binding.btnVirtual.setOnClickListener {
            findNavController().navigate(DashboardFragmentDirections.toVirtualRegionFragment())
        }
    }

}