package org.dexcare.sampleapp.ui.reasonforvisit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.dexcare.sampleapp.databinding.ReasonForVisitFragmentBinding
import org.dexcare.sampleapp.ui.common.SchedulingInfo
import org.koin.android.ext.android.inject

class ReasonForVisitFragment : Fragment() {

    private val args: ReasonForVisitFragmentArgs by navArgs()
    private val viewModel: ReasonForVisitFragmentViewModel by viewModels()
    private lateinit var binding: ReasonForVisitFragmentBinding
    private val schedulingInfo: SchedulingInfo by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ReasonForVisitFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel

        viewModel.reasonForVisit = schedulingInfo.reasonForVisit

        binding.btnContinue.setOnClickListener {
            schedulingInfo.reasonForVisit = viewModel.reasonForVisit
            findNavController().navigate(ReasonForVisitFragmentDirections.toDemographicsFragment(args.schedulingFlow))
        }
    }
}
