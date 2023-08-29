package com.dexcare.sample.common

import android.content.Context
import org.dexcare.sampleapp.android.R
import javax.inject.Inject

class DexCareConfigProvider @Inject constructor(private val context: Context) {

    fun getNationalProviderId() = context.getString(R.string.hardcoded_national_provider_id)
}
