package org.dexcare.sampleapp.ui.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import org.dexcare.DexCareSDK
import org.dexcare.exts.virtualService
import org.dexcare.sampleapp.MainActivity
import org.dexcare.sampleapp.R
import org.dexcare.sampleapp.databinding.PaymentFragmentBinding
import org.dexcare.sampleapp.ext.showItemListDialog
import org.dexcare.sampleapp.ui.virtual.VirtualSchedulingFlow
import org.dexcare.services.models.InsuranceManualSelf
import org.dexcare.services.models.InsurancePayer
import org.dexcare.services.models.PatientDeclaration
import org.dexcare.services.virtualvisit.models.RegisterPushNotification
import org.dexcare.services.virtualvisit.models.VirtualVisitInformation
import org.koin.android.ext.android.inject
import timber.log.Timber

class PaymentFragment : Fragment() {

    private val viewModel: PaymentFragmentViewModel by viewModels()
    private lateinit var binding: PaymentFragmentBinding
    private val insuranceProviders = mutableListOf<InsurancePayer>()
    private val virtualSchedulingFlow: VirtualSchedulingFlow by inject()
    private var selectedInsurancePayer: InsurancePayer? = null

    // TODO: Implement Firebase SDK to retrieve fcm token
    private val mockRegisterPushNotification = RegisterPushNotification(
        "prod.FCM.acme.HealthConnect",
        "cZj396LBT3A:APA91bFruyeb4xOSKE2Pz2x5xPzoZgV0k_LiNc9zw64OoM3gu2_Q0gEQxEHA3Yhfny6HjK2inFj-wYor8BD24L-9XQUlY8Pz4brJ9myFI6DKVNucSobEBDJkMujsiHpuDX2ENTDzgZbn"
    )

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

        binding.btnVirtual.setOnClickListener {

            if (selectedInsurancePayer == null) return@setOnClickListener

            DexCareSDK.virtualService
                .startVirtualVisit(
                    this,
                    mockRegisterPushNotification,
                    InsuranceManualSelf(
                        viewModel.insuranceMemberId,
                        selectedInsurancePayer!!.payerId
                    ),
                    VirtualVisitInformation(
                        virtualSchedulingFlow.reasonForVisit,
                        PatientDeclaration.Self,
                        virtualSchedulingFlow.region!!.regionId,
                        virtualSchedulingFlow.patientDemographics!!.email,
                        virtualSchedulingFlow.patientDemographics!!.homePhone
                    )
                )
                .subscribe({
                    val visitId = it.first
                    val virtualVisitIntent = it.second
                    requireActivity().startActivityForResult(virtualVisitIntent, MainActivity.VIRTUAL_REQUEST_CODE)
                }, {
                    Timber.e(it)
                })
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
}
