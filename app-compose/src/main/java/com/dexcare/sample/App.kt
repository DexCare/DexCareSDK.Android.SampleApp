package com.dexcare.sample

import android.app.Application
import android.content.Context
import com.dexcare.acme.android.R
import com.stripe.android.PaymentConfiguration
import dagger.hilt.android.HiltAndroidApp
import org.dexcare.DexCareSDK
import org.dexcare.Environment
import timber.log.Timber

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        DexCareSDK.init(this,
            object : Environment {
                override val isProd: Boolean = false
                override val fhirOrchUrl: String = getString(R.string.dexcare_fhirorch_url)
                override val virtualVisitUrl: String = getString(R.string.dexcare_virtualvisit_url)
            }
        )

        PaymentConfiguration.init(this,getString(R.string.stripe_publishable_key))

    }
}

/**
 * Utility function to check if required values are overridden in config.xml.
 * App can not function without these values.
 * */
fun areConfigValuesSetUp(context: Context): Boolean {
    return context.getString(R.string.stripe_publishable_key).isNotBlank() &&
            context.getString(R.string.auth0_client_id).isNotBlank() &&
            context.getString(R.string.auth0_domain).isNotBlank() &&
            context.getString(R.string.dexcare_domain).isNotBlank()
}
