package org.dexcare.sampleapp.ui.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayout
import com.stripe.android.Stripe
import org.dexcare.DexCareSDK
import org.dexcare.sampleapp.MainActivity
import org.dexcare.sampleapp.R
import org.dexcare.sampleapp.databinding.PaymentFragmentBinding
import org.dexcare.sampleapp.ext.showItemListDialog
import org.dexcare.sampleapp.ext.showMaterialDialog
import org.dexcare.sampleapp.services.DemographicsService
import org.dexcare.sampleapp.ui.common.SchedulingFlow
import org.dexcare.sampleapp.ui.common.SchedulingInfo
import org.dexcare.services.models.*
import org.dexcare.services.virtualvisit.models.*
import org.dexcare.VisitStatus.*
import org.dexcare.services.provider.models.ProviderVisitInformation
import org.dexcare.services.retail.models.RetailVisitInformation
import org.dexcare.services.virtualvisit.models.RegisterPushNotification
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.text.NumberFormat

class PaymentFragment : Fragment() {

    private val args: PaymentFragmentArgs by navArgs()
    private val viewModel: PaymentFragmentViewModel by viewModels()
    private lateinit var binding: PaymentFragmentBinding
    private val insuranceProviders = mutableListOf<InsurancePayer>()
    private val schedulingInfo: SchedulingInfo by inject()
    private var selectedInsurancePayer: InsurancePayer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PaymentFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel

        when (args.schedulingFlow) {
            SchedulingFlow.Provider,
            SchedulingFlow.Retail -> {
                // Retail and Provider will just use SelfPayment in this sample app.
                // See https://developers.dexcarehealth.com/paymentmethod/ for supported
                // Payment types for each method of care
                binding.paymentInputsLayout.visibility = View.GONE
                binding.tabLayoutPayment.visibility = View.GONE
            }
            SchedulingFlow.Virtual -> {
                binding.layoutCouponCodeInput.root.visibility = View.GONE
                binding.layoutCreditCardInput.root.visibility = View.GONE
            }
        }

        viewModel.errorLiveData.observe(viewLifecycleOwner, {
            it?.let {
                showMaterialDialog(message = it.javaClass.simpleName)
            }
        })

        viewModel.getInsuranceProviders(getString(R.string.brand))
            .observe(viewLifecycleOwner, { insuranceProviders ->
                this.insuranceProviders.clear()
                this.insuranceProviders.addAll(insuranceProviders)
            })

        binding.tabLayoutPayment.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(p0: TabLayout.Tab?) {

                when (p0?.text) {
                    getString(R.string.insurance) -> {
                        binding.layoutInsuranceInput.root.visibility = View.VISIBLE
                        binding.layoutCreditCardInput.root.visibility = View.GONE
                        binding.layoutCouponCodeInput.root.visibility = View.GONE
                        binding.btnBookVisit.isEnabled = true
                        schedulingInfo.selectedPaymentOption = PaymentOption.INSURANCE
                    }
                    getString(R.string.credit_card) -> {
                        binding.layoutInsuranceInput.root.visibility = View.GONE
                        binding.layoutCreditCardInput.root.visibility = View.VISIBLE
                        binding.layoutCouponCodeInput.root.visibility = View.GONE
                        binding.btnBookVisit.isEnabled = true
                        schedulingInfo.selectedPaymentOption = PaymentOption.CREDIT_CARD
                    }
                    getString(R.string.coupon_code) -> {
                        binding.layoutInsuranceInput.root.visibility = View.GONE
                        binding.layoutCreditCardInput.root.visibility = View.GONE
                        binding.layoutCouponCodeInput.root.visibility = View.VISIBLE
                        binding.btnBookVisit.isEnabled = false
                        schedulingInfo.selectedPaymentOption = PaymentOption.COUPON_CODE
                    }

                    else -> {
                        throw Exception("Unsupported tab")
                    }
                }
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }

            override fun onTabReselected(p0: TabLayout.Tab?) {

            }
        })

        binding.layoutInsuranceInput.insuranceProviderInputText.apply {
            setOnClickListener {
                showInsuranceProviderList()
            }

            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) showInsuranceProviderList()
            }
        }

        binding.layoutCouponCodeInput.couponCodeInputText.addTextChangedListener {
            viewModel.discountAmount = "$0.00"
            binding.btnBookVisit.isEnabled = false
        }

        binding.layoutCouponCodeInput.btnVerifyCouponCode.setOnClickListener {
            DexCareSDK.paymentService.verifyCouponCode(viewModel.couponCode)
                .subscribe({
                    val formattedDiscount = NumberFormat.getCurrencyInstance().format(it)
                    viewModel.discountAmount = formattedDiscount
                    binding.btnBookVisit.isEnabled = true
                }, {
                    viewModel.errorLiveData.value = it
                })
        }

        binding.btnBookVisit.setOnClickListener {

            when (args.schedulingFlow) {
                SchedulingFlow.Provider -> {
                    bookProviderVisit()
                }
                SchedulingFlow.Retail -> {
                    bookRetailVisit()
                }
                SchedulingFlow.Virtual -> {
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

    private fun createVirtualVisitDetails(stateLicensure: String): VirtualVisitDetails =
        VirtualVisitDetails(
            acceptedTerms = true, // patient has accepted terms of service
            assignmentQualifiers = listOf(DefaultVirtualVisitAssignmentQualifiers.Adult.qualifier), // qualifications to assign visit to a provider
            patientDeclaration = schedulingInfo.patientDeclaration, // is this visit being submitted by the patient or by a proxy
            stateLicensure = stateLicensure, //"WA"  // state licensure required for provider to see patient
            visitReason = schedulingInfo.reasonForVisit,
            visitTypeName = DefaultVirtualVisitTypes.Virtual.type,
            practiceId = getString(R.string.virtual_practice_id),
            assessmentToolUsed = "ada", // if patient has done preassessment, which tool was used
            brand = "default",
            interpreterLanguage = "interpreterLanguage", // optional language requested if interpreter services are available; ISO 639-3 Individual Language codes
            userEmail = schedulingInfo.patientDemographics!!.email,
            contactPhoneNumber = schedulingInfo.patientDemographics!!.homePhone!!,
            preTriageTags = listOf("preTriageTag"), // list of scheduledDepartments
            urgency = 0, // 0 for default urgency
            initialStatus = DefaultVisitStatus.Requested.status // requested, waitoffline
        )

    private fun bookVirtualVisit() {
        // Patient represents the person receiving care (not necessarily the app user)
        val patient = when (schedulingInfo.patientDeclaration) {
            PatientDeclaration.Other -> schedulingInfo.dependentPatient!!
            else -> get<DemographicsService>().getDemographics()!!
        }

        // Actor represents the app user booking for someone else
        val actor = when (schedulingInfo.patientDeclaration) {
            PatientDeclaration.Other -> get<DemographicsService>().getDemographics()!!
            else -> null
        }

        val relationshipToPatient = when (schedulingInfo.patientDeclaration) {
            PatientDeclaration.Other -> schedulingInfo.actorRelationshipToPatient!!
            else -> null
        }

        val payment = createPaymentMethod()
        if (payment == null) {
            showMaterialDialog(message = "Invalid payment information")
            return
        }

        viewModel.loading = true

        DexCareSDK.virtualService
            .createVirtualVisit(
                fragment = this,
                patient = patient,
                virtualVisitDetails = createVirtualVisitDetails(schedulingInfo.patientDemographics?.addresses?.firstOrNull()?.state.orEmpty()),
                paymentMethod = payment,
                virtualActor = null, // individual acting as a Patient proxy
                registerPushNotification = null
            )
            .subscribe({
                // You can save this visitId for a later `resumeVirtualVisit`
                // if something goes wrong.
                val visitId = it.second
                val virtualVisitIntent = it.third

                (requireActivity() as MainActivity).activityResultLauncher.launch(virtualVisitIntent)
            }, {
                viewModel.errorLiveData.value = it
                Timber.e(it)
            }).onDisposed = {
            viewModel.loading = false
        }
    }


    private fun bookProviderVisit() {
        // Patient represents the person receiving care (not necessarily the app user)
        val patient = when (schedulingInfo.patientDeclaration) {
            PatientDeclaration.Other -> schedulingInfo.dependentPatient!!
            else -> get<DemographicsService>().getDemographics()!!
        }

        // Actor represents the app user booking for someone else
        val actor = when (schedulingInfo.patientDeclaration) {
            PatientDeclaration.Other -> get<DemographicsService>().getDemographics()!!
            else -> null
        }

        val relationshipToPatient = when (schedulingInfo.patientDeclaration) {
            PatientDeclaration.Other -> schedulingInfo.actorRelationshipToPatient!!
            else -> null
        }

        val payment = createPaymentMethod()
        if (payment == null) {
            showMaterialDialog(message = "Invalid payment information")
            return
        }

        val ehrSystemName = schedulingInfo.provider!!.departments.first {
            it.departmentId == schedulingInfo.timeSlot!!.departmentId
        }.ehrSystemName

        viewModel.loading = true
        DexCareSDK.providerService
            .scheduleProviderVisit(
                payment,
                ProviderVisitInformation(
                    visitReason = schedulingInfo.reasonForVisit,
                    patientDeclaration = schedulingInfo.patientDeclaration,
                    userEmail = schedulingInfo.patientDemographics!!.email,
                    contactPhoneNumber = schedulingInfo.patientDemographics!!.homePhone!!,
                    actorRelationshipToPatient = relationshipToPatient
                ),
                timeSlot = schedulingInfo.timeSlot!!,
                ehrSystemName = ehrSystemName,
                patientDexCarePatient = patient,
                actorDexCarePatient = actor
            ).subscribe({
                // Note: there's no way to cancel Provider visits through the SDK.
                showMaterialDialog(message = "Provider visit booked")
                findNavController().navigate(R.id.dashboardFragment)
                schedulingInfo.clear()
            }, {
                viewModel.errorLiveData.value = it
                Timber.e(it)
            }).onDisposed = {
            viewModel.loading = false
        }
    }

    private fun bookRetailVisit() {
        // Patient represents the person receiving care (not necessarily the app user)
        val patient = when (schedulingInfo.patientDeclaration) {
            PatientDeclaration.Other -> schedulingInfo.dependentPatient!!
            else -> get<DemographicsService>().getDemographics()!!
        }

        // Actor represents the app user booking for someone else
        val actor = when (schedulingInfo.patientDeclaration) {
            PatientDeclaration.Other -> get<DemographicsService>().getDemographics()!!
            else -> null
        }

        val relationshipToPatient = when (schedulingInfo.patientDeclaration) {
            PatientDeclaration.Other -> schedulingInfo.actorRelationshipToPatient!!
            else -> null
        }

        val payment = createPaymentMethod()
        if (payment == null) {
            showMaterialDialog(message = "Invalid payment information")
            return
        }

        viewModel.loading = true
        DexCareSDK.retailService
            .scheduleRetailAppointment(
                payment,
                RetailVisitInformation(
                    schedulingInfo.reasonForVisit,
                    schedulingInfo.patientDeclaration,
                    schedulingInfo.patientDemographics!!.email,
                    schedulingInfo.patientDemographics!!.homePhone!!,
                    actorRelationshipToPatient = relationshipToPatient
                ),
                schedulingInfo.timeSlot!!,
                schedulingInfo.clinic!!.ehrSystemName,
                patientDexCarePatient = patient,
                actorDexCarePatient = actor
            ).subscribe({
                showMaterialDialog(message = "Retail visit booked")
                findNavController().navigate(R.id.dashboardFragment)
                schedulingInfo.clear()
            }, {
                viewModel.errorLiveData.value = it
                Timber.e(it)
            }).onDisposed = {
            viewModel.loading = false
        }
    }

    private fun createRegisterPushNotification(): RegisterPushNotification {
        // TODO: Implement Firebase SDK to retrieve fcm token
        return RegisterPushNotification(
            getString(R.string.fcm_app_id),
            "cZj396LBT3A:APA91bFruyeb4xOSKE2Pz2x5xPzoZgV0k_LiNc9zw64OoM3gu2_Q0gEQxEHA3Yhfny6HjK2inFj-wYor8BD24L-9XQUlY8Pz4brJ9myFI6DKVNucSobEBDJkMujsiHpuDX2ENTDzgZbn"
        )
    }

    private fun createPaymentMethod(): PaymentMethod? {
        return when (args.schedulingFlow) {
            SchedulingFlow.Provider,
            SchedulingFlow.Retail -> SelfPayment()
            SchedulingFlow.Virtual -> {
                when (schedulingInfo.selectedPaymentOption) {
                    PaymentOption.INSURANCE -> {
                        if (selectedInsurancePayer == null) return null
                        InsuranceManualSelf(
                            viewModel.insuranceMemberId,
                            selectedInsurancePayer!!.payerId
                        )
                    }
                    PaymentOption.CREDIT_CARD -> {
                        val cardParams = binding.layoutCreditCardInput.cardInputWidget.cardParams
                            ?: return null
                        val token = Stripe(requireContext(), getString(R.string.stripe_publishable_key))
                            .createCardTokenSynchronous(cardParams)
                        CreditCard(token!!.id)
                    }
                    PaymentOption.COUPON_CODE -> SelfPayment()//CouponCode(viewModel.couponCode)
                }
            }
            else -> null
        }
    }
}
