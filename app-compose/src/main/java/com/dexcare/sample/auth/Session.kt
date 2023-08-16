package com.dexcare.sample.auth

import java.time.LocalDateTime

data class Session(
    val accessToken: String,
    val refreshToken: String,
    val expiresAt: LocalDateTime
)
