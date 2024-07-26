package com.dexcare.sample.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dexcare.sample.auth.AuthProvider
import com.dexcare.sample.auth.LoginResult
import com.dexcare.sample.auth.LogoutHandler
import com.dexcare.sample.auth.Session
import com.dexcare.sample.auth.SessionManager
import com.dexcare.sample.data.EnvironmentsRepository
import com.dexcare.sample.data.ErrorResult
import com.dexcare.sample.data.ResultState
import com.dexcare.sample.data.model.AppEnvironment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.dexcare.DexCareSDK
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val environmentsRepository: EnvironmentsRepository,
    private val logoutHandler: LogoutHandler,
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
                        _state.update { it.copy(loginComplete = true, error = null) }
                    }

                    is LoginResult.Error -> {
                        _state.update {
                            it.copy(
                                error = result.errorResult,
                                loginComplete = false,
                            )
                        }
                    }
                }
            }
        } else {
            _state.update { it.copy(loginComplete = true) }
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
        Timber.d("savedEnvironment: $savedEnvironment")
        if (savedEnvironment == null) {
            if (allEnvironments.size == 1) {
                _state.update { it.copy(selectedEnvironment = allEnvironments.first()) }
                environmentsRepository.selectEnvironment(allEnvironments.first())
            }
        } else {
            allEnvironments.firstOrNull { it.configId == savedEnvironment.configId }
                ?.let { environment ->
                    _state.update { oldState ->
                        environmentsRepository.selectEnvironment(environment)
                        oldState.copy(selectedEnvironment = environment)
                    }
                } ?: run {
                Timber.e("Invalid App State")
            }
        }
    }

    private fun signInToDexCare(token: String) {
        DexCareSDK.signIn(token)
    }

    data class UiState(
        val selectedEnvironment: AppEnvironment? = null,
        val allEnvironments: List<AppEnvironment> = emptyList(),
        val loginComplete: Boolean = false,
        val error: ErrorResult? = null,
    )
}
