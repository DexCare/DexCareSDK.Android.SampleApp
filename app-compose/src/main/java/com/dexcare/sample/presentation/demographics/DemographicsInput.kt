package com.dexcare.sample.presentation.demographics

import androidx.compose.runtime.Stable
import org.dexcare.services.patient.models.Address
import org.dexcare.services.patient.models.Gender
import org.dexcare.services.patient.models.HumanName
import org.dexcare.services.patient.models.PatientDemographics
import java.time.LocalDate

@Stable
data class DemographicsInput(
    val firstName: FormField<String>,
    val lastName: FormField<String>,
    val email: FormField<String>,
    val phone: FormField<String>,
    val last4Ssn: FormField<String>,
    val gender: FormField<Gender>,
    val dateOfBirth: FormField<LocalDate>,
    val streetAddress: FormField<String>,
    val addressLine2: FormField<String>,
    val city: FormField<String>,
    val state: FormField<String>,
    val zipCode: FormField<String>,
) {

    fun isValid(): Boolean {
        return firstName.error.isNullOrEmpty() &&
                lastName.error.isNullOrEmpty() &&
                email.error.isNullOrEmpty() &&
                phone.error.isNullOrEmpty() &&
                last4Ssn.error.isNullOrEmpty() &&
                gender.error.isNullOrEmpty() &&
                dateOfBirth.error.isNullOrEmpty() &&
                streetAddress.error.isNullOrEmpty() &&
                addressLine2.error.isNullOrEmpty() &&
                city.error.isNullOrEmpty() &&
                state.error.isNullOrEmpty() &&
                zipCode.error.isNullOrEmpty()
    }

    fun validate(): DemographicsInput {
        return DemographicsInput(
            firstName = this.firstName.validate(),
            lastName = this.lastName.validate(),
            email = this.email.validate(),
            phone = this.phone.validate(),
            last4Ssn = this.last4Ssn.validate(),
            gender = this.gender.validate(),
            dateOfBirth = this.dateOfBirth.validate(),
            streetAddress = this.streetAddress.validate(),
            addressLine2 = this.addressLine2.validate(),
            city = this.city.validate(),
            state = this.state.validate(),
            zipCode = this.zipCode.validate(),
        )
    }

    fun withFirstName(input: String): DemographicsInput {
        return copy(firstName = this.firstName.resetWith(input))
    }

    fun withLastName(input: String): DemographicsInput {
        return copy(lastName = this.lastName.resetWith(input))
    }

    fun withEmail(input: String): DemographicsInput {
        return copy(email = this.email.resetWith(input))
    }

    fun withPhone(input: String): DemographicsInput {
        return copy(phone = this.phone.resetWith(input))
    }

    fun withLast4Ssn(input: String): DemographicsInput {
        return copy(last4Ssn = this.last4Ssn.resetWith(input))
    }

    fun withDateOfBirth(input: LocalDate?): DemographicsInput {
        return copy(dateOfBirth = this.dateOfBirth.resetWith(input))
    }

    fun withGender(input: Gender): DemographicsInput {
        return copy(gender = this.gender.resetWith(input))
    }

    fun withStreetAddress(input: String): DemographicsInput {
        return copy(streetAddress = this.streetAddress.resetWith(input))
    }

    fun withAddressLine2(input: String): DemographicsInput {
        return copy(addressLine2 = this.addressLine2.resetWith(input))
    }

    fun withCity(input: String): DemographicsInput {
        return copy(city = this.city.resetWith(input))
    }

    fun withState(input: String): DemographicsInput {
        return copy(state = this.state.resetWith(input))
    }

    fun withZipCode(input: String): DemographicsInput {
        return copy(zipCode = this.zipCode.resetWith(input))
    }

    override fun toString(): String {
        return "firstName=${firstName}, " +
                "lastName=${lastName}, " +
                "email=${email}, " +
                "last4Ssn=${last4Ssn}, " +
                "phone=${phone}, " +
                "dateOfBirth=${dateOfBirth}, " +
                "gender=${gender}, " +
                "streetAddress=${streetAddress}, " +
                "addressLine2=${addressLine2}, " +
                "city=${city}, " +
                "state=${state}, " +
                "zipCode=${zipCode}"
    }

    companion object {
        fun initialize(): DemographicsInput {
            return DemographicsInput(
                firstName = FormField.with(Validators.Name),
                lastName = FormField.with(Validators.Name),
                email = FormField.with(Validators.Email),
                last4Ssn = FormField.with(Validators.Ssn),
                phone = FormField.with(Validators.Phone),
                dateOfBirth = FormField.with(Validators.DateOfBirth),
                gender = FormField.with(Validators.Gender),
                streetAddress = FormField.with(Validators.Address),
                addressLine2 = FormField.with(Validators.None),
                city = FormField.with(Validators.None),
                state = FormField.with(Validators.None),
                zipCode = FormField.with(Validators.ZipCode),
            )
        }
    }
}


fun DemographicsInput.mapToDemographics() = PatientDemographics(
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


