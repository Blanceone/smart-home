package com.smarthome.domain.repository

import com.smarthome.data.remote.dto.*

interface UserRepository {
    suspend fun getCurrentUser(): Result<UserDto>
    suspend fun updateCurrentUser(nickname: String?, avatar: String?): Result<UserDto>
    suspend fun saveUserInfo(request: SaveUserInfoRequest): Result<UserInfoDto>
    suspend fun getUserInfo(): Result<UserInfoDto>
    suspend fun saveHouseLayout(request: SaveHouseLayoutRequest): Result<HouseLayoutDto>
    suspend fun getHouseLayout(): Result<HouseLayoutDto>
    suspend fun getUserSchemes(): Result<SchemeListDto>
    suspend fun submitFeedback(type: String, content: String, contact: String?): Result<FeedbackResponse>
    suspend fun submitSchemeRating(schemeId: String, rating: Int, content: String?): Result<FeedbackResponse>
}
