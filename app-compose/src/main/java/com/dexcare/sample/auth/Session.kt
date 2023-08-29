package com.dexcare.sample.auth

import java.time.Instant

data class Session(
    val accessToken: String,
    val refreshToken: String,
    val expiresAt: Instant
)
