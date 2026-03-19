package com.smarthome.data.remote.api

import com.smarthome.data.remote.dto.ApiResponse
import com.smarthome.data.remote.dto.ConfigDto
import retrofit2.http.GET

interface ConfigApi {
    @GET("config")
    suspend fun getConfig(): ApiResponse<ConfigDto>
}
