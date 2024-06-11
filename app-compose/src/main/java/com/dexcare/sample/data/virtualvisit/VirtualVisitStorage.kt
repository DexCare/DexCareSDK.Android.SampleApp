package com.dexcare.sample.data.virtualvisit

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject

class VirtualVisitStorage @Inject constructor(private val sharedPreferences: SharedPreferences) {

    fun saveVisit(id: String) {
        sharedPreferences.edit {
            putString(KEY_VISIT_ID, id)
        }
    }

    fun getVisitId(): String? = sharedPreferences.getString(KEY_VISIT_ID, null)

    private companion object {
        const val KEY_VISIT_ID = "virtual_visit_id"
    }

}
