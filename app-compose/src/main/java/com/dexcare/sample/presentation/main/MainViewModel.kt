package com.dexcare.sample.presentation.main

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dexcare.sample.auth.AuthProvider
import com.dexcare.sample.auth.LoginResult
import com.dexcare.sample.auth.LogoutHandler
import com.dexcare.sample.auth.Session
import com.dexcare.sample.auth.SessionManager
import com.dexcare.sample.common.toError
import com.dexcare.sample.data.ErrorResult
import com.dexcare.sample.data.ResultState
import com.dexcare.sample.data.model.AppEnvironment
import com.dexcare.sample.data.repository.EnvironmentsRepository
import com.dexcare.sample.data.repository.PatientRepository
import com.dexcare.sample.data.repository.VirtualVisitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.dexcare.DexCareSDK
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val environmentsRepository: EnvironmentsRepository,
    private val logoutHandler: LogoutHandler,
    private val virtualVisitRepository: VirtualVisitRepository,
    private val patientRepository: PatientRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _state
    private lateinit var authProvider: AuthProvider

    init {
        setUpAppEnvironment()
    }

    fun setAuthProvider(authProvider: AuthProvider) {
        this.authProvider = authProvider
    }

    fun reload() {
        _state.update { UiState() }
        setUpAppEnvironment()
    }

    fun selectEnvironment(appEnvironment: AppEnvironment) {
        logoutHandler.logOut()
        environmentsRepository.selectEnvironment(appEnvironment)
        _state.update { it.copy(selectedEnvironment = appEnvironment) }
        checkAuthAndNavigate()
    }

    fun checkAuthAndNavigate() {
        if (_state.value.selectedEnvironment != null && !sessionManager.isLoggedIn()) {
            viewModelScope.launch {
                when (val result = authProvider.login(_state.value.selectedEnvironment!!)) {
                    is LoginResult.Success -> {
                        sessionManager.save(
                            Session(
                                accessToken = result.accessToken,
                                refreshToken = result.refreshToken,
                                expiresAt = result.expiresAt
                            )
                        )
                        signInToDexCare(result.accessToken)
                        _state.update { it.copy(showMainContent = true, error = null) }
                    }

                    is LoginResult.Error -> {
                        _state.update {
                            it.copy(
                                error = result.errorResult,
                                showMainContent = false,
                            )
                        }
                    }
                }
            }
        } else {
            _state.update { it.copy(showMainContent = true) }
        }
    }

    fun onRejoinVisit(visitId: String) {
        _state.update { it.copy(visitId = visitId, rejoiningVisit = true) }
        viewModelScope.launch {
            val isActive = virtualVisitRepository.isVisitActive(visitId)
            _state.update { it.copy(visitActive = isActive) }
            if (isActive) {
                //update visitId so that it can be relaunched on next session
                virtualVisitRepository.updateVisitId(visitId)
            }
        }
    }

    fun onRejoinVisit(activity: FragmentActivity) {
        _state.update { it.copy(rejoiningVisit = true) }
        patientRepository.findPatient(onSuccess = { patient ->
            virtualVisitRepository.rejoinVisit(
                activity,
                patient,
                onComplete = { intent, error ->
                    _state.update {
                        it.copy(
                            rejoiningVisit = false,
                            visitIntent = intent,
                            error = error?.toError(title = "Error rejoining visit")
                        )
                    }
                })
        }, onError = {
            _state.update {
                it.copy(
                    rejoiningVisit = false,
                    error = ErrorResult(
                        "Error rejoining visit",
                        "We were unable to load the patient record."
                    )
                )
            }
        })
    }

    fun resetToMainContent() {
        _state.update {
            it.copy(
                rejoiningVisit = false,
                showMainContent = true,
                visitActive = null
            )
        }
    }

    private fun setUpAppEnvironment() {
        viewModelScope.launch {
            environmentsRepository.getAllEnvironments().collect { result ->
                when (result) {
                    is ResultState.Complete -> {
                        _state.update { it.copy(allEnvironments = result.data) }
                        findDefaultEnvironmentIfAvailable(result.data)
                    }

                    is ResultState.Error -> {
                        sessionManager.clear()
                        _state.update { it.copy(error = result.errorResult) }
                    }

                    else -> {}
                }
            }
        }
    }

    /*
    * Find if there is a pre-selected environment. If yes, refresh the config so that we have new values(if any).
    * If there is no pre-selected environment, user should pick one from the options.
    * If there is only one in the config, it's selected automatically.
    * */
    private fun findDefaultEnvironmentIfAvailable(allEnvironments: List<AppEnvironment>) {
        val savedEnvironment = environmentsRepository.findSelectedEnvironment()
        if (savedEnvironment == null) {
            if (allEnvironments.size == 1) {
                _state.update { it.copy(selectedEnvironment = allEnvironments.first()) }
                environmentsRepository.selectEnvironment(allEnvironments.first())
            }
        } else {

            _state.update { oldState ->
                oldState.copy(selectedEnvironment = savedEnvironment)
            }
        }
    }

    private fun signInToDexCare(token: String) {
        DexCareSDK.signIn(token)
    }

    data class UiState(
        val selectedEnvironment: AppEnvironment? = null,
        val allEnvironments: List<AppEnvironment> = emptyList(),
        val showMainContent: Boolean = false,
        val error: ErrorResult? = null,
        val rejoiningVisit: Boolean = false,
        val visitId: String? = null,
        val visitActive: Boolean? = null,
        val visitIntent: Intent? = null,
    )
}
