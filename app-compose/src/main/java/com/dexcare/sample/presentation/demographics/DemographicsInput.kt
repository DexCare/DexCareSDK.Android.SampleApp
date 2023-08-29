package com.dexcare.sample.presentation.demographics

data class DemographicsInput(
    val firstName: FormField<String>,
    val lastName: FormField<String>,
    val email: FormField<String>,
    val phone: FormField<String>,
    val last4Ssn: FormField<String>,
    val gender: FormField<String>,
    val dateOfBirth: FormField<String>,
    val streetAddress: FormField<String>,
    val addressLine2: FormField<String>,
    val city: FormField<String>,
    val state: FormField<String>,
    val zipCode: FormField<String>,
) {

    fun isValid(): Boolean {
        return firstName.error.isNullOrEmpty() &&
                lastName.error.isNullOrEmpty()
    }

    fun validate(): DemographicsInput {
        return DemographicsInput(
            firstName = this.firstName.validate(),
            lastName = this.lastName.validate(),
            email = this.email.validate(),
            phone = this.email.validate(),
            last4Ssn = this.email.validate(),
            gender = this.email.validate(),
            dateOfBirth = this.email.validate(),
            streetAddress = this.email.validate(),
            addressLine2 = this.email.validate(),
            city = this.email.validate(),
            state = this.email.validate(),
            zipCode = this.email.validate(),
        )
    }

    fun withFirstName(firstName: String): DemographicsInput {
        return copy(firstName = FormField(firstName, null, Validators.NameValidator))
    }

    fun withLastName(lastName: String): DemographicsInput {
        return copy(lastName = FormField(lastName, null, Validators.NameValidator))
    }

    companion object {
        fun initialize(): DemographicsInput {
            return DemographicsInput(
                firstName = FormField.with(Validators.NameValidator),
                lastName = FormField.with(Validators.NameValidator),
                email = FormField.with(Validators.EmailValidator),
                last4Ssn = FormField.with(Validators.None),
                phone = FormField.with(Validators.None),
                dateOfBirth = FormField.with(Validators.None),
                gender = FormField.with(Validators.None),
                streetAddress = FormField.with(Validators.None),
                addressLine2 = FormField.with(Validators.None),
                city = FormField.with(Validators.None),
                state = FormField.with(Validators.None),
                zipCode = FormField.with(Validators.None),
            )
        }
    }
}





