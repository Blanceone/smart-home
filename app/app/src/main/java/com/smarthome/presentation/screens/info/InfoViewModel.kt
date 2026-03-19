package com.smarthome.presentation.screens.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smarthome.data.remote.dto.request.SaveHouseLayoutRequest
import com.smarthome.data.remote.dto.request.SaveUserInfoRequest
import com.smarthome.domain.repository.SchemeRepository
import com.smarthome.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InfoUiState(
    val currentStep: Int = 0,
    val totalSteps: Int = 7,
    val basicInfo: BasicInfoData = BasicInfoData(),
    val lifestyle: LifestyleData = LifestyleData(),
    val deviceExperience: DeviceExperienceData = DeviceExperienceData(),
    val aestheticPreference: AestheticPreferenceData = AestheticPreferenceData(),
    val brandPreference: BrandPreferenceData = BrandPreferenceData(),
    val houseLayout: HouseLayoutData = HouseLayoutData(),
    val budget: Double? = null,
    val isGenerating: Boolean = false,
    val generatedSchemeId: String? = null
)

@HiltViewModel
class InfoViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val schemeRepository: SchemeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InfoUiState())
    val uiState: StateFlow<InfoUiState> = _uiState.asStateFlow()

    fun updateBasicInfo(data: BasicInfoData) {
        _uiState.value = _uiState.value.copy(basicInfo = data)
    }

    fun updateLifestyle(data: LifestyleData) {
        _uiState.value = _uiState.value.copy(lifestyle = data)
    }

    fun updateDeviceExperience(data: DeviceExperienceData) {
        _uiState.value = _uiState.value.copy(deviceExperience = data)
    }

    fun updateAestheticPreference(data: AestheticPreferenceData) {
        _uiState.value = _uiState.value.copy(aestheticPreference = data)
    }

    fun updateBrandPreference(data: BrandPreferenceData) {
        _uiState.value = _uiState.value.copy(brandPreference = data)
    }

    fun updateHouseLayout(data: HouseLayoutData) {
        _uiState.value = _uiState.value.copy(houseLayout = data)
    }

    fun updateBudget(budget: Double) {
        _uiState.value = _uiState.value.copy(budget = budget)
    }

    fun nextStep() {
        val current = _uiState.value.currentStep
        if (current < _uiState.value.totalSteps - 1) {
            _uiState.value = _uiState.value.copy(currentStep = current + 1)
        }
    }

    fun previousStep() {
        val current = _uiState.value.currentStep
        if (current > 0) {
            _uiState.value = _uiState.value.copy(currentStep = current - 1)
        }
    }

    fun generateScheme() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isGenerating = true)

            saveUserInfo()
            saveHouseLayout()

            val budget = _uiState.value.budget ?: return@launch

            schemeRepository.generateScheme(budget).fold(
                onSuccess = { scheme ->
                    _uiState.value = _uiState.value.copy(
                        isGenerating = false,
                        generatedSchemeId = scheme.id
                    )
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(isGenerating = false)
                }
            )
        }
    }

    private suspend fun saveUserInfo() {
        val state = _uiState.value
        val request = SaveUserInfoRequest(
            basicInfo = SaveUserInfoRequest.BasicInfo(
                age = state.basicInfo.age,
                occupation = state.basicInfo.occupation,
                familyMembers = listOf("独居"),
                city = state.basicInfo.city
            ),
            lifestyle = SaveUserInfoRequest.Lifestyle(
                sleepPattern = state.lifestyle.sleepPattern
            ),
            deviceExperience = SaveUserInfoRequest.DeviceExperience(
                knowledgeLevel = state.deviceExperience.knowledgeLevel
            ),
            aestheticPreference = SaveUserInfoRequest.AestheticPreference(
                decorStyle = state.aestheticPreference.decorStyle
            ),
            brandPreference = SaveUserInfoRequest.BrandPreference(
                preferredBrands = state.brandPreference.preferredBrands
            )
        )
        userRepository.saveUserInfo(request)
    }

    private suspend fun saveHouseLayout() {
        val state = _uiState.value
        val request = SaveHouseLayoutRequest(
            houseType = state.houseLayout.houseType,
            totalArea = state.houseLayout.totalArea ?: 0.0,
            rooms = listOf(
                SaveHouseLayoutRequest.Room("客厅", 30.0)
            )
        )
        userRepository.saveHouseLayout(request)
    }
}
