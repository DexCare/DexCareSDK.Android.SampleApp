package com.dexcare.sample.presentation.demographics

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dexcare.sample.data.PatientRepository
import com.dexcare.sample.data.SchedulingDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.dexcare.DexCareSDK
import org.dexcare.services.models.PatientDeclaration
import org.dexcare.services.patient.models.Address
import org.dexcare.services.patient.models.Gender
import org.dexcare.services.patient.models.HumanName
import org.dexcare.services.patient.models.PatientDemographics
import org.dexcare.services.virtualvisit.models.CatchmentArea
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
        val validatedInput = patientInput.validate()
        val isValid = validatedInput.isValid()

        _state.update {
            it.copy(
                patientDemographicsInput = validatedInput,
                inputComplete = isValid
            )
        }

        if (isValid) {
            setPatient()
        }
    }

    fun onNavigationComplete() {
        _state.update {
            it.copy(inputComplete = false)
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

    private fun setPatient() {
        val patientDemographics = _state.value.patientDemographicsInput.mapInputToPatient()
        val address = patientDemographics.addresses.first()
        getCatchmentArea(
            schedulingDataStore.scheduleRequest.virtualPracticeRegion?.regionCode.orEmpty(),
            state = address.state,
            zipCode = address.postalCode,
            brandName = _state.value.brandName,
            onSuccess = {
                findOrCreateAppUserPatient(it.ehrSystem, patientDemographics)
            },
            onError = {

            }
        )


    }

    //only for virtual, ehrSystem can be found out from other system for retail and provider
    private fun getCatchmentArea(
        regionCode: String,
        state: String,
        zipCode: String,
        brandName: String,
        onSuccess: (CatchmentArea) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        setInProgress(true)
        DexCareSDK.patientService.getCatchmentArea(
            regionCode,
            state,
            zipCode,
            brandName
        ).subscribe({ catchmentArea ->
            setInProgress(false)
            onSuccess(catchmentArea)
        }, {
            setInProgress(false)
            onError(it)
        })
    }

    private fun findOrCreateAppUserPatient(
        ehrSystem: String,
        appUserDemographics: PatientDemographics
    ) {
        setInProgress(true)
        DexCareSDK.patientService
            .findOrCreatePatient(ehrSystem, appUserDemographics)
            .subscribe(
                {
                    setInProgress(false)
                    schedulingDataStore.setPatient(it)
                    schedulingDataStore.setAppUserDemographics(appUserDemographics)
                }, {
                    setInProgress(false)
                    Timber.e(it)
                })
    }

    private fun DemographicsInput.mapInputToPatient() = PatientDemographics(
        addresses = listOf(
            Address(
                line1 = streetAddress.input.orEmpty(),
                line2 = addressLine2.input.orEmpty(),
                city = city.input.orEmpty(),
                state = state.input.orEmpty(),
                postalCode = zipCode.input.orEmpty()
            )
        ),
        birthdate = dateOfBirth.input ?: LocalDate.now(),
        email = email.input.orEmpty(),
        gender = gender.input ?: Gender.Unknown,
        name = HumanName(lastName.input.orEmpty(), firstName.input.orEmpty()),
        last4SSN = last4Ssn.input.orEmpty(),
        homePhone = phone.input.orEmpty(),
    )

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
        val inputComplete: Boolean = false,
        val inProgress: Boolean = false,
        val brandName: String = "",
        val patientDeclaration: PatientDeclaration = PatientDeclaration.Self,
    )

}
