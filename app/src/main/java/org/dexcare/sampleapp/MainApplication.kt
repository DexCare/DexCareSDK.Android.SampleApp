package org.dexcare.sampleapp

import android.app.Application
import android.content.Context
import com.stripe.android.PaymentConfiguration
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

        try {
            PaymentConfiguration.init(
                applicationContext,
                getString(R.string.stripe_publishable_key)
            )
        } catch (ex: IllegalArgumentException) {
            Timber.e(ex)
        }

    }
}


/**
 * Utility function to check if required values are overridden in config.xml.
 * App can not function without these values.
 * */
fun areConfigValuesSetUp(context: Context): Boolean {
    return context.getString(R.string.stripe_publishable_key).isNotBlank() &&
            context.getString(R.string.auth0_client_id).isNotBlank() &&
            context.getString(R.string.auth0_domain).isNotBlank()
}
