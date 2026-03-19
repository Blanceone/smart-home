package com.smarthome.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val code: Int,
    val message: String,
    val data: T?,
    val timestamp: Long
)

@Serializable
data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
    val user: User
) {
    @Serializable
    data class User(
        val id: String,
        val nickname: String?,
        val avatar: String?,
        val isNewUser: Boolean
    )
}

@Serializable
data class UserResponse(
    val id: String,
    val openid: String?,
    val nickname: String?,
    val avatar: String?,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class UserInfoResponse(
    val basicInfo: BasicInfo? = null,
    val lifestyle: Lifestyle? = null,
    val deviceExperience: DeviceExperience? = null,
    val aestheticPreference: AestheticPreference? = null,
    val brandPreference: BrandPreference? = null,
    val isCompleted: Boolean = false,
    val updatedAt: String? = null
) {
    @Serializable
    data class BasicInfo(
        val age: String?,
        val occupation: String?,
        val familyMembers: List<String>? = null,
        val city: String?
    )

    @Serializable
    data class Lifestyle(
        val sleepPattern: String?,
        val homeActivities: List<String>? = null,
        val entertainmentHabits: List<String>? = null
    )

    @Serializable
    data class DeviceExperience(
        val knowledgeLevel: String?,
        val usedDevices: List<String>? = null
    )

    @Serializable
    data class AestheticPreference(
        val decorStyle: String?,
        val colorPreferences: List<String>? = null
    )

    @Serializable
    data class BrandPreference(
        val preferredBrands: List<String>? = null
    )
}

@Serializable
data class HouseLayoutResponse(
    val id: String? = null,
    val houseType: String? = null,
    val totalArea: Double? = null,
    val rooms: List<Room>? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
) {
    @Serializable
    data class Room(
        val id: String?,
        val name: String,
        val area: Double,
        val specialNeeds: String? = null
    )
}

@Serializable
data class SchemeResponse(
    val id: String,
    val name: String,
    val budget: Double,
    val totalPrice: Double,
    val status: String,
    val decorationGuide: DecorationGuide? = null,
    val devices: List<Device>? = null,
    val createdAt: String
) {
    @Serializable
    data class DecorationGuide(
        val summary: String? = null,
        val rooms: List<Room>? = null,
        val professionalAdvice: String? = null
    ) {
        @Serializable
        data class Room(
            val name: String,
            val layout: String? = null,
            val devices: List<String>? = null,
            val installationPoints: List<String>? = null,
            val notes: String? = null
        )
    }

    @Serializable
    data class Device(
        val id: String,
        val name: String,
        val brand: String,
        val category: String,
        val price: Double,
        val quantity: Int,
        val description: String?,
        val recommendReason: String?,
        val imageUrl: String?,
        val taobaoUrl: String?
    )
}

@Serializable
data class SchemeListResponse(
    val list: List<SchemeItem>,
    val total: Int
) {
    @Serializable
    data class SchemeItem(
        val id: String,
        val name: String,
        val budget: Double,
        val totalPrice: Double,
        val deviceCount: Int,
        val createdAt: String
    )
}

@Serializable
data class DeviceResponse(
    val id: String,
    val name: String,
    val brand: String,
    val category: String,
    val price: Double,
    val originalPrice: Double?,
    val description: String?,
    val features: List<String>?,
    val specifications: Map<String, String>?,
    val applicableScenes: List<String>?,
    val imageUrl: String?,
    val images: List<String>?,
    val taobaoUrl: String?,
    val priceUpdatedAt: String?
)

@Serializable
data class DeviceListResponse(
    val list: List<DeviceItem>,
    val pagination: Pagination
) {
    @Serializable
    data class DeviceItem(
        val id: String,
        val name: String,
        val brand: String,
        val category: String,
        val price: Double,
        val imageUrl: String?
    )

    @Serializable
    data class Pagination(
        val page: Int,
        val pageSize: Int,
        val total: Int,
        val totalPages: Int
    )
}

@Serializable
data class PurchaseUrlResponse(
    val purchaseUrl: String,
    val price: Double,
    val coupon: Coupon?,
    val expiresAt: String
) {
    @Serializable
    data class Coupon(
        val value: Double,
        val description: String
    )
}

@Serializable
data class ShareResponse(
    val shareImageUrl: String,
    val shareUrl: String,
    val expiresAt: String
)

@Serializable
data class ExportResponse(
    val pdfUrl: String,
    val expiresAt: String
)
