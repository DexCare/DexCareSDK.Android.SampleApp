package com.dexcare.sample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dexcare.sample.auth.AuthProvider
import com.dexcare.sample.auth.LoginResult
import com.dexcare.sample.auth.Session
import com.dexcare.sample.auth.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.dexcare.DexCareSDK
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val sessionManager: SessionManager) : ViewModel() {
    private val _state = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _state

    fun onResume(authProvider: AuthProvider) {
        if (!sessionManager.isLoggedIn()) {
            viewModelScope.launch {
                when (val result = authProvider.login()) {
                    is LoginResult.Success -> {
                        sessionManager.save(
                            Session(
                                accessToken = result.accessToken,
                                refreshToken = result.refreshToken,
                                expiresAt = result.expiresAt
                            )
                        )
                        signInToDexCare(result.accessToken)
                        _state.update { it.copy(loginComplete = true, errorMessage = null) }
                    }

                    is LoginResult.Error -> {
                        _state.update {
                            it.copy(
                                loginComplete = false,
                                errorMessage = result.message
                            )
                        }
                    }
                }
            }
        }
    }


    private fun signInToDexCare(token: String) {
        DexCareSDK.signIn(token)
    }

    data class UiState(val errorMessage: String? = null, val loginComplete: Boolean = false)
}
