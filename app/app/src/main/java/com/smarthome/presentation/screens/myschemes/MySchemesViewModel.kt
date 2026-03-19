package com.smarthome.presentation.screens.myschemes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smarthome.data.remote.dto.response.SchemeListResponse
import com.smarthome.domain.repository.SchemeRepository
import com.smarthome.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MySchemesUiState(
    val isLoading: Boolean = false,
    val schemes: List<SchemeListResponse.SchemeItem> = emptyList()
)

@HiltViewModel
class MySchemesViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val schemeRepository: SchemeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MySchemesUiState())
    val uiState: StateFlow<MySchemesUiState> = _uiState.asStateFlow()

    init {
        loadSchemes()
    }

    private fun loadSchemes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            userRepository.getUserSchemes().fold(
                onSuccess = { response ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        schemes = response.list
                    )
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            )
        }
    }

    fun deleteScheme(schemeId: String) {
        viewModelScope.launch {
            schemeRepository.deleteScheme(schemeId).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        schemes = _uiState.value.schemes.filter { it.id != schemeId }
                    )
                },
                onFailure = { }
            )
        }
    }
}
