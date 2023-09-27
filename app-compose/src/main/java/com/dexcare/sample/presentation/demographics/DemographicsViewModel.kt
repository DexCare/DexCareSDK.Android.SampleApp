package com.dexcare.sample.presentation.demographics

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dexcare.sample.data.PatientRepository
import com.dexcare.sample.data.SchedulingDataStore
import com.dexcare.sample.data.VisitType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.dexcare.services.models.PatientDeclaration
import org.dexcare.services.models.RelationshipToPatient
import org.dexcare.services.patient.models.DexCarePatient
import org.dexcare.services.patient.models.Gender
import org.dexcare.services.patient.models.PatientDemographics
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DemographicsViewModel @Inject constructor(
    private val patientRepository: PatientRepository,
    private val schedulingDataStore: SchedulingDataStore,
) : ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _state

    init {
        viewModelScope.launch {
            setInProgress(true)
            patientRepository.findPatient(
                onSuccess = { dexCarePatient ->
                    setInProgress(false)
                    dexCarePatient.demographicsLinks.firstOrNull()?.let {
                        setDemographicsFrom(it)
                    }
                },
                onError = {
                    setInProgress(false)
                }
            )
        }
    }

    fun onSubmit(patientInput: DemographicsInput, actorInput: DemographicsInput?) {
        Timber.d("patientInput:$patientInput")
        val isInputValid: Boolean
        if (actorInput != null) {
            val validatedActorInput = actorInput.validate()
            val isActorInputValid = validatedActorInput.isValid()

            val validatedPatientInput = patientInput.validate()
            val isPatientValid = validatedPatientInput.isValid()

            isInputValid = isActorInputValid && isPatientValid

            _state.update {
                it.copy(
                    patientDemographicsInput = validatedPatientInput,
                    actorDemographicsInput = validatedActorInput,
                )
            }
        } else {
            val validatedInput = patientInput.validate()
            isInputValid = validatedInput.isValid()

            _state.update {
                it.copy(
                    patientDemographicsInput = validatedInput,
                )
            }
        }
        if (isInputValid) {
            when (schedulingDataStore.scheduleRequest.visitType) {
                VisitType.Retail -> onContinueRetailFlow()
                VisitType.Virtual -> onContinueVirtualFlow()
                VisitType.Provider -> onContinueProviderFlow()
                null -> {
                    Timber.e("Visit Type not defined")
                }
            }
        }

    }

    private fun onContinueVirtualFlow() {
        setInProgress(true)
        if (PatientDeclaration.Self == _state.value.patientDeclaration) {
            findEhrSystem { ehrSystemName ->
                setUpAppUserDemographics(
                    ehrSystemName,
                    _state.value.patientDemographicsInput.mapToDemographics()
                ) { patient ->
                    schedulingDataStore.setPatient(patient)
                    _state.update { it.copy(demographicsComplete = true) }
                }
            }
        } else {
            findEhrSystem { ehrSystemName ->
                setUpDependentPatientLink(ehrSystemName) { dexCarePatient ->
                    schedulingDataStore.setPatient(dexCarePatient)
                    // The requirement for the Actor is that they have at least one demographic link.
                    // The EHR System of the Actor's demographic link does not matter for dependent visits,
                    // they just need to have a link.
                    // For simplicity in this sample app, we are always calling findOrCreatePatient
                    // which will ensure a link exists.
                    // If the Actor already has an existing link, calling findOrCreatePatient is not required.
                    setUpAppUserDemographics(
                        ehrSystemName,
                        _state.value.actorDemographicsInput.mapToDemographics()
                    ) { _ ->
                        _state.update { it.copy(demographicsComplete = true) }
                    }
                }
            }
        }
    }

    private fun onContinueRetailFlow() {
        val ehrSystemName = schedulingDataStore.scheduleRequest.retailClinic?.ehrSystemName
        if (ehrSystemName.isNullOrEmpty()) {
            Timber.e("Retail Clinic not defined for the visit.")
            return
        }
        findOrCreatePatientsWithEhrSystemName(ehrSystemName)
    }

    private fun onContinueProviderFlow() {
        val ehrSystemName = schedulingDataStore.scheduleRequest.provider!!.departments.first {
            it.departmentId == schedulingDataStore.scheduleRequest.selectedTimeSlot!!.departmentId
        }.ehrSystemName
        findOrCreatePatientsWithEhrSystemName(ehrSystemName)
    }

    fun onNavigationComplete() {
        _state.update {
            it.copy(demographicsComplete = false)
        }
    }

    private fun setInProgress(inProgress: Boolean) {
        _state.update { it.copy(inProgress = inProgress) }
    }

    private fun setDemographicsFrom(demographics: PatientDemographics) {
        val address = demographics.addresses.firstOrNull()
        val demographicsRecord = DemographicsInput.initialize()
            .withFirstName(demographics.name.given)
            .withLastName(demographics.name.family)
            .withGender(demographics.gender)
            .withEmail(demographics.email)
            .withPhone(demographics.homePhone.orEmpty())
            .withLast4Ssn(demographics.last4SSN)
            .withStreetAddress(address?.line1.orEmpty())
            .withAddressLine2(address?.line2.orEmpty())
            .withAddressLine2(address?.line2.orEmpty())
            .withCity(address?.city.orEmpty())
            .withState(address?.state.orEmpty())
            .withZipCode(address?.postalCode.orEmpty())

        _state.update {
            it.copy(
                patientDemographicsInput = demographicsRecord,
            )
        }
    }

    fun setBrandName(brandName: String) {
        _state.update { it.copy(brandName = brandName) }
    }

    fun onTabSelected(patientDeclaration: PatientDeclaration) {
        _state.update { it.copy(patientDeclaration = patientDeclaration) }
        schedulingDataStore.setPatientDeclaration(patientDeclaration)
    }

    fun onSelectGender(gender: Gender) {
        _state.update {
            it.copy(
                patientDemographicsInput = it.patientDemographicsInput.withGender(
                    gender
                )
            )
        }
    }

    fun onSelectBirthDate(date: LocalDate) {
        _state.update {
            it.copy(
                patientDemographicsInput = it.patientDemographicsInput.withDateOfBirth(
                    date
                )
            )
        }
    }

    fun setError(error: Throwable?) {
        _state.update { it.copy(error = error, inProgress = false) }
    }

    // Needed only for Virtual. For Retail and Provider, ehrSystem  can be found out from other system.
    // The patient is required to have a demographic link in the EHR system of the virtual department.
    // EHR system can be determined with catchment area.
    // The set demographics passed in to getCatchmentArea matters - it should always be the patient.
    private fun findEhrSystem(onComplete: (ehrSystem: String) -> Unit) {
        val patientDemographics = _state.value.patientDemographicsInput.mapToDemographics()
        val address = patientDemographics.addresses.first()
        patientRepository.findCatchmentArea(
            visitState = schedulingDataStore.scheduleRequest.virtualPracticeRegion?.regionCode.orEmpty(),
            residenceState = address.state,
            residenceZipCode = address.postalCode,
            brandName = _state.value.brandName,
        ).subscribe(onSuccess = {
            onComplete(it.ehrSystem)
        }, onError = {
            setError(it)
            Timber.d(it)
        })
    }

    private fun findOrCreatePatientsWithEhrSystemName(ehrSystemName: String) {
        if (_state.value.patientDeclaration == PatientDeclaration.Other) {
            setUpDependentPatientLink(ehrSystemName) {
                // The requirement for the Actor is that they have at least one demographic link.
                // The EHR System of the Actor's demographic link does not matter for dependent visits,
                // they just need to have a link.
                // For simplicity in this sample app, we are always calling findOrCreatePatient
                // which will ensure a link exists.
                // If the Actor already has an existing link, calling findOrCreatePatient is not required.
                setUpAppUserDemographics(
                    ehrSystemName,
                    _state.value.actorDemographicsInput.mapToDemographics()
                ) {
                    _state.update { it.copy(demographicsComplete = true) }
                }
            }
        } else {
            setUpAppUserDemographics(
                ehrSystemName,
                _state.value.patientDemographicsInput.mapToDemographics()
            ) {
                _state.update { it.copy(demographicsComplete = true) }
            }
        }
    }

    private fun setUpDependentPatientLink(ehrSystem: String, onComplete: (DexCarePatient) -> Unit) {
        patientRepository.findOrCreateDependentPatient(
            ehrSystem,
            _state.value.patientDemographicsInput.mapToDemographics()
        ).subscribe(onSuccess = {
            onComplete(it)
        }, onError = {
            setError(it)
            Timber.d(it)
        })
    }

    private fun setUpAppUserDemographics(
        ehrSystem: String,
        demographics: PatientDemographics,
        onComplete: (DexCarePatient) -> Unit
    ) {
        patientRepository.findOrCreateAppUserPatient(
            ehrSystem,
            demographics
        ).subscribe(onSuccess = {
            schedulingDataStore.setAppUserDemographics(demographics)
            onComplete(it)
        }, onError = {
            setError(it)
            Timber.d(it)
        })
    }

    @Stable
    data class UiState(
        /**
         * Person receiving the care.
         * */
        val patientDemographicsInput: DemographicsInput = DemographicsInput.initialize(),

        /**
         * App user booking the care for someone else.
         * */
        val actorDemographicsInput: DemographicsInput = DemographicsInput.initialize(),
        val demographicsComplete: Boolean = false,
        val inProgress: Boolean = false,
        val brandName: String = "",
        val patientDeclaration: PatientDeclaration = PatientDeclaration.Self,
        val error: Throwable? = null,
        val relationShipToPatient: RelationshipToPatient = RelationshipToPatient.LegalGuardian,//todo add input for someone else type
    )

}
