package com.dexcare.sample.di

import android.content.Context
import android.content.SharedPreferences
import com.dexcare.sample.auth.SessionManager
import com.dexcare.sample.data.messaging.NotificationManager
import com.dexcare.sample.data.repository.EnvironmentsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CommonModule {

    @Provides
    fun provideSessionManager(sharedPreferences: SharedPreferences): SessionManager {
        return SessionManager(sharedPreferences)
    }

    @Singleton
    @Provides
    fun provideEnvironmentRepository(
        @ApplicationContext context: Context,
        json: Json,
        sharedPreferences: SharedPreferences
    ): EnvironmentsRepository =
        EnvironmentsRepository(context, json, sharedPreferences)

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    fun provideJsonParser(): Json {
        return Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
            isLenient = true
            allowSpecialFloatingPointValues = true
            coerceInputValues = true
            prettyPrint = true
            allowTrailingComma = true
        }
    }

    @Provides
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManager {
        return NotificationManager(context)
    }
}
