package com.dexcare.sample.auth

import com.dexcare.sample.data.EnvironmentsRepository
import javax.inject.Inject


class LogoutHandler @Inject constructor(
    private val sessionManager: SessionManager,
    private val environmentsRepository: EnvironmentsRepository
) {
    fun logOut() {
        sessionManager.clear()
        environmentsRepository.clearEnvironment()
    }
}
