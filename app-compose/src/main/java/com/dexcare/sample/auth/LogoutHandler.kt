package com.dexcare.sample.auth

import com.dexcare.sample.data.repository.EnvironmentsRepository
import com.dexcare.sample.data.repository.VirtualVisitRepository
import javax.inject.Inject


class LogoutHandler @Inject constructor(
    private val sessionManager: SessionManager,
    private val environmentsRepository: EnvironmentsRepository,
    private val virtualVisitRepository: VirtualVisitRepository
) {
    fun logOut() {
        sessionManager.clear()
        environmentsRepository.clearEnvironment()
        virtualVisitRepository.clear()
    }
}
