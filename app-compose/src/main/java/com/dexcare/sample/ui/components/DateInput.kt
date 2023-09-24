package com.dexcare.sample.ui.components

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext
import java.time.LocalDate

@Composable
fun DateInput(
    isEnabled: MutableState<Boolean>,
    defaultDate: LocalDate = LocalDate.now(),
    onDateSelected: (LocalDate) -> Unit
) {
    val context = LocalContext.current
    val dialog = DatePickerDialog(
        context, { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
            onDateSelected(LocalDate.of(selectedYear, selectedMonth, selectedDayOfMonth))
        }, defaultDate.year, defaultDate.monthValue, defaultDate.dayOfMonth
    )

    if (isEnabled.value) {
        dialog.show()
    }
}
