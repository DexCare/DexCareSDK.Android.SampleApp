package org.dexcare.sampleapp.modules

import org.dexcare.sampleapp.ui.common.SchedulingInfo
import org.koin.dsl.module

val storageModule = module {
    single {
        SchedulingInfo()
    }
}
