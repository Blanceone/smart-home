package com.smarthome.presentation.scheme

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smarthome.data.remote.dto.SchemeDto
import com.smarthome.data.remote.dto.ExportDto
import com.smarthome.domain.repository.SchemeRepository
import com.smarthome.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SchemeUiState {
    data object Loading : SchemeUiState()
    data class Error(val message: String) : SchemeUiState()
    data class Success(
        val scheme: SchemeDto,
        val isSaved: Boolean = false,
        val exportInfo: ExportDto? = null
    ) : SchemeUiState()
}

@HiltViewModel
class SchemeViewModel @Inject constructor(
    private val schemeRepository: SchemeRepository,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val schemeId: String? = savedStateHandle.get<String>("schemeId")

    private val _uiState = MutableStateFlow<SchemeUiState>(SchemeUiState.Loading)
    val uiState: StateFlow<SchemeUiState> = _uiState.asStateFlow()

    init {
        schemeId?.let { loadSchemeDetail(it) }
    }

    fun generateScheme(budget: Double) {
        viewModelScope.launch {
            _uiState.value = SchemeUiState.Loading
            
            schemeRepository.generateScheme(budget, false)
                .onSuccess { scheme ->
                    _uiState.value = SchemeUiState.Success(scheme = scheme)
                }
                .onFailure { exception ->
                    _uiState.value = SchemeUiState.Error(exception.message ?: "生成方案失败")
                }
        }
    }

    fun loadSchemeDetail(id: String) {
        viewModelScope.launch {
            _uiState.value = SchemeUiState.Loading
            
            schemeRepository.getSchemeDetail(id)
                .onSuccess { scheme ->
                    _uiState.value = SchemeUiState.Success(scheme = scheme)
                }
                .onFailure { exception ->
                    _uiState.value = SchemeUiState.Error(exception.message ?: "加载方案失败")
                }
        }
    }

    fun saveScheme() {
        val currentState = _uiState.value
        if (currentState !is SchemeUiState.Success) return
        val schemeId = currentState.scheme.id
        
        viewModelScope.launch {
            schemeRepository.saveScheme(schemeId)
                .onSuccess {
                    _uiState.value = currentState.copy(isSaved = true)
                }
                .onFailure { exception ->
                    _uiState.value = SchemeUiState.Error(exception.message ?: "保存方案失败")
                }
        }
    }

    fun shareScheme() {
        val currentState = _uiState.value
        if (currentState !is SchemeUiState.Success) return
        val scheme = currentState.scheme
        
        val shareText = buildString {
            append("智能家居方案推荐\n")
            append("方案名称：${scheme.name}\n")
            append("预算：¥${scheme.budget.toInt()}\n")
            append("设备数量：${scheme.devices?.size ?: 0}件\n")
            scheme.decorationGuide?.summary?.let {
                append("\n${it}")
            }
            append("\n\n由智能家居方案定制APP生成")
        }
        
        _uiState.value = currentState.copy()
    }

    fun exportScheme() {
        val currentState = _uiState.value
        if (currentState !is SchemeUiState.Success) return
        val schemeId = currentState.scheme.id
        
        viewModelScope.launch {
            schemeRepository.exportScheme(schemeId)
                .onSuccess { exportInfo ->
                    _uiState.value = currentState.copy(exportInfo = exportInfo)
                }
                .onFailure { exception ->
                    _uiState.value = SchemeUiState.Error(exception.message ?: "导出失败")
                }
        }
    }

    fun clearError() {
        val currentState = _uiState.value
        if (currentState is SchemeUiState.Error) {
            _uiState.value = SchemeUiState.Loading
        }
    }

    fun clearExportInfo() {
        val currentState = _uiState.value
        if (currentState is SchemeUiState.Success) {
            _uiState.value = currentState.copy(exportInfo = null)
        }
    }

    fun submitRating(rating: Int, content: String?) {
        val currentState = _uiState.value
        if (currentState !is SchemeUiState.Success) return
        val schemeId = currentState.scheme.id
        
        viewModelScope.launch {
            userRepository.submitSchemeRating(schemeId, rating, content)
        }
    }
}
