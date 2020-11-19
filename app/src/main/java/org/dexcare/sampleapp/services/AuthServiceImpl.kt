package org.dexcare.sampleapp.services

class AuthServiceImpl : AuthService {
    var token: String = ""

    override fun getCurrentToken(): String {
        return token
    }

    override fun hasToken(): Boolean {
        return token.isNotEmpty()
    }

    override fun saveToken(token: String) {
        this.token = token
    }

    override fun clearToken() {
        token = ""
    }
}
