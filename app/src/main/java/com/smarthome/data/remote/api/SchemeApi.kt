package com.smarthome.data.remote.api

import com.smarthome.data.remote.dto.*
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SchemeApi {
    @POST("schemes/generate")
    suspend fun generateScheme(@Body request: GenerateSchemeRequest): ApiResponse<SchemeDto>

    @GET("schemes/{schemeId}")
    suspend fun getSchemeDetail(@Path("schemeId") schemeId: String): ApiResponse<SchemeDto>

    @POST("schemes/{schemeId}/save")
    suspend fun saveScheme(@Path("schemeId") schemeId: String): ApiResponse<SaveSchemeResponse>

    @DELETE("schemes/{schemeId}")
    suspend fun deleteScheme(@Path("schemeId") schemeId: String): ApiResponse<Unit>

    @GET("schemes/{schemeId}/export")
    suspend fun exportScheme(@Path("schemeId") schemeId: String): ApiResponse<ExportDto>
}
