package org.dexcare.sampleapp.ui.common.viewmodel

import android.view.View

enum class VisibilityState(val viewState: Int) {
    Visible(View.VISIBLE),
    Invisible(View.INVISIBLE),
    Gone(View.GONE)
}