package com.dexcare.sample.di

import com.dexcare.sample.auth.Auth0LoginProvider
import com.dexcare.sample.auth.AuthProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
abstract class AuthModule {

    @Binds
    abstract fun provideAuth(auth0LoginProvider: Auth0LoginProvider): AuthProvider
}
