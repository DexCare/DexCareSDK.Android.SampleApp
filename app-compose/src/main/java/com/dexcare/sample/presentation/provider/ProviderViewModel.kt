package com.dexcare.sample.presentation.provider

import androidx.lifecycle.ViewModel
import com.dexcare.sample.common.DexCareConfigProvider
import com.dexcare.sample.data.ProviderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.dexcare.services.provider.models.Provider
import org.dexcare.services.provider.models.ProviderVisitType
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProviderViewModel @Inject constructor(
    private val providerRepository: ProviderRepository,
    private val config: DexCareConfigProvider
) :
    ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _state

    init {
        fetchProvider()
    }

    private fun fetchProvider() {
        _state.update { it.copy(inProgress = true) }
        providerRepository.getProvider(config.getNationalProviderId(), onResult = { result ->
            result.onSuccess { provider ->
                Timber.d("provider details:${provider}")
                _state.update {
                    it.copy(
                        errorMessage = null,
                        provider = provider,
                        inProgress = false
                    )
                }
            }
            result.onFailure { error ->
                Timber.e(error)
                _state.update { it.copy(errorMessage = error.message, inProgress = false) }
            }
        })
    }


    fun onVisitTypeSelected(visitType: ProviderVisitType) {
        providerRepository.onVisitTypeSelected(visitType)
    }

    private fun setProgress(isProgress: Boolean) {
        _state.update { it.copy(inProgress = isProgress) }
    }

    data class UiState(
        val errorMessage: String? = null,
        val provider: Provider? = null,
        val inProgress: Boolean = false
    )
}
