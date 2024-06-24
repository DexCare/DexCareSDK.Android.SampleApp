package com.dexcare.sample

import android.app.Application
import com.dexcare.sample.data.repository.EnvironmentsRepository
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    @Inject
    lateinit var environmentsRepository: EnvironmentsRepository

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        environmentsRepository.loadConfigFile()
    }
}
