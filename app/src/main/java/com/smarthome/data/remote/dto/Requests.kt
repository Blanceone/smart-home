package com.smarthome.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val deviceId: String,
    val nickname: String? = null,
    val avatar: String? = null
)

@Serializable
data class UpdateUserRequest(
    val nickname: String? = null,
    val avatar: String? = null
)

@Serializable
data class SaveUserInfoRequest(
    val basicInfo: BasicInfoRequest,
    val lifestyle: LifestyleRequest? = null,
    val deviceExperience: DeviceExperienceRequest? = null,
    val aestheticPreference: AestheticPreferenceRequest? = null,
    val brandPreference: BrandPreferenceRequest? = null
)

@Serializable
data class BasicInfoRequest(
    val age: String,
    val occupation: String,
    val familyMembers: List<String>,
    val city: String
)

@Serializable
data class LifestyleRequest(
    val sleepPattern: String? = null,
    val homeActivities: List<String>? = null,
    val entertainmentHabits: List<String>? = null
)

@Serializable
data class DeviceExperienceRequest(
    val knowledgeLevel: String? = null,
    val usedDevices: List<String>? = null
)

@Serializable
data class AestheticPreferenceRequest(
    val decorStyle: String? = null,
    val colorPreferences: List<String>? = null
)

@Serializable
data class BrandPreferenceRequest(
    val preferredBrands: List<String>? = null
)

@Serializable
data class SaveHouseLayoutRequest(
    val houseType: String,
    val totalArea: Double,
    val rooms: List<RoomRequest>
)

@Serializable
data class RoomRequest(
    val name: String,
    val area: Double,
    val specialNeeds: String? = null
)

@Serializable
data class GenerateSchemeRequest(
    val budget: Double,
    val regenerate: Boolean = false
)

@Serializable
data class SchemeRatingRequest(
    val schemeId: String,
    val rating: Int,
    val content: String? = null
)

@Serializable
data class SuggestionRequest(
    val type: String,
    val content: String,
    val contact: String? = null
)

@Serializable
data class DataCorrectionRequest(
    val deviceId: String,
    val errorType: String,
    val correctInfo: String
)
