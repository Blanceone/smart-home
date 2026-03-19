package com.smarthome.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class WechatLoginRequest(
    val code: String,
    val userInfo: UserInfo? = null
) {
    @Serializable
    data class UserInfo(
        val nickname: String? = null,
        val avatar: String? = null
    )
}

@Serializable
data class RefreshTokenRequest(
    val refreshToken: String
)

@Serializable
data class UpdateUserRequest(
    val nickname: String? = null,
    val avatar: String? = null
)

@Serializable
data class SaveUserInfoRequest(
    val basicInfo: BasicInfo,
    val lifestyle: Lifestyle? = null,
    val deviceExperience: DeviceExperience? = null,
    val aestheticPreference: AestheticPreference? = null,
    val brandPreference: BrandPreference? = null
) {
    @Serializable
    data class BasicInfo(
        val age: String,
        val occupation: String,
        val familyMembers: List<String>,
        val city: String
    )

    @Serializable
    data class Lifestyle(
        val sleepPattern: String? = null,
        val homeActivities: List<String>? = null,
        val entertainmentHabits: List<String>? = null
    )

    @Serializable
    data class DeviceExperience(
        val knowledgeLevel: String? = null,
        val usedDevices: List<String>? = null
    )

    @Serializable
    data class AestheticPreference(
        val decorStyle: String? = null,
        val colorPreferences: List<String>? = null
    )

    @Serializable
    data class BrandPreference(
        val preferredBrands: List<String>? = null
    )
}

@Serializable
data class SaveHouseLayoutRequest(
    val houseType: String,
    val totalArea: Double,
    val rooms: List<Room>
) {
    @Serializable
    data class Room(
        val name: String,
        val area: Double,
        val specialNeeds: String? = null
    )
}

@Serializable
data class GenerateSchemeRequest(
    val budget: Double,
    val regenerate: Boolean = false
)
