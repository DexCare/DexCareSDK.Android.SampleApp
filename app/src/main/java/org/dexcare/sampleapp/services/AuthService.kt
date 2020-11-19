package org.dexcare.sampleapp.services

interface AuthService {
    fun getCurrentToken(): String
    fun hasToken(): Boolean
    fun saveToken(token: String)
    fun clearToken()
}
