package com.smarthome.presentation.screens.scheme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smarthome.data.remote.dto.response.SchemeResponse
import com.smarthome.domain.repository.SchemeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SchemeUiState(
    val isLoading: Boolean = false,
    val scheme: SchemeResponse? = null,
    val error: String? = null,
    val isSaved: Boolean = false
)

@HiltViewModel
class SchemeViewModel @Inject constructor(
    private val schemeRepository: SchemeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SchemeUiState())
    val uiState: StateFlow<SchemeUiState> = _uiState.asStateFlow()

    fun loadScheme(schemeId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            schemeRepository.getSchemeDetail(schemeId).fold(
                onSuccess = { scheme ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        scheme = scheme
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            )
        }
    }

    fun saveScheme() {
        val schemeId = _uiState.value.scheme?.id ?: return

        viewModelScope.launch {
            schemeRepository.saveScheme(schemeId).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isSaved = true)
                },
                onFailure = { }
            )
        }
    }

    fun shareScheme() {
        val schemeId = _uiState.value.scheme?.id ?: return

        viewModelScope.launch {
            schemeRepository.shareScheme(schemeId)
        }
    }
}
