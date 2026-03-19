package com.smarthome.data.remote.api

import com.smarthome.data.remote.dto.*
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("users/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<RegisterResponse>
}
