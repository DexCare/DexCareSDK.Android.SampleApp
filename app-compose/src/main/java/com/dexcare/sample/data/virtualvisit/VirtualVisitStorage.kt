package com.dexcare.sample.data.virtualvisit

import android.content.SharedPreferences
import androidx.core.content.edit
import org.dexcare.services.virtualvisit.models.VirtualPracticeRegion
import javax.inject.Inject

class VirtualVisitStorage @Inject constructor(private val sharedPreferences: SharedPreferences) {

    fun saveVisit(id: String) {
        sharedPreferences.edit {
            putString(KEY_VISIT_ID, id)
        }
    }

    fun saveRegion(region: VirtualPracticeRegion) {
        sharedPreferences.edit {
            putString(KEY_VISIT_REGION, region.practiceRegionId)
        }
    }

    fun getRegionId(): String {
        return sharedPreferences.getString(KEY_VISIT_REGION, "").orEmpty()
    }

    fun getVisitId(): String? = sharedPreferences.getString(KEY_VISIT_ID, null)

    fun clearData() {
        sharedPreferences.edit {
            remove(KEY_VISIT_ID)
            remove(KEY_VISIT_REGION)
        }
    }

    private companion object {
        const val KEY_VISIT_ID = "virtual_visit_id"
        const val KEY_VISIT_REGION = "virtual_visit_practice_region"
    }

}
