package com.dexcare.sample.presentation.demographics

import androidx.core.text.isDigitsOnly
import org.dexcare.util.EmailValidator
import org.dexcare.util.PhoneValidator
import org.dexcare.util.ZipCodeValidator
import timber.log.Timber
import java.time.LocalDate
import java.time.Period


interface InputValidator<T : Any> {

    fun validate(input: T?): String?
}

enum class ValidationError(val message: String) {
    Required("This is required"),
    InValid("Invalid input")
}

object Validators {

    /**
     * Validator for a form field that accepts anything.
     * i.e no validation needed for the field.
     * */
    object None : InputValidator<String> {
        override fun validate(input: String?): String? {
            return null
        }
    }

    private fun String.hasDigits(): Boolean {
        return any { it.isDigit() }
    }

    object Text : InputValidator<String> {
        override fun validate(input: String?): String? {
            Timber.d("validating input:$input")
            val error = if (input.isNullOrEmpty()) {
                ValidationError.Required
            } else if (input.hasDigits()) {
                ValidationError.InValid
            } else {
                null
            }
            return error?.message
        }
    }

    object Name : InputValidator<String> {
        override fun validate(input: String?): String? {
            Timber.d("validating name:$input")
            val error = if (input.isNullOrEmpty()) {
                ValidationError.Required
            } else if (input.hasDigits()) {
                ValidationError.InValid
            } else {
                null
            }
            return error?.message
        }
    }

    object DateOfBirth : InputValidator<LocalDate> {

        private const val MIN_AGE = 18

        override fun validate(input: LocalDate?): String? {
            Timber.d("validating dateOfBirth:$input")
            val now = LocalDate.now()
            val error = if (input == null) {
                ValidationError.Required
            } else if (input.isAfter(now) || Period.between(input, now).years < MIN_AGE) {
                ValidationError.InValid
            } else {
                null
            }
            return error?.message
        }
    }

    object Email : InputValidator<String> {
        override fun validate(input: String?): String? {
            Timber.d("validating email:$input")
            val error = if (input.isNullOrEmpty()) {
                ValidationError.Required
            } else if (!EmailValidator.isValid(input)) {
                ValidationError.InValid
            } else {
                null
            }
            return error?.message
        }
    }

    object Phone : InputValidator<String> {
        override fun validate(input: String?): String? {
            Timber.d("validating phone:$input")
            val error = if (input.isNullOrEmpty()) {
                ValidationError.Required
            } else if (PhoneValidator.isValid(input)) {
                null
            } else {
                ValidationError.InValid
            }
            return error?.message
        }
    }

    object Ssn : InputValidator<String> {
        override fun validate(input: String?): String? {
            Timber.d("validating ssn:$input")
            val error = if (input.isNullOrEmpty()) {
                ValidationError.Required
            } else if (input.isDigitsOnly()) {
                null
            } else {
                ValidationError.InValid
            }
            return error?.message
        }
    }

    object Address : InputValidator<String> {
        override fun validate(input: String?): String? {
            Timber.d("validating address:$input")
            val error = if (input.isNullOrEmpty()) {
                ValidationError.Required
            } else if (input.length > 45) {
                ValidationError.InValid
            } else {
                null
            }
            return error?.message
        }
    }

    object ZipCode : InputValidator<String> {
        override fun validate(input: String?): String? {
            Timber.d("validating zipcode:$input")
            val error = if (input.isNullOrEmpty()) {
                ValidationError.Required
            } else if (!ZipCodeValidator.isValid(input)) {
                ValidationError.InValid
            } else {
                null
            }
            return error?.message
        }
    }

    object Gender : InputValidator<org.dexcare.services.patient.models.Gender> {
        override fun validate(input: org.dexcare.services.patient.models.Gender?): String? {
            Timber.d("validating gender:$input")
            val error = if (input == null ||
                input == org.dexcare.services.patient.models.Gender.Unknown
            ) {
                ValidationError.Required
            } else {
                null
            }
            return error?.message
        }
    }
}
