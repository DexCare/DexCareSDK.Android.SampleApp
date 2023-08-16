package com.dexcare.sample.auth

import java.time.LocalDateTime

class SessionManager {

    private var session: Session? = null

    fun save(session: Session) {
        this.session = session
    }

    fun clear() {
        session = null
    }

    fun isLoggedIn(): Boolean {
        return session != null && session!!.expiresAt.isAfter(LocalDateTime.now())
    }

}
