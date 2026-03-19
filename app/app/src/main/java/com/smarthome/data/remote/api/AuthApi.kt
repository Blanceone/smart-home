package com.smarthome.data.remote.api

import com.smarthome.data.remote.dto.request.RefreshTokenRequest
import com.smarthome.data.remote.dto.request.WechatLoginRequest
import com.smarthome.data.remote.dto.response.ApiResponse
import com.smarthome.data.remote.dto.response.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("auth/wechat/login")
    suspend fun wechatLogin(
        @Body request: WechatLoginRequest
    ): ApiResponse<LoginResponse>

    @POST("auth/refresh")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): ApiResponse<LoginResponse>

    @POST("auth/logout")
    suspend fun logout(): ApiResponse<Unit>
}
