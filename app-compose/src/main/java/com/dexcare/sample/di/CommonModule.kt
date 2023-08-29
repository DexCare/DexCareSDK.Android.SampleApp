package com.dexcare.sample.di

import android.content.Context
import android.content.SharedPreferences
import com.dexcare.sample.auth.SessionManager
import com.dexcare.sample.common.DexCareConfigProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class CommonModule {

    @Provides
    fun provideSessionManager(sharedPreferences: SharedPreferences): SessionManager {
        return SessionManager(sharedPreferences)
    }

    @Provides
    fun provideConfig(@ApplicationContext context: Context): DexCareConfigProvider {
        return DexCareConfigProvider(context)
    }
}
