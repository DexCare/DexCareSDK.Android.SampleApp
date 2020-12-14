package org.dexcare.sampleapp.ui.payment

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
import org.dexcare.sampleapp.MainActivity
import org.dexcare.sampleapp.R
import org.dexcare.sampleapp.databinding.PaymentFragmentBinding
import org.dexcare.sampleapp.ext.showItemListDialog
import org.dexcare.sampleapp.services.DemographicsService
import org.dexcare.sampleapp.ui.common.SchedulingFlow
import org.dexcare.sampleapp.ui.common.SchedulingInfo
import org.dexcare.services.models.InsuranceManualSelf
import org.dexcare.services.models.InsurancePayer
import org.dexcare.services.models.PatientDeclaration
import org.dexcare.services.models.SelfPayment
import org.dexcare.services.retail.models.RetailVisitInformation
import org.dexcare.services.virtualvisit.models.RegisterPushNotification
import org.dexcare.services.virtualvisit.models.VirtualVisitInformation
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import timber.log.Timber

class PaymentFragment : Fragment() {

    private val args: PaymentFragmentArgs by navArgs()
    private val viewModel: PaymentFragmentViewModel by viewModels()
    private lateinit var binding: PaymentFragmentBinding
    private val insuranceProviders = mutableListOf<InsurancePayer>()
    private val schedulingInfo: SchedulingInfo by inject()
    private var selectedInsurancePayer: InsurancePayer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PaymentFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel

        viewModel.getInsuranceProviders(getString(R.string.brand))
            .observe(viewLifecycleOwner, { insuranceProviders ->
                this.insuranceProviders.clear()
                this.insuranceProviders.addAll(insuranceProviders)
            })

        binding.insuranceProviderInputText.apply {
            setOnClickListener {
                showInsuranceProviderList()
            }

            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) showInsuranceProviderList()
            }
        }

        binding.btnBookVisit.setOnClickListener {

            when (args.schedulingFlow) {
                SchedulingFlow.Retail -> {
                    bookRetailVisit()
                }
                SchedulingFlow.Virtual -> {
                    if (selectedInsurancePayer == null) return@setOnClickListener

                    bookVirtualVisit()
                }
                else -> {
                    Toast.makeText(requireContext(), "Unknown flow", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showInsuranceProviderList() {
        if (insuranceProviders.isEmpty()) return

        val displayList = insuranceProviders.map {
            it.name
        }

        activity?.showItemListDialog(
            displayList,
            getString(R.string.insurance_providers)
        ) {
            val selectionText = displayList[it]

            selectedInsurancePayer = insuranceProviders.first { it.name == selectionText }
            viewModel.insuranceProviderName = selectionText
        }
    }

    private fun bookVirtualVisit() {
        DexCareSDK.virtualService
            .startVirtualVisit(
                this,
                createRegisterPushNotification(),
                InsuranceManualSelf(
                    viewModel.insuranceMemberId,
                    selectedInsurancePayer!!.payerId
                ),
                VirtualVisitInformation(
                    schedulingInfo.reasonForVisit,
                    PatientDeclaration.Self,
                    schedulingInfo.region!!.regionId,
                    schedulingInfo.patientDemographics!!.email,
                    schedulingInfo.patientDemographics!!.homePhone
                ),
                schedulingInfo.catchmentArea!!,
                get<DemographicsService>().getDemographics()!!
            )
            .subscribe({
                val visitId = it.first
                val virtualVisitIntent = it.second
                requireActivity().startActivityForResult(
                    virtualVisitIntent,
                    MainActivity.VIRTUAL_REQUEST_CODE
                )
            }, {
                Timber.e(it)
            })
    }

    private fun bookRetailVisit() {
        DexCareSDK.retailService
            .scheduleRetailAppointment(
                SelfPayment(),
                RetailVisitInformation(
                    schedulingInfo.reasonForVisit,
                    PatientDeclaration.Self,
                    schedulingInfo.patientDemographics!!.email,
                    schedulingInfo.patientDemographics!!.homePhone
                ),
                schedulingInfo.timeSlot!!,
                schedulingInfo.clinic!!.ehrSystemName,
                get<DemographicsService>().getDemographics()!!
            ).subscribe({
                Toast.makeText(requireContext(), "Retail visit booked", Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.dashboardFragment)
                schedulingInfo.clear()
            }, {
                Timber.e(it)
            })
    }

    private fun createRegisterPushNotification(): RegisterPushNotification {
        // TODO: Implement Firebase SDK to retrieve fcm token
        return RegisterPushNotification(
            getString(R.string.fcm_app_id),
            "cZj396LBT3A:APA91bFruyeb4xOSKE2Pz2x5xPzoZgV0k_LiNc9zw64OoM3gu2_Q0gEQxEHA3Yhfny6HjK2inFj-wYor8BD24L-9XQUlY8Pz4brJ9myFI6DKVNucSobEBDJkMujsiHpuDX2ENTDzgZbn"
        )
    }
}
