package com.smarthome.presentation.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smarthome.data.remote.dto.*
import com.smarthome.data.remote.dto.BasicInfoRequest
import com.smarthome.data.remote.dto.LifestyleRequest
import com.smarthome.data.remote.dto.DeviceExperienceRequest
import com.smarthome.data.remote.dto.AestheticPreferenceRequest
import com.smarthome.data.remote.dto.BrandPreferenceRequest
import com.smarthome.data.remote.dto.SaveHouseLayoutRequest
import com.smarthome.data.remote.dto.RoomRequest
import com.smarthome.domain.repository.UserRepository
import com.smarthome.domain.repository.SchemeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InfoCollectionUiState(
    val currentStep: Int = 1,
    val totalSteps: Int = 7,
    val isLoading: Boolean = false,
    val isCompleted: Boolean = false,
    val error: String? = null,
    
    val basicInfo: BasicInfoData? = null,
    val lifestyle: LifestyleData? = null,
    val deviceExperience: DeviceExperienceData? = null,
    val aestheticPreference: AestheticPreferenceData? = null,
    val brandPreference: BrandPreferenceData? = null,
    val houseLayout: HouseLayoutData? = null,
    val budget: Double? = null
)

data class BasicInfoData(
    val age: String? = null,
    val occupation: String? = null,
    val familyMembers: Set<String> = emptySet(),
    val city: String = ""
)

data class LifestyleData(
    val sleepPattern: String? = null,
    val homeActivities: Set<String> = emptySet(),
    val entertainmentHabits: Set<String> = emptySet()
)

data class DeviceExperienceData(
    val knowledgeLevel: String? = null,
    val usedDevices: Set<String> = emptySet()
)

data class AestheticPreferenceData(
    val decorStyle: String? = null,
    val colorPreferences: Set<String> = emptySet()
)

data class BrandPreferenceData(
    val preferredBrands: Set<String> = emptySet()
)

data class HouseLayoutData(
    val houseType: String? = null,
    val totalArea: Double? = null,
    val rooms: List<RoomData> = emptyList()
)

data class RoomData(
    val id: String = "",
    val name: String = "",
    val area: Double = 0.0,
    val specialNeeds: String? = null
)

@HiltViewModel
class InfoViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val schemeRepository: SchemeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InfoCollectionUiState())
    val uiState: StateFlow<InfoCollectionUiState> = _uiState.asStateFlow()

    init {
        loadSavedInfo()
    }

    private fun loadSavedInfo() {
        viewModelScope.launch {
            userRepository.getUserInfo()
                .onSuccess { info ->
                    _uiState.value = _uiState.value.copy(
                        basicInfo = info.basicInfo?.let {
                            BasicInfoData(
                                age = it.age,
                                occupation = it.occupation,
                                familyMembers = it.familyMembers?.toSet() ?: emptySet(),
                                city = it.city ?: ""
                            )
                        },
                        lifestyle = info.lifestyle?.let {
                            LifestyleData(
                                sleepPattern = it.sleepPattern,
                                homeActivities = it.homeActivities?.toSet() ?: emptySet(),
                                entertainmentHabits = it.entertainmentHabits?.toSet() ?: emptySet()
                            )
                        },
                        deviceExperience = info.deviceExperience?.let {
                            DeviceExperienceData(
                                knowledgeLevel = it.knowledgeLevel,
                                usedDevices = it.usedDevices?.toSet() ?: emptySet()
                            )
                        },
                        aestheticPreference = info.aestheticPreference?.let {
                            AestheticPreferenceData(
                                decorStyle = it.decorStyle,
                                colorPreferences = it.colorPreferences?.toSet() ?: emptySet()
                            )
                        },
                        brandPreference = info.brandPreference?.let {
                            BrandPreferenceData(
                                preferredBrands = it.preferredBrands?.toSet() ?: emptySet()
                            )
                        },
                        isCompleted = info.isCompleted
                    )
                }
            
            userRepository.getHouseLayout()
                .onSuccess { layout ->
                    _uiState.value = _uiState.value.copy(
                        houseLayout = HouseLayoutData(
                            houseType = layout.houseType,
                            totalArea = layout.totalArea,
                            rooms = layout.rooms?.map { room ->
                                RoomData(
                                    id = room.id ?: "",
                                    name = room.name,
                                    area = room.area,
                                    specialNeeds = room.specialNeeds
                                )
                            } ?: emptyList()
                        )
                    )
                }
        }
    }

    fun setBasicInfo(data: BasicInfoData) {
        _uiState.value = _uiState.value.copy(
            basicInfo = data,
            currentStep = 2
        )
    }

    fun setLifestyle(data: LifestyleData) {
        _uiState.value = _uiState.value.copy(
            lifestyle = data,
            currentStep = 3
        )
    }

    fun setDeviceExperience(data: DeviceExperienceData) {
        _uiState.value = _uiState.value.copy(
            deviceExperience = data,
            currentStep = 4
        )
    }

    fun setAestheticPreference(data: AestheticPreferenceData) {
        _uiState.value = _uiState.value.copy(
            aestheticPreference = data,
            currentStep = 5
        )
    }

    fun setBrandPreference(data: BrandPreferenceData) {
        _uiState.value = _uiState.value.copy(
            brandPreference = data,
            currentStep = 6
        )
    }

    fun setHouseLayout(data: HouseLayoutData) {
        _uiState.value = _uiState.value.copy(
            houseLayout = data,
            currentStep = 7
        )
    }

    fun setBudget(budget: Double) {
        _uiState.value = _uiState.value.copy(budget = budget)
    }

    fun goToStep(step: Int) {
        _uiState.value = _uiState.value.copy(currentStep = step)
    }

    fun nextStep() {
        val currentStep = _uiState.value.currentStep
        if (currentStep < _uiState.value.totalSteps) {
            _uiState.value = _uiState.value.copy(currentStep = currentStep + 1)
        }
    }

    fun previousStep() {
        val currentStep = _uiState.value.currentStep
        if (currentStep > 1) {
            _uiState.value = _uiState.value.copy(currentStep = currentStep - 1)
        }
    }

    fun saveAllAndGenerate() {
        val state = _uiState.value
        
        if (state.basicInfo == null || state.budget == null) {
            _uiState.value = state.copy(error = "请完成必填信息")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null)

            val request = SaveUserInfoRequest(
                basicInfo = BasicInfoRequest(
                    age = state.basicInfo.age ?: "",
                    occupation = state.basicInfo.occupation ?: "",
                    familyMembers = state.basicInfo.familyMembers.toList(),
                    city = state.basicInfo.city
                ),
                lifestyle = state.lifestyle?.let {
                    LifestyleRequest(
                        sleepPattern = it.sleepPattern,
                        homeActivities = it.homeActivities.toList(),
                        entertainmentHabits = it.entertainmentHabits.toList()
                    )
                },
                deviceExperience = state.deviceExperience?.let {
                    DeviceExperienceRequest(
                        knowledgeLevel = it.knowledgeLevel,
                        usedDevices = it.usedDevices.toList()
                    )
                },
                aestheticPreference = state.aestheticPreference?.let {
                    AestheticPreferenceRequest(
                        decorStyle = it.decorStyle,
                        colorPreferences = it.colorPreferences.toList()
                    )
                },
                brandPreference = state.brandPreference?.let {
                    BrandPreferenceRequest(
                        preferredBrands = it.preferredBrands.toList()
                    )
                }
            )

            userRepository.saveUserInfo(request)
                .onSuccess {
                    saveHouseLayoutAndGenerate(state)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message
                    )
                }
        }
    }

    private suspend fun saveHouseLayoutAndGenerate(state: InfoCollectionUiState) {
        val houseLayout = state.houseLayout
        if (houseLayout != null && houseLayout.houseType != null && houseLayout.totalArea != null) {
            val houseLayoutRequest = SaveHouseLayoutRequest(
                houseType = houseLayout.houseType,
                totalArea = houseLayout.totalArea,
                rooms = houseLayout.rooms.map { room ->
                    RoomRequest(
                        name = room.name,
                        area = room.area,
                        specialNeeds = room.specialNeeds
                    )
                }
            )

            userRepository.saveHouseLayout(houseLayoutRequest)
                .onSuccess {
                    generateScheme(state.budget!!)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message
                    )
                }
        } else {
            generateScheme(state.budget!!)
        }
    }

    private suspend fun generateScheme(budget: Double) {
        schemeRepository.generateScheme(budget, false)
            .onSuccess {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isCompleted = true
                )
            }
            .onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = exception.message
                )
            }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
