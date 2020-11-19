package org.dexcare.sampleapp.modules

import android.content.Context
import org.dexcare.sampleapp.R
import org.dexcare.services.patient.models.Gender
import org.koin.core.qualifier.named
import org.koin.dsl.module

val GENDER_MAP = named("GENDER_MAP")

val utilModule = module {
    single(GENDER_MAP) {
        hashMapOf(
            Pair(
                get<Context>().getString(R.string.male),
                Gender.Male
            ),
            Pair(
                get<Context>().getString(R.string.female),
                Gender.Female
            ),
            Pair(
                get<Context>().getString(R.string.other),
                Gender.Other
            )
        )
    }
}
