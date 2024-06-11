package com.dexcare.sample.presentation.demographics

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dexcare.sample.common.displayMessage
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
                    dexCarePatient.demographicsLinks.firstOrNull()?.let { patient ->
                        _state.update { it.copy(ehrSystemName = patient.getEhrSystemName()) }
                        setDemographicsFrom(patient)
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
                    otherPatientDemographicsInput = validatedPatientInput,
                    actorDemographicsInput = validatedActorInput,
                )
            }
        } else {
            val validatedInput = patientInput.validate()
            isInputValid = validatedInput.isValid()

            _state.update {
                it.copy(
                    selfPatientDemographicsInput = validatedInput,
                )
            }
        }
        if (isInputValid) {
            when (schedulingDataStore.scheduleRequest.visitType) {
                VisitType.Retail -> onContinueRetailFlow()
                VisitType.Virtual -> onContinueVirtualFlow()
                VisitType.Provider -> onContinueProviderFlow()
                else -> {
                    Timber.e("Visit Type not defined")
                }
            }
        }

    }

    private fun onContinueVirtualFlow() {
        setInProgress(true)
        val ehrSystemName =
            schedulingDataStore.scheduleRequest.virtualPracticeRegion?.departments?.firstOrNull()?.ehrSystemName.orEmpty()
        if (PatientDeclaration.Self == _state.value.patientDeclaration) {
            setUpAppUserDemographics(
                ehrSystemName,
                _state.value.selfPatientDemographicsInput.mapToDemographics()
            ) { patient ->
                schedulingDataStore.setPatient(patient)
                _state.update { it.copy(demographicsComplete = true) }
            }
        } else {
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

    private fun onContinueRetailFlow() {
        setInProgress(true)
        val ehrSystemName = _state.value.ehrSystemName
            ?: schedulingDataStore.scheduleRequest.retailClinic?.ehrSystemName
        if (ehrSystemName.isNullOrEmpty()) {
            _state.update {
                it.copy(
                    errorMessage = "Retail Clinic not defined for the visit.",
                    inProgress = false
                )
            }
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
            .withDateOfBirth(demographics.birthdate)
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
                selfPatientDemographicsInput = demographicsRecord,
                actorDemographicsInput = demographicsRecord
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

    fun onSelectGender(gender: Gender, isPatient: Boolean) {
        _state.update {
            if (it.patientDeclaration == PatientDeclaration.Self) {
                it.copy(
                    selfPatientDemographicsInput = it.selfPatientDemographicsInput.withGender(
                        gender
                    )
                )
            } else if (isPatient) {
                it.copy(
                    otherPatientDemographicsInput = it.otherPatientDemographicsInput.withGender(
                        gender
                    )
                )
            } else {
                it.copy(
                    actorDemographicsInput = it.actorDemographicsInput.withGender(
                        gender
                    )
                )
            }
        }
    }

    fun onSelectBirthDate(date: LocalDate, isPatient: Boolean) {
        _state.update {
            if (it.patientDeclaration == PatientDeclaration.Self) {
                it.copy(
                    selfPatientDemographicsInput = it.selfPatientDemographicsInput.withDateOfBirth(
                        date
                    )
                )
            } else if (isPatient) {
                it.copy(
                    otherPatientDemographicsInput = it.otherPatientDemographicsInput.withDateOfBirth(
                        date
                    )
                )
            } else {
                it.copy(
                    actorDemographicsInput = it.actorDemographicsInput.withDateOfBirth(
                        date
                    )
                )
            }
        }
    }

    fun setError(error: String?) {
        _state.update { it.copy(errorMessage = error, inProgress = false) }
    }

    private fun findOrCreatePatientsWithEhrSystemName(ehrSystemName: String) {
        setInProgress(true)
        if (_state.value.patientDeclaration == PatientDeclaration.Other) {
            setUpDependentPatientLink(ehrSystemName) { patient ->
                schedulingDataStore.setPatient(patient)
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
                    _state.update { it.copy(demographicsComplete = true, inProgress = false) }
                }
            }
        } else {
            setUpAppUserDemographics(
                ehrSystemName,
                _state.value.selfPatientDemographicsInput.mapToDemographics()
            ) { patient ->
                schedulingDataStore.setPatient(patient)
                _state.update { it.copy(demographicsComplete = true, inProgress = false) }
            }
        }
    }

    private fun setUpDependentPatientLink(ehrSystem: String, onComplete: (DexCarePatient) -> Unit) {
        setInProgress(true)
        patientRepository.findOrCreateDependentPatient(
            ehrSystem,
            _state.value.otherPatientDemographicsInput.mapToDemographics()
        ).subscribe(onSuccess = {
            onComplete(it)
            setInProgress(false)
        }, onError = {
            setError(it.displayMessage())
            setInProgress(false)
            Timber.e("error::$it")
        })
    }

    private fun setUpAppUserDemographics(
        ehrSystem: String,
        demographics: PatientDemographics,
        onComplete: (DexCarePatient) -> Unit
    ) {
        setInProgress(true)
        patientRepository.findOrCreateAppUserPatient(
            ehrSystem,
            demographics
        ).subscribe(onSuccess = {
            schedulingDataStore.setAppUserDemographics(demographics)
            setInProgress(false)
            onComplete(it)
        }, onError = {
            setInProgress(false)
            setError(it.displayMessage())
            Timber.e("error::$it")
        })
    }

    @Stable
    data class UiState(
        /**
         * Person receiving the care.
         * */
        val selfPatientDemographicsInput: DemographicsInput = DemographicsInput.initialize(),

        /**
         * Person receiving care when visit is being booked for someone else.
         *
         * */
        val otherPatientDemographicsInput: DemographicsInput = DemographicsInput.initialize(),

        /**
         * App user booking the care for someone else.
         * */
        val actorDemographicsInput: DemographicsInput = DemographicsInput.initialize(),
        val demographicsComplete: Boolean = false,
        val inProgress: Boolean = false,
        val brandName: String = "",
        val patientDeclaration: PatientDeclaration = PatientDeclaration.Self,
        val errorMessage: String? = null,
        val ehrSystemName: String? = null,
        val relationShipToPatient: RelationshipToPatient = RelationshipToPatient.LegalGuardian,//todo add input for someone else type
    )

}
