package com.smarthome.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smarthome.data.remote.dto.SchemeItemDto
import com.smarthome.data.remote.dto.UserDto
import com.smarthome.domain.repository.ConfigRepository
import com.smarthome.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,
    val user: UserDto? = null,
    val recentSchemes: List<SchemeItemDto> = emptyList(),
    val hasCompletedInfo: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val configRepository: ConfigRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            val userDeferred = async {
                userRepository.getCurrentUser()
            }

            val schemesDeferred = async {
                userRepository.getUserSchemes()
            }

            val userInfoDeferred = async {
                userRepository.getUserInfo()
            }

            userDeferred.await()
                .onSuccess { user ->
                    _uiState.value = _uiState.value.copy(user = user)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(error = exception.message)
                }

            schemesDeferred.await()
                .onSuccess { schemes ->
                    _uiState.value = _uiState.value.copy(
                        recentSchemes = schemes.list.take(3)
                    )
                }

            userInfoDeferred.await()
                .onSuccess { userInfo ->
                    _uiState.value = _uiState.value.copy(
                        hasCompletedInfo = userInfo.isCompleted
                    )
                }

            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
