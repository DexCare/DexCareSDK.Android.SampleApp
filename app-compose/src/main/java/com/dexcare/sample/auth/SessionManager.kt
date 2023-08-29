package com.dexcare.sample.auth

import android.content.SharedPreferences
import androidx.core.content.edit
import java.time.Instant
import javax.inject.Inject

class SessionManager @Inject constructor(private val sharedPreferences: SharedPreferences) {

    private var session: Session? = null

    fun save(session: Session) {
        this.session = session
        sharedPreferences.edit {
            putString(KEY_ACCESS_TOKEN, session.accessToken)
            putString(KEY_REFRESH_TOKEN, session.refreshToken)
            putLong(KEY_EXPIRY, session.expiresAt.toEpochMilli())
        }
    }

    private fun getSession() {
        val accessToken = sharedPreferences.getString(KEY_ACCESS_TOKEN, "").orEmpty()
        val refreshToken = sharedPreferences.getString(KEY_REFRESH_TOKEN, "").orEmpty()
        val expiry = sharedPreferences.getLong(KEY_EXPIRY, Instant.now().toEpochMilli())
        session = Session(
            accessToken,
            refreshToken,
            Instant.ofEpochMilli(expiry)
        )
    }

    fun clear() {
        session = null
    }

    fun isLoggedIn(): Boolean {
        getSession()
        return session != null && session!!.expiresAt.isAfter(Instant.now())
    }

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_EXPIRY = "expires_at"
    }
}
