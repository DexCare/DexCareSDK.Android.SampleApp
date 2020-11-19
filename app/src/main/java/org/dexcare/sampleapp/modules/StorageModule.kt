package org.dexcare.sampleapp.modules

import org.dexcare.sampleapp.ui.virtual.VirtualSchedulingFlow
import org.koin.dsl.module

val storageModule = module {
    single {
        VirtualSchedulingFlow()
    }
}
