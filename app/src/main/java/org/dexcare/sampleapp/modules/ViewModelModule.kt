package org.dexcare.sampleapp.modules

import org.dexcare.sampleapp.ui.common.viewmodel.input.address.AddressViewModel
import org.dexcare.sampleapp.ui.demographics.DemographicsFragmentViewModel
import org.koin.dsl.module


/**
 * Contains all the different view models that need to be injected
 */
val viewModelModule = module {

    factory {
        AddressViewModel()
    }
}
