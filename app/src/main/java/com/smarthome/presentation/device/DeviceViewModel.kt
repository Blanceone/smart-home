package com.smarthome.presentation.device

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smarthome.data.remote.dto.DeviceDto
import com.smarthome.data.remote.dto.PurchaseUrlDto
import com.smarthome.domain.repository.DeviceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DeviceUiState(
    val isLoading: Boolean = false,
    val device: DeviceDto? = null,
    val purchaseUrl: PurchaseUrlDto? = null,
    val error: String? = null
)

@HiltViewModel
class DeviceViewModel @Inject constructor(
    private val deviceRepository: DeviceRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val deviceId: String? = savedStateHandle.get<String>("deviceId")

    private val _uiState = MutableStateFlow(DeviceUiState())
    val uiState: StateFlow<DeviceUiState> = _uiState.asStateFlow()

    init {
        deviceId?.let { loadDeviceDetail(it) }
    }

    fun loadDeviceDetail(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            deviceRepository.getDeviceDetail(id)
                .onSuccess { device ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        device = device
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

    fun getPurchaseUrl() {
        val deviceId = _uiState.value.device?.id ?: return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            deviceRepository.getPurchaseUrl(deviceId)
                .onSuccess { purchaseUrl ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        purchaseUrl = purchaseUrl
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

    fun clearPurchaseUrl() {
        _uiState.value = _uiState.value.copy(purchaseUrl = null)
    }
}
