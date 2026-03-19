package com.smarthome.data.remote.api

import com.smarthome.data.remote.dto.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface UserApi {
    @GET("users/me")
    suspend fun getCurrentUser(): ApiResponse<UserDto>

    @PATCH("users/me")
    suspend fun updateCurrentUser(@Body request: UpdateUserRequest): ApiResponse<UserDto>

    @POST("users/me/info")
    suspend fun saveUserInfo(@Body request: SaveUserInfoRequest): ApiResponse<UserInfoDto>

    @GET("users/me/info")
    suspend fun getUserInfo(): ApiResponse<UserInfoDto>

    @POST("users/me/house-layout")
    suspend fun saveHouseLayout(@Body request: SaveHouseLayoutRequest): ApiResponse<HouseLayoutDto>

    @GET("users/me/house-layout")
    suspend fun getHouseLayout(): ApiResponse<HouseLayoutDto>

    @GET("users/me/schemes")
    suspend fun getUserSchemes(): ApiResponse<SchemeListDto>
}
