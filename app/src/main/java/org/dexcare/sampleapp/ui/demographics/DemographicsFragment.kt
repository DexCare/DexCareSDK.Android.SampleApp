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
import com.google.android.material.tabs.TabLayout
import org.dexcare.DexCareSDK
import org.dexcare.sampleapp.R
import org.dexcare.sampleapp.databinding.DemographicsFragmentBinding
import org.dexcare.sampleapp.ext.showItemListDialog
import org.dexcare.sampleapp.ext.showMaterialDialog
import org.dexcare.sampleapp.modules.GENDER_MAP
import org.dexcare.sampleapp.modules.RELATIONSHIP_TO_PATIENT_LIST
import org.dexcare.sampleapp.services.DemographicsService
import org.dexcare.sampleapp.ui.common.SchedulingFlow.*
import org.dexcare.sampleapp.ui.common.SchedulingInfo
import org.dexcare.services.models.PatientDeclaration
import org.dexcare.services.models.RelationshipToPatient
import org.dexcare.services.patient.models.Address
import org.dexcare.services.patient.models.Gender
import org.dexcare.services.patient.models.HumanName
import org.dexcare.services.patient.models.PatientDemographics
import org.dexcare.services.virtualvisit.models.CatchmentArea
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.time.LocalDate
import java.time.Month
import java.util.*

class DemographicsFragment : Fragment() {

    private val args: DemographicsFragmentArgs by navArgs()
    private val viewModel: DemographicsFragmentViewModel by viewModels()
    private lateinit var binding: DemographicsFragmentBinding
    private val schedulingInfo: SchedulingInfo by inject()
    private val demographicsService: DemographicsService by inject()

    private val genderMap: HashMap<String, Gender> by inject(GENDER_MAP)
    private val relationshipToPatientList: List<Pair<String, RelationshipToPatient>> by inject(
        RELATIONSHIP_TO_PATIENT_LIST
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DemographicsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel

        viewModel.errorLiveData.observe(viewLifecycleOwner, {
            it?.let {
                showMaterialDialog(message = it.javaClass.simpleName)
            }
        })

        demographicsService.getDemographics()?.demographicsLinks?.firstOrNull()?.let {
            viewModel.appUserDemographics.setFromExistingDemographics(it)
        }

        setupClickListenersMySelfTab()
        setupClickListenersSomeoneElseTab()

        binding.tabLayoutDemographics.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(p0: TabLayout.Tab?) {
                when (p0) {
                    // Someone Else tab
                    binding.tabLayoutDemographics.getTabAt(1) -> {
                        binding.selfDemographicsLayout.root.visibility = View.GONE
                        binding.someoneElseDemographicsLayout.root.visibility = View.VISIBLE
                        schedulingInfo.patientDeclaration = PatientDeclaration.Other
                    }
                    else -> {
                        binding.selfDemographicsLayout.root.visibility = View.VISIBLE
                        binding.someoneElseDemographicsLayout.root.visibility = View.GONE
                        schedulingInfo.patientDeclaration = PatientDeclaration.Self
                    }
                }
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }

            override fun onTabReselected(p0: TabLayout.Tab?) {

            }
        })

        binding.btnContinue.setOnClickListener {
            if (!verifyInput()) {
                showMaterialDialog(
                    getString(R.string.invalid_input_title),
                    getString(R.string.invalid_input_message)
                )
                return@setOnClickListener
            }

            viewModel.loading = true

            when (args.schedulingFlow) {
                Provider -> onContinueClickedProvider()
                Retail -> onContinueClickedRetail()
                Virtual -> onContinueClickedVirtual()
                Unknown -> Toast.makeText(requireContext(), "Unknown flow", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun verifyInput(): Boolean {
        return when (schedulingInfo.patientDeclaration) {
            PatientDeclaration.Self -> viewModel.appUserDemographics.isValid()
            PatientDeclaration.Other -> {
                viewModel.appUserDemographics.isValid()
                        && viewModel.someoneElseDemographics.isValid()
                        // relationship to patient is only required for the actor on someone else visits
                        && viewModel.appUserDemographics.relationshipToPatient != null
            }
        }
    }

    private fun collectAppUserDemographics(): PatientDemographics {
        return viewModel.appUserDemographics.run {
            PatientDemographics(
                listOf(collectAddress(this)),
                dateOfBirth!!,
                email,
                gender!!,
                HumanName(lastName, firstName),
                last4SSN,
                phoneNumber
            )
        }
    }

    private fun collectDependentPatientDemographics(): PatientDemographics {
        return viewModel.someoneElseDemographics.run {
            PatientDemographics(
                listOf(collectAddress(this)),
                dateOfBirth!!,
                email,
                gender!!,
                HumanName(lastName, firstName),
                last4SSN,
                phoneNumber
            )
        }
    }

    private fun collectAddress(viewModel: DemographicsViewModel): Address =
        viewModel.addressViewModel.run {
            Address(streetAddress, addressLine2, city, state, zipCode)
        }

    private fun showGenderPickerDialog(viewModel: DemographicsViewModel) {
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

    private fun showRelationshipPickerDialog(viewModel: DemographicsViewModel) {
        activity?.showItemListDialog(
            relationshipToPatientList.map {
                it.first
            },
            getString(R.string.relationship_to_patient)
        ) {
            val selectionText = relationshipToPatientList.map { listEntry ->
                listEntry.first
            }[it]

            val relationshipToPatient = relationshipToPatientList.first { it.first == selectionText }.second

            viewModel.relationshipToPatient = relationshipToPatient
            schedulingInfo.actorRelationshipToPatient = relationshipToPatient
        }
    }

    private fun showDatePickerDialog(viewModel: DemographicsViewModel) {
        val dateOfBirth = viewModel.dateOfBirth ?: LocalDate.of(1980, Month.JANUARY, 1)

        // The Java 8 Date Time models have month values of 1-12.
        // Java Calendar has month values of 0-11.
        // We need to add and subtract 1 here to get the proper month values
        // for use with this date picker dialog
        DatePickerDialog(
            requireContext(),
            R.style.AlertDialog,
            { _, year, month, dayOfMonth ->
                val newDateTime = LocalDate.of(year, month + 1, dayOfMonth)
                viewModel.dateOfBirth = newDateTime
            },
            dateOfBirth.year,
            dateOfBirth.monthValue - 1,
            dateOfBirth.dayOfMonth
        )
            .show()
    }

    private fun onContinueClickedVirtual() {
        val demographics = when (schedulingInfo.patientDeclaration) {
            PatientDeclaration.Self -> collectAppUserDemographics()
            PatientDeclaration.Other -> collectDependentPatientDemographics()
        }

        // The patient is required to have a demographic link in the EHR system of the virtual department.
        // EHR system can be determined with catchment area.
        // The set demographics passed in to getCatchmentArea matters - it should always be the patient.
        getCatchmentAreaForDemographics(demographics, callback = {
            val ehrSystem = it.ehrSystem
            if (schedulingInfo.patientDeclaration == PatientDeclaration.Other) {
                findOrCreateDependentPatient(ehrSystem, callback = {
                    // The requirement for the Actor is that they have at least one demographic link.
                    // The EHR System of the Actor's demographic link does not matter for dependent visits, they just need to have a link.
                    // For simplicity in this sample app, we are always calling findOrCreatePatient which will ensure a link exists.
                    // If the Actor already has an existing link, calling findOrCreatePatient is not required.
                    findOrCreateAppUserPatient(ehrSystem)
                })
            } else if (schedulingInfo.patientDeclaration == PatientDeclaration.Self) {
                findOrCreateAppUserPatient(ehrSystem)
            }
        })
    }

    private fun getCatchmentAreaForDemographics(
        demographics: PatientDemographics,
        callback: (catchmentArea: CatchmentArea) -> Unit
    ) {
        DexCareSDK.patientService.getCatchmentArea(
            schedulingInfo.virtualPracticeRegion!!.regionCode,
            demographics.addresses.first().state,
            demographics.addresses.first().postalCode,
            getString(R.string.brand)
        ).subscribe({ catchmentArea ->
            schedulingInfo.catchmentArea = catchmentArea

            callback.invoke(catchmentArea)
        }, {
            viewModel.errorLiveData.value = it
            Timber.e(it)
            viewModel.loading = false
        })
    }

    private fun onContinueClickedProvider() {
        val department = schedulingInfo.provider!!.departments.first {
            it.departmentId == schedulingInfo.timeSlot!!.departmentId
        }

        val ehrSystem = department.ehrSystemName
        findOrCreatePatientsWithEhrSystemName(ehrSystem)
    }

    private fun onContinueClickedRetail() {
        val ehrSystem = schedulingInfo.clinic!!.ehrSystemName
        findOrCreatePatientsWithEhrSystemName(ehrSystem)
    }

    fun findOrCreatePatientsWithEhrSystemName(ehrSystemName: String) {
        if (schedulingInfo.patientDeclaration == PatientDeclaration.Other) {
            findOrCreateDependentPatient(ehrSystemName) {
                // The requirement for the Actor is that they have at least one demographic link.
                // The EHR System of the Actor's demographic link does not matter for dependent visits, they just need to have a link.
                // For simplicity in this sample app, we are always calling findOrCreatePatient which will ensure a link exists.
                // If the Actor already has an existing link, calling findOrCreatePatient is not required.
                findOrCreateAppUserPatient(ehrSystemName)
            }
            return
        }

        findOrCreateAppUserPatient(ehrSystemName)
    }

    private fun findOrCreateDependentPatient(ehrSystem: String, callback: () -> Unit) {
        DexCareSDK.patientService.findOrCreateDependentPatient(
            ehrSystem,
            collectDependentPatientDemographics()
        ).subscribe({
            schedulingInfo.dependentPatient = it
            callback.invoke()
        }, {
            viewModel.errorLiveData.value = it
            viewModel.loading = false
            Timber.e(it)
        })
    }


    private fun findOrCreateAppUserPatient(ehrSystem: String) {
        val appUserDemographics = collectAppUserDemographics()
        DexCareSDK.patientService
            .findOrCreatePatient(ehrSystem, appUserDemographics)
            .subscribe({
                get<DemographicsService>().setDemographics(it)
                schedulingInfo.patientDemographics = appUserDemographics
                findNavController().navigate(DemographicsFragmentDirections.toPaymentFragment(args.schedulingFlow))
            }, {
                viewModel.errorLiveData.value = it
                Timber.e(it)
            }).onDisposed = {
            viewModel.loading = false
        }
    }

    private fun setupClickListenersMySelfTab() {
        binding.selfDemographicsLayout.genderInputText.apply {

            setOnClickListener {
                showGenderPickerDialog(viewModel.appUserDemographics)
            }

            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) showGenderPickerDialog(viewModel.appUserDemographics)
            }
        }

        binding.selfDemographicsLayout.dateOfBirthInputText.apply {
            setOnClickListener {
                showDatePickerDialog(viewModel.appUserDemographics)
            }

            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) showDatePickerDialog(viewModel.appUserDemographics)
            }
        }
    }

    private fun setupClickListenersSomeoneElseTab() {
        // On Someone Else tab, the patient is the "someone else"
        binding.someoneElseDemographicsLayout.patientGenderInputText.apply {

            setOnClickListener {
                showGenderPickerDialog(viewModel.someoneElseDemographics)
            }

            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) showGenderPickerDialog(viewModel.someoneElseDemographics)
            }
        }

        binding.someoneElseDemographicsLayout.patientDateOfBirthInputText.apply {
            setOnClickListener {
                showDatePickerDialog(viewModel.someoneElseDemographics)
            }

            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) showDatePickerDialog(viewModel.someoneElseDemographics)
            }
        }

        // actor is the app user
        binding.someoneElseDemographicsLayout.actorGenderInputText.apply {

            setOnClickListener {
                showGenderPickerDialog(viewModel.appUserDemographics)
            }

            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) showGenderPickerDialog(viewModel.appUserDemographics)
            }
        }

        binding.someoneElseDemographicsLayout.actorDateOfBirthInputText.apply {
            setOnClickListener {
                showDatePickerDialog(viewModel.appUserDemographics)
            }

            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) showDatePickerDialog(viewModel.appUserDemographics)
            }
        }

        binding.someoneElseDemographicsLayout.relationshipToPatientInputText.apply {
            setOnClickListener {
                showRelationshipPickerDialog(viewModel.appUserDemographics)
            }

            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) showRelationshipPickerDialog(viewModel.appUserDemographics)
            }
        }
    }
}
