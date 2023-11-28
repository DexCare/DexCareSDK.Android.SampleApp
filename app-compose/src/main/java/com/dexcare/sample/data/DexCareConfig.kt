package com.dexcare.sample.data

import android.content.Context
import org.dexcare.sampleapp.android.R

interface DexCareConfig {

    fun brandName(): String

    fun tenant(): String

    fun getNationalProviderId(): String

    fun virtualPracticeId(): String
}

class DexCareConfigImpl constructor(private val context: Context) :
    DexCareConfig {

    override fun brandName(): String {
        return context.getString(R.string.brand)
    }

    override fun tenant(): String {
        return context.getString(R.string.tenant)
    }

    override fun getNationalProviderId(): String {
        return context.getString(R.string.hardcoded_national_provider_id)
    }

    override fun virtualPracticeId(): String {
        return context.getString(R.string.virtual_practice_id)
    }
}
