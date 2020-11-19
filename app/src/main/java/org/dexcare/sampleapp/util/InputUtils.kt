package org.dexcare.sampleapp.util

import org.dexcare.util.EmailValidator

object InputUtils {
    private var SSN_LENGTH = 4
    private var MIN_PASSWORD_LENGTH = 8
    private var SPECIAL_CHARS = "!@#$%^&*()-_=+[{]}\\|:;,.?/~`"
    private val NAME_VALIDATION_REGEX = "^[\\p{L} '.-]+$".toRegex()

    fun hasLowerCaseChar(target: String): Boolean {
        return target.matches(".*[a-z].*".toRegex())
    }

    fun hasUpperCaseChar(target: String): Boolean {
        return target.matches(".*[A-Z].*".toRegex())
    }

    fun hasSpecialChar(target: String?): Boolean {
        if (target != null && !target.isEmpty()) {
            for (element in SPECIAL_CHARS) {
                if (target.indexOf(element) >= 0) {
                    return true
                }
            }
        }
        return false
    }

    fun isOnlyDigits(target: String?): Boolean {
        if (target.isNullOrEmpty()) {
            return false
        }
        for (element in target) {
            if (!Character.isDigit(element)) {
                return false
            }
        }
        return true
    }

    fun hasDigit(target: String): Boolean {
        return target.matches(".*\\d.*".toRegex())
    }

    fun isPasswordLongEnough(target: String): Boolean {
        return target.length >= MIN_PASSWORD_LENGTH
    }

    /**
     * Validate an email according to the DexCare SDK's email requirements
     *
     * @param emailAddress email to validate
     * @return true if the email is deemed valid by the SDK
     */
    fun isEmailValid(emailAddress: String?): Boolean {
        if (emailAddress.isNullOrEmpty()) {
            return false
        }
        return EmailValidator.isValid(emailAddress)
    }

    /**
     * Validates a name.  Currently only checks if the string is not null and not empty.
     *
     * @param name - name to validate
     * @return - false if the name is invalid, true otherwise.
     */
    fun isValidName(name: String?): Boolean {
        if (name.isNullOrEmpty()) return false
        return name.matches(NAME_VALIDATION_REGEX)
    }

    /**
     * Validates a first name.  Currently only checks if the string is not null and not empty.
     *
     * @param name - name to validate
     * @return - false if the name is invalid, true otherwise.
     */
    fun isValidFirstName(name: String?): Boolean {
        return isValidName(name)
    }

    /**
     * Validates a last name.  Currently only checks if the string is not null and not empty.
     *
     * @param name - name to validate
     * @return - false if the name is invalid, true otherwise
     */
    fun isValidLastName(name: String?): Boolean {
        return isValidName(name)
    }

    /**
     * Validates a 4 digit ssn.  Checks to make sure it's not null, empty, is 4 characters long
     * and all digits.
     *
     * @param number
     * @return
     */
    fun isValid4DigitSsn(number: String?): Boolean {
        return number?.length == SSN_LENGTH && isOnlyDigits(number)
    }

    /**
     * Checks if the DoB provided is valid.  Currently only checks that the passed string is not
     * null and not empty.
     *
     * @param dob
     * @return
     */
    fun isValidDateOfBirth(dob: String?): Boolean {
        return dob?.isNotEmpty() ?: false
    }
}