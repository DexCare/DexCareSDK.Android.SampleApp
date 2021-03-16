package org.dexcare.sampleapp.ui.demographics

import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import org.dexcare.sampleapp.modules.GENDER_MAP
import org.dexcare.sampleapp.modules.RELATIONSHIP_TO_PATIENT_LIST
import org.dexcare.sampleapp.ui.common.viewmodel.BaseViewModel
import org.dexcare.sampleapp.ui.common.viewmodel.input.address.AddressViewModel
import org.dexcare.services.models.RelationshipToPatient
import org.dexcare.services.patient.models.Gender
import org.dexcare.services.patient.models.PatientDemographics
import org.dexcare.util.EmailValidator
import org.dexcare.util.PhoneValidator
import org.koin.core.component.get
import org.koin.core.component.inject
import java.text.SimpleDateFormat
import java.util.*

class DemographicsViewModel : BaseViewModel() {

    @Bindable
    var firstName: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.firstName)
        }

    @Bindable
    var lastName: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.lastName)
        }

    @Bindable
    var email: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.email)
        }

    var dateOfBirth: Date? = null
        set(value) {
            field = value
            dateOfBirthString = value?.let {
                SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).format(it)
            } ?: ""
        }

    @Bindable
    var dateOfBirthString: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.dateOfBirthString)
        }

    var gender: Gender? = null
        set(value) {
            field = value

            genderString = value?.let {
                get<HashMap<String, Gender>>(GENDER_MAP).asIterable().firstOrNull {
                    it.value == value
                }?.key ?: ""
            } ?: ""
        }

    @Bindable
    var genderString: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.genderString)
        }

    @Bindable
    var last4SSN: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.last4SSN)
        }

    @Bindable
    var phoneNumber: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.phoneNumber)
        }

    @get:Bindable
    val addressViewModel: AddressViewModel by inject()

    var relationshipToPatient: RelationshipToPatient? = null
        set(value) {
            field = value

            relationshipToPatientString = value?.let {
                get<List<Pair<String, RelationshipToPatient>>>(RELATIONSHIP_TO_PATIENT_LIST).asIterable()
                    .firstOrNull {
                        it.second == value
                    }?.first ?: ""
            } ?: ""
        }

    @Bindable
    var relationshipToPatientString: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.relationshipToPatientString)
        }

    fun isValid(): Boolean {
        return firstName.isNotEmpty() &&
                lastName.isNotEmpty() &&
                last4SSN.isNotEmpty() &&
                dateOfBirth != null &&
                gender != null &&
                PhoneValidator.isValid(phoneNumber) &&
                EmailValidator.isValid(email) &&
                addressViewModel.isValid()
    }

    fun setFromExistingDemographics(patientDemographics: PatientDemographics) {
        firstName = patientDemographics.name.given
        lastName = patientDemographics.name.family
        gender = patientDemographics.gender
        last4SSN = patientDemographics.last4SSN
        dateOfBirth = patientDemographics.birthdate
        email = patientDemographics.email
        phoneNumber = patientDemographics.homePhone
        patientDemographics.addresses.firstOrNull()?.let {
            addressViewModel.apply {
                streetAddress = it.line1
                addressLine2 = it.line2 ?: ""
                city = it.city
                state = it.state
                zipCode = it.postalCode
            }
        }
    }
}
