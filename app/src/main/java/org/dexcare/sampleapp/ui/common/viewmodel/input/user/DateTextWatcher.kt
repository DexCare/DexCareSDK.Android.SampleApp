package org.dexcare.sampleapp.ui.common.viewmodel.input.user

import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateTextWatcher(private val dateFormatPattern: String) : TextWatcher {

    private var updatedText: String = ""
    private var editing: Boolean = false
    private var cursorPosition: Int = 0
    private var extraOffset: Int = 0

    override fun beforeTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {}

    override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {

        if (text.toString() == updatedText || editing) return

        // removing characters
        if (updatedText.length > text.length) {
            updatedText = text.toString()
            cursorPosition = start
            return
        }

        val validDate = "01-01-1988"
        val currentText = addLeadingZerosAndDashes(text.toString())

        // predicted date for validating next entered character, 1988 - was a leap year, so entering 02-29 would be correct input
        val predictedDate = currentText + validDate.substring(currentText.length)

        // editing characters in the middle
        if (!"\\d\\d-\\d\\d-\\d\\d\\d\\d".toRegex().matches(predictedDate)) {
            updatedText = currentText
            cursorPosition = start + 1
            return
        }

        val dateFormat = SimpleDateFormat(dateFormatPattern, Locale.US)
        dateFormat.isLenient = false

        try {
            dateFormat.parse(predictedDate)
            updatedText = currentText
            cursorPosition = start + extraOffset + 1
        } catch (e: ParseException) {
            // nothing entered, do not move cursor
            cursorPosition = start
        }
    }

    override fun afterTextChanged(editable: Editable) {
        if (editing) return

        editing = true

        editable.clear()
        editable.insert(0, updatedText)

        Selection.setSelection(editable, cursorPosition)

        editing = false
    }

    private fun addLeadingZerosAndDashes(input: String): String {

        val tmpInput: String =  when {
            // one digit month entered: adding leading zero and dash
            "^[2-9]$".toRegex().matches(input) -> {
                "0$input-"
            }
            // two digits month entered: adding dash
            "^[0-1][1-9]$".toRegex().matches(input) -> {
                "$input-"
            }
            // single digit day entered: adding leading zero and dash
            "^[0-1][1-9]-[4-9]$".toRegex().matches(input) -> {
                insertLeadingZeroForDay(input) + "-"
            }
            // single digit day and dash entered: adding leading zero
            "^[0-1][1-9]-[1-3]-$".toRegex().matches(input) -> {
                insertLeadingZeroForDay(input)
            }
            // two digits day entered: adding dash
            "^[0-1][1-9]-([1-3][0-9]|[0-3][1-9])$".toRegex().matches(input) -> {
                "$input-"
            }
            else -> {
                input
            }
        }

        extraOffset = tmpInput.length - input.length

        return tmpInput
    }

    private fun insertLeadingZeroForDay(input: String): String {
        return input.substring(0, input.indexOf("-") + 1) + "0" + input.substring(input.indexOf("-") + 1)
    }
}
