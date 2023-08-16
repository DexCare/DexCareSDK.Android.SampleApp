package com.dexcare.sample.auth

import java.time.LocalDateTime

interface AuthProvider {
    suspend fun login(): LoginResult
}

sealed class LoginResult {
    data class Success(
        val accessToken: String,
        val refreshToken: String,
        val expiresAt: LocalDateTime
    ) : LoginResult()

    data class Error(val message: String) : LoginResult()
}
