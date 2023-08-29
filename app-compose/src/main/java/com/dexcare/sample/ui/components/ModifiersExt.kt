package com.dexcare.sample.ui.components

import androidx.compose.ui.Modifier


inline fun Modifier.applyWhen(
    condition: Boolean,
    then: Modifier.() -> Modifier
): Modifier =
    if (condition) {
        then()
    } else {
        this
    }
