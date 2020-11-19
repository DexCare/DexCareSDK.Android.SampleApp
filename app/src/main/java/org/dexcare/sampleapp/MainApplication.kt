package org.dexcare.sampleapp

import android.app.Application
import org.dexcare.DexCareSDK
import org.dexcare.Environment
import org.dexcare.sampleapp.modules.serviceModule
import org.dexcare.sampleapp.modules.storageModule
import org.dexcare.sampleapp.modules.utilModule
import org.dexcare.sampleapp.modules.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        startKoin {
            androidContext(this@MainApplication)
            modules(
                listOf(
                    serviceModule,
                    storageModule,
                    utilModule,
                    viewModelModule
                )
            )
        }

        DexCareSDK.initWithKoinSupport(this,
            object : Environment {
                override val isProd: Boolean = false
                override val fhirOrchUrl: String = getString(R.string.dexcare_fhirorch_url)
                override val virtualVisitUrl: String = getString(R.string.dexcare_virtualvisit_url)
                override val pcpUrl: String = getString(R.string.dexcare_pcp_url)
            }
        )
    }
}
