package org.dexcare.sampleapp

import android.app.Application
import com.stripe.android.PaymentConfiguration
import org.dexcare.DexCareSDK
import org.dexcare.Environment
import org.dexcare.sampleapp.modules.serviceModule
import org.dexcare.sampleapp.modules.storageModule
import org.dexcare.sampleapp.modules.utilModule
import org.dexcare.sampleapp.modules.viewModelModule
import org.dexcare.services.virtualvisit.VirtualEventListener
import org.dexcare.services.virtualvisit.VirtualService
import org.dexcare.services.virtualvisit.errors.DevicePairFailedReason
import org.dexcare.services.virtualvisit.errors.VirtualVisitError
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

        PaymentConfiguration.init(applicationContext, getString(R.string.stripe_publishable_key))
    }
}
