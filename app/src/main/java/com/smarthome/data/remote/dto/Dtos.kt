package com.smarthome.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val code: Int,
    val message: String,
    val data: T?,
    val timestamp: Long
)

@Serializable
data class RegisterResponse(
    val id: String,
    val deviceId: String,
    val nickname: String,
    val avatar: String?,
    val isNewUser: Boolean,
    val createdAt: String
)

@Serializable
data class UserDto(
    val id: String,
    val deviceId: String,
    val nickname: String,
    val avatar: String?,
    val isNewUser: Boolean = false,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class UserInfoDto(
    val basicInfo: BasicInfoDto? = null,
    val lifestyle: LifestyleDto? = null,
    val deviceExperience: DeviceExperienceDto? = null,
    val aestheticPreference: AestheticPreferenceDto? = null,
    val brandPreference: BrandPreferenceDto? = null,
    val isCompleted: Boolean = false,
    val updatedAt: String? = null
)

@Serializable
data class BasicInfoDto(
    val age: String? = null,
    val occupation: String? = null,
    val familyMembers: List<String>? = null,
    val city: String? = null
)

@Serializable
data class LifestyleDto(
    val sleepPattern: String? = null,
    val homeActivities: List<String>? = null,
    val entertainmentHabits: List<String>? = null
)

@Serializable
data class DeviceExperienceDto(
    val knowledgeLevel: String? = null,
    val usedDevices: List<String>? = null
)

@Serializable
data class AestheticPreferenceDto(
    val decorStyle: String? = null,
    val colorPreferences: List<String>? = null
)

@Serializable
data class BrandPreferenceDto(
    val preferredBrands: List<String>? = null
)

@Serializable
data class HouseLayoutDto(
    val id: String? = null,
    val houseType: String? = null,
    val totalArea: Double? = null,
    val rooms: List<RoomDto>? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

@Serializable
data class RoomDto(
    val id: String? = null,
    val name: String,
    val area: Double,
    val specialNeeds: String? = null
)

@Serializable
data class SchemeDto(
    val id: String,
    val name: String,
    val budget: Double,
    val totalPrice: Double,
    val status: String,
    val decorationGuide: DecorationGuideDto? = null,
    val devices: List<SchemeDeviceDto>? = null,
    val createdAt: String
)

@Serializable
data class DecorationGuideDto(
    val summary: String? = null,
    val rooms: List<RoomGuideDto>? = null,
    val professionalAdvice: String? = null
)

@Serializable
data class RoomGuideDto(
    val name: String,
    val layout: String? = null,
    val devices: List<String>? = null,
    val installationPoints: List<String>? = null,
    val notes: String? = null
)

@Serializable
data class SchemeDeviceDto(
    val id: String,
    val name: String,
    val brand: String,
    val category: String,
    val price: Double,
    val quantity: Int,
    val description: String? = null,
    val recommendReason: String? = null,
    val imageUrl: String? = null,
    val taobaoUrl: String? = null
)

@Serializable
data class SchemeListDto(
    val list: List<SchemeItemDto>,
    val total: Int,
    val maxAllowed: Int = 3
)

@Serializable
data class SchemeItemDto(
    val id: String,
    val name: String,
    val budget: Double,
    val totalPrice: Double,
    val deviceCount: Int,
    val createdAt: String
)

@Serializable
data class DeviceDto(
    val id: String,
    val name: String,
    val brand: String,
    val category: String,
    val price: Double,
    val originalPrice: Double? = null,
    val description: String? = null,
    val features: List<String>? = null,
    val specifications: Map<String, String>? = null,
    val applicableScenes: List<String>? = null,
    val imageUrl: String? = null,
    val images: List<String>? = null,
    val taobaoUrl: String? = null,
    val priceUpdatedAt: String? = null
)

@Serializable
data class DeviceListDto(
    val list: List<DeviceItemDto>,
    val pagination: PaginationDto
)

@Serializable
data class DeviceItemDto(
    val id: String,
    val name: String,
    val brand: String,
    val category: String,
    val price: Double,
    val imageUrl: String? = null
)

@Serializable
data class PaginationDto(
    val page: Int,
    val pageSize: Int,
    val total: Int,
    val totalPages: Int
)

@Serializable
data class SaveSchemeResponse(
    val id: String,
    val savedAt: String
)

@Serializable
data class ExportDto(
    val pdfUrl: String,
    val expiresAt: String
)

@Serializable
data class FeedbackDto(
    val id: String,
    val type: String,
    val content: String,
    val createdAt: String
)

@Serializable
data class LoginResponse(
    val user: UserDto,
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val expiresIn: Long? = null
)

@Serializable
data class PurchaseUrlDto(
    val purchaseUrl: String,
    val price: Double,
    val coupon: CouponDto? = null,
    val expiresAt: String
)

@Serializable
data class CouponDto(
    val value: Double,
    val description: String
)

@Serializable
data class FeedbackResponse(
    val id: String,
    val createdAt: String
)

@Serializable
data class ConfigDto(
    val version: VersionDto? = null,
    val dictionaries: DictionariesDto? = null,
    val platforms: PlatformsDto? = null
)

@Serializable
data class VersionDto(
    val latestVersion: String? = null,
    val minVersion: String? = null,
    val updateUrl: String? = null,
    val forceUpdate: Boolean? = null
)

@Serializable
data class DictionariesDto(
    val ageRanges: List<String>? = null,
    val occupations: List<String>? = null,
    val familyMembers: List<String>? = null,
    val sleepPatterns: List<String>? = null,
    val homeActivities: List<String>? = null,
    val entertainmentHabits: List<String>? = null,
    val knowledgeLevels: List<String>? = null,
    val decorStyles: List<String>? = null,
    val colorPreferences: List<String>? = null,
    val brands: List<String>? = null,
    val houseTypes: List<String>? = null,
    val roomTypes: List<String>? = null,
    val deviceCategories: List<String>? = null
)

@Serializable
data class PlatformsDto(
    val android: PlatformInfoDto? = null,
    val ios: PlatformInfoDto? = null,
    val web: PlatformInfoDto? = null
)

@Serializable
data class PlatformInfoDto(
    val minVersion: String? = null,
    val targetVersion: String? = null,
    val supportedBrowsers: List<String>? = null
)

@Serializable
data class HealthDto(
    val status: String? = null,
    val services: Map<String, String>? = null,
    val timestamp: Long? = null
)
