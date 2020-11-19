package org.dexcare.sampleapp.modules

import org.dexcare.sampleapp.services.AuthService
import org.dexcare.sampleapp.services.AuthServiceImpl
import org.dexcare.sampleapp.services.DemographicsService
import org.dexcare.sampleapp.services.DemographicsServiceImpl
import org.koin.dsl.module

val serviceModule = module {
    single<AuthService> {
        AuthServiceImpl()
    }

    single<DemographicsService> {
        DemographicsServiceImpl()
    }
}
