package com.smarthome.data.repository

import com.smarthome.data.remote.api.UserApi
import com.smarthome.data.remote.api.FeedbackApi
import com.smarthome.data.remote.dto.*
import com.smarthome.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi,
    private val feedbackApi: FeedbackApi
) : UserRepository {

    override suspend fun getCurrentUser(): Result<UserDto> {
        return try {
            val response = userApi.getCurrentUser()
            if (response.code == 0 && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateCurrentUser(
        nickname: String?,
        avatar: String?
    ): Result<UserDto> {
        return try {
            val response = userApi.updateCurrentUser(UpdateUserRequest(nickname, avatar))
            if (response.code == 0 && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveUserInfo(request: SaveUserInfoRequest): Result<UserInfoDto> {
        return try {
            val response = userApi.saveUserInfo(request)
            if (response.code == 0 && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserInfo(): Result<UserInfoDto> {
        return try {
            val response = userApi.getUserInfo()
            if (response.code == 0 && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveHouseLayout(request: SaveHouseLayoutRequest): Result<HouseLayoutDto> {
        return try {
            val response = userApi.saveHouseLayout(request)
            if (response.code == 0 && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getHouseLayout(): Result<HouseLayoutDto> {
        return try {
            val response = userApi.getHouseLayout()
            if (response.code == 0 && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserSchemes(): Result<SchemeListDto> {
        return try {
            val response = userApi.getUserSchemes()
            if (response.code == 0 && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun submitFeedback(
        type: String,
        content: String,
        contact: String?
    ): Result<FeedbackResponse> {
        return try {
            val request = SuggestionRequest(type = type, content = content, contact = contact)
            val response = feedbackApi.submitSuggestion(request)
            if (response.code == 0 && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun submitSchemeRating(
        schemeId: String,
        rating: Int,
        content: String?
    ): Result<FeedbackResponse> {
        return try {
            val request = SchemeRatingRequest(schemeId = schemeId, rating = rating, content = content)
            val response = feedbackApi.submitSchemeRating(request)
            if (response.code == 0 && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
