package com.smarthome.data.repository

import com.smarthome.data.remote.api.UserApi
import com.smarthome.data.remote.dto.request.SaveHouseLayoutRequest
import com.smarthome.data.remote.dto.request.SaveUserInfoRequest
import com.smarthome.data.remote.dto.request.UpdateUserRequest
import com.smarthome.data.remote.dto.response.HouseLayoutResponse
import com.smarthome.data.remote.dto.response.SchemeListResponse
import com.smarthome.data.remote.dto.response.UserInfoResponse
import com.smarthome.data.remote.dto.response.UserResponse
import com.smarthome.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi
) : UserRepository {

    override suspend fun getCurrentUser(): Result<UserResponse> {
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
    ): Result<UserResponse> {
        return try {
            val response = userApi.updateCurrentUser(
                UpdateUserRequest(nickname, avatar)
            )
            if (response.code == 0 && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveUserInfo(request: SaveUserInfoRequest): Result<UserInfoResponse> {
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

    override suspend fun getUserInfo(): Result<UserInfoResponse> {
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

    override suspend fun saveHouseLayout(request: SaveHouseLayoutRequest): Result<HouseLayoutResponse> {
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

    override suspend fun getHouseLayout(): Result<HouseLayoutResponse> {
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

    override suspend fun getUserSchemes(): Result<SchemeListResponse> {
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
}
