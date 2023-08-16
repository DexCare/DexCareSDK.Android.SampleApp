package com.dexcare.sample.di

import com.dexcare.sample.auth.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class CommonModule {

    @Provides
    fun provideSessionManager(): SessionManager {
        return SessionManager()
    }
}
