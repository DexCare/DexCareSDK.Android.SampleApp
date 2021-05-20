package org.dexcare.sampleapp.ui.retail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import org.dexcare.sampleapp.R
import org.dexcare.sampleapp.databinding.RetailClinicsFragmentBinding
import org.dexcare.sampleapp.ext.showMaterialDialog
import org.dexcare.sampleapp.ui.retail.adapter.RetailClinicAdapter
import org.dexcare.sampleapp.ui.retail.adapter.RetailClinicViewModel

class RetailClinicsFragment : Fragment() {

    private val viewModel: RetailClinicsFragmentViewModel by viewModels()
    private lateinit var binding: RetailClinicsFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RetailClinicsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel
        binding.recyclerClinics.apply {
            adapter = RetailClinicAdapter(mutableListOf())
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        viewModel.errorLiveData.observe(viewLifecycleOwner, {
            it?.let {
                showMaterialDialog(message = it.javaClass.simpleName)
            }
        })

        viewModel.getClinics(getString(R.string.brand))
            .observe(viewLifecycleOwner, { clinicList ->

                (binding.recyclerClinics.adapter as? RetailClinicAdapter)?.items =
                    clinicList.map { clinic ->
                        RetailClinicViewModel(clinic)
                    }.toMutableList()

                fetchTimeSlots()
            })
    }

    private fun fetchTimeSlots() {
        viewModel.getTimeSlots()
            .observe(viewLifecycleOwner, { timeSlotList ->
                (binding.recyclerClinics.adapter as? RetailClinicAdapter)?.items?.map { viewModel ->
                    timeSlotList.map { clinicTimeSlot ->
                        if (viewModel.clinic?.departmentID == clinicTimeSlot.departmentId) {
                            viewModel.clinicTimeSlot = clinicTimeSlot
                        }
                    }
                }
            })
    }
}
