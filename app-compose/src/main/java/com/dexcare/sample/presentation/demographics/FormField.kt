package com.dexcare.sample.presentation.demographics

import androidx.compose.runtime.Stable

@Stable
data class FormField<T : Any>(
    val input: T?,
    val error: String? = null,
    val validator: InputValidator<T>
) {
    fun validate(): FormField<T> {
        val error = validator.validate(input)
        return this.copy(error = error)
    }

    fun resetWith(input: T?): FormField<T> {
        return this.copy(input = input, error = null)
    }

    override fun toString(): String {
        return input.toString()
    }

    companion object {
        fun <T : Any> with(value: T, validator: InputValidator<T>): FormField<T> {
            return FormField(
                input = value,
                validator = validator
            )
        }

        fun <T : Any> with(validator: InputValidator<T>): FormField<T> {
            return FormField(
                input = null,
                validator = validator
            )
        }
    }
}
