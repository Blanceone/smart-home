package com.smarthome.domain.repository

import com.smarthome.data.remote.dto.request.SaveHouseLayoutRequest
import com.smarthome.data.remote.dto.request.SaveUserInfoRequest
import com.smarthome.data.remote.dto.response.HouseLayoutResponse
import com.smarthome.data.remote.dto.response.SchemeListResponse
import com.smarthome.data.remote.dto.response.UserInfoResponse
import com.smarthome.data.remote.dto.response.UserResponse

interface UserRepository {
    suspend fun getCurrentUser(): Result<UserResponse>
    suspend fun updateCurrentUser(nickname: String?, avatar: String?): Result<UserResponse>
    suspend fun saveUserInfo(request: SaveUserInfoRequest): Result<UserInfoResponse>
    suspend fun getUserInfo(): Result<UserInfoResponse>
    suspend fun saveHouseLayout(request: SaveHouseLayoutRequest): Result<HouseLayoutResponse>
    suspend fun getHouseLayout(): Result<HouseLayoutResponse>
    suspend fun getUserSchemes(): Result<SchemeListResponse>
}
