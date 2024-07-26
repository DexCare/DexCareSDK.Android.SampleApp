package com.dexcare.sample.auth

import com.dexcare.sample.data.ErrorResult
import com.dexcare.sample.data.model.AppEnvironment
import java.time.Instant

interface AuthProvider {
    suspend fun login(appEnvironment: AppEnvironment): LoginResult
}

sealed class LoginResult {
    data class Success(
        val accessToken: String,
        val refreshToken: String,
        val expiresAt: Instant
    ) : LoginResult()

    data class Error(val errorResult: ErrorResult) : LoginResult()
}
