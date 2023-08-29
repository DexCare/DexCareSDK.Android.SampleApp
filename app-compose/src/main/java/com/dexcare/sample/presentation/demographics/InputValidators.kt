package com.dexcare.sample.presentation.demographics

import timber.log.Timber


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

    object NameValidator : InputValidator<String> {
        override fun validate(input: String?): String? {
            Timber.d("validating name:$input")
            val error = if (input.isNullOrEmpty()) {
                ValidationError.Required
            } else if (input.contains("0-9")) {
                ValidationError.InValid
            } else {
                null
            }
            return error?.message
        }
    }

    object EmailValidator : InputValidator<String> {
        override fun validate(input: String?): String? {
            Timber.d("validating email:$input")
            val error = if (input.isNullOrEmpty()) {
                ValidationError.Required
            } else {
                //todo use dexcare EmailValidator.isValid
                null
            }
            return error?.message
        }
    }
}
