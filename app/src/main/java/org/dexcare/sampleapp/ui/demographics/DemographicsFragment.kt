package org.dexcare.sampleapp.ui.demographics

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.dexcare.DexCareSDK
import org.dexcare.sampleapp.R
import org.dexcare.sampleapp.databinding.DemographicsFragmentBinding
import org.dexcare.sampleapp.ext.showItemListDialog
import org.dexcare.sampleapp.modules.GENDER_MAP
import org.dexcare.sampleapp.services.DemographicsService
import org.dexcare.sampleapp.ui.common.SchedulingFlow.*
import org.dexcare.sampleapp.ui.common.SchedulingInfo
import org.dexcare.services.patient.models.Address
import org.dexcare.services.patient.models.Gender
import org.dexcare.services.patient.models.HumanName
import org.dexcare.services.patient.models.PatientDemographics
import org.dexcare.services.virtualvisit.models.CatchmentArea
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.*

class DemographicsFragment : Fragment() {

    private val args: DemographicsFragmentArgs by navArgs()
    private val viewModel: DemographicsFragmentViewModel by viewModels()
    private lateinit var binding: DemographicsFragmentBinding
    private val schedulingInfo: SchedulingInfo by inject()
    private val demographicsService: DemographicsService by inject()

    private val genderMap: HashMap<String, Gender> by inject(GENDER_MAP)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DemographicsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel

        demographicsService.getDemographics()?.demographicsLinks?.firstOrNull()?.let {
            viewModel.setFromExistingDemographics(it)
        }

        binding.selfDemographicsLayout.parentInputLayout.genderInputText.apply {

            setOnClickListener {
                showGenderPickerDialog()
            }

            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) showGenderPickerDialog()
            }
        }

        binding.selfDemographicsLayout.parentInputLayout.dateOfBirthInputText.apply {
            setOnClickListener {
                showDatePickerDialog()
            }

            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) showDatePickerDialog()
            }
        }


        binding.btnContinue.setOnClickListener {
            if (!verifyInput()) return@setOnClickListener

            val demographics = collectDemographics()
            viewModel.loading = true

            when (args.schedulingFlow) {
                Retail -> findOrCreatePatientUsingEhrSystem(demographics)
                Virtual -> createPatientUsingVisitState(demographics)
                Unknown -> Toast.makeText(requireContext(), "Unknown flow", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun verifyInput(): Boolean {
        return viewModel.isValid()
    }

    private fun collectDemographics(): PatientDemographics {
        return viewModel.run {
            PatientDemographics(
                listOf(collectAddress()),
                dateOfBirth!!,
                email,
                gender!!,
                HumanName(lastName, firstName),
                last4SSN,
                phoneNumber
            )
        }
    }

    private fun collectAddress(): Address = viewModel.addressViewModel.run {
        Address(streetAddress, addressLine2, city, state, zipCode)
    }

    private fun showGenderPickerDialog() {
        activity?.showItemListDialog(
            genderMap.map {
                it.key
            },
            getString(R.string.gender)
        ) {
            val selectionText = genderMap.map { mapEntry ->
                mapEntry.key
            }[it]

            viewModel.gender = genderMap[selectionText]
        }
    }

    private fun showDatePickerDialog() {
        val dateOfBirth = Calendar.getInstance().apply {
            viewModel.dateOfBirth?.let {
                time = it
            } ?: set(1980, 0, 1)
        }

        DatePickerDialog(
            requireContext(), R.style.AlertDialog, { _, year, month, dayOfMonth ->
                val newDateTime = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }.time

                viewModel.dateOfBirth = newDateTime
            },
            dateOfBirth.get(Calendar.YEAR),
            dateOfBirth.get(Calendar.MONTH),
            dateOfBirth.get(Calendar.DAY_OF_MONTH)
        )
            .show()
    }

    private fun createPatientUsingVisitState(demographics: PatientDemographics) {
        DexCareSDK.patientService.getCatchmentArea(
            schedulingInfo.region?.regionId!!,
            demographics.addresses.first().state,
            demographics.addresses.first().postalCode,
            getString(R.string.brand)
        ).subscribe({ catchmentArea ->
            schedulingInfo.catchmentArea = catchmentArea
            findOrCreatePatientUsingCatchmentArea(
                catchmentArea,
                demographics
            )
        }, {
            Timber.e(it)
        })
    }

    private fun findOrCreatePatientUsingCatchmentArea(
        catchmentArea: CatchmentArea,
        demographics: PatientDemographics
    ) {
        DexCareSDK.patientService.findOrCreatePatient(
            catchmentArea.ehrSystem,
            demographics
        ).subscribe({
            get<DemographicsService>().setDemographics(it)
            schedulingInfo.patientDemographics = demographics
            findNavController().navigate(DemographicsFragmentDirections.toPaymentFragment(args.schedulingFlow))
        }, {
            Timber.e(it)
        }).onDisposed = {
            viewModel.loading = false
        }
    }

    private fun findOrCreatePatientUsingEhrSystem(demographics: PatientDemographics) {
        val ehrSystem = schedulingInfo.clinic!!.ehrSystemName
        DexCareSDK.patientService
            .findOrCreatePatient(ehrSystem, demographics)
            .subscribe({
                get<DemographicsService>().setDemographics(it)
                schedulingInfo.patientDemographics = demographics
                findNavController().navigate(DemographicsFragmentDirections.toPaymentFragment(args.schedulingFlow))
            }, {
                Timber.e(it)
            }).onDisposed = {
            viewModel.loading = false
        }
    }
}
