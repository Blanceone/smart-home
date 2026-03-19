package com.smarthome.data.remote.api

import com.smarthome.data.remote.dto.request.SaveHouseLayoutRequest
import com.smarthome.data.remote.dto.request.SaveUserInfoRequest
import com.smarthome.data.remote.dto.request.UpdateUserRequest
import com.smarthome.data.remote.dto.response.ApiResponse
import com.smarthome.data.remote.dto.response.HouseLayoutResponse
import com.smarthome.data.remote.dto.response.SchemeListResponse
import com.smarthome.data.remote.dto.response.UserInfoResponse
import com.smarthome.data.remote.dto.response.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface UserApi {

    @GET("users/me")
    suspend fun getCurrentUser(): ApiResponse<UserResponse>

    @PATCH("users/me")
    suspend fun updateCurrentUser(
        @Body request: UpdateUserRequest
    ): ApiResponse<UserResponse>

    @POST("users/me/info")
    suspend fun saveUserInfo(
        @Body request: SaveUserInfoRequest
    ): ApiResponse<UserInfoResponse>

    @GET("users/me/info")
    suspend fun getUserInfo(): ApiResponse<UserInfoResponse>

    @POST("users/me/house-layout")
    suspend fun saveHouseLayout(
        @Body request: SaveHouseLayoutRequest
    ): ApiResponse<HouseLayoutResponse>

    @GET("users/me/house-layout")
    suspend fun getHouseLayout(): ApiResponse<HouseLayoutResponse>

    @GET("users/me/schemes")
    suspend fun getUserSchemes(): ApiResponse<SchemeListResponse>
}
