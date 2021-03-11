package org.dexcare.sampleapp.modules

import android.content.Context
import org.dexcare.sampleapp.R
import org.dexcare.services.models.RelationshipToPatient
import org.dexcare.services.patient.models.Gender
import org.koin.core.qualifier.named
import org.koin.dsl.module

val GENDER_MAP = named("GENDER_MAP")
val RELATIONSHIP_TO_PATIENT_LIST = named("RELATIONSHIP_TO_PATIENT_LIST")

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

    single(RELATIONSHIP_TO_PATIENT_LIST) {
        // This is a subset of the RelationshipToPatient enum values.
        // You can display all of them, or pick an choose which ones to display to the user.
        listOf(
            Pair(
                get<Context>().getString(R.string.mother),
                RelationshipToPatient.Mother
            ),
            Pair(
                get<Context>().getString(R.string.father),
                RelationshipToPatient.Father
            ),
            Pair(
                get<Context>().getString(R.string.significant_other),
                RelationshipToPatient.SignificantOther
            ),
            Pair(
                get<Context>().getString(R.string.legal_guardian),
                RelationshipToPatient.LegalGuardian
            ),
            Pair(
                get<Context>().getString(R.string.sister),
                RelationshipToPatient.Sister
            ),
            Pair(
                get<Context>().getString(R.string.brother),
                RelationshipToPatient.Brother
            )
        )
    }
}
