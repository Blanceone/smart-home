package com.smarthome.presentation.myschemes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smarthome.data.remote.dto.SchemeItemDto
import com.smarthome.data.remote.dto.ExportDto
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
    val schemes: List<SchemeItemDto> = emptyList(),
    val exportInfo: ExportDto? = null,
    val deletedSchemeId: String? = null,
    val error: String? = null
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

    fun loadSchemes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            userRepository.getUserSchemes()
                .onSuccess { result ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        schemes = result.list
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message
                    )
                }
        }
    }

    fun deleteScheme(schemeId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            schemeRepository.deleteScheme(schemeId)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        schemes = _uiState.value.schemes.filter { it.id != schemeId },
                        deletedSchemeId = schemeId
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message
                    )
                }
        }
    }

    fun exportScheme(schemeId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            schemeRepository.exportScheme(schemeId)
                .onSuccess { exportInfo ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        exportInfo = exportInfo
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearExportInfo() {
        _uiState.value = _uiState.value.copy(exportInfo = null)
    }

    fun clearDeletedSchemeId() {
        _uiState.value = _uiState.value.copy(deletedSchemeId = null)
    }
}
