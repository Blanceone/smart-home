package com.smarthome.data.remote.api

import com.smarthome.data.remote.dto.request.GenerateSchemeRequest
import com.smarthome.data.remote.dto.response.ApiResponse
import com.smarthome.data.remote.dto.response.SchemeResponse
import com.smarthome.data.remote.dto.response.ShareResponse
import com.smarthome.data.remote.dto.response.ExportResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SchemeApi {

    @POST("schemes/generate")
    suspend fun generateScheme(
        @Body request: GenerateSchemeRequest
    ): ApiResponse<SchemeResponse>

    @GET("schemes/{schemeId}")
    suspend fun getSchemeDetail(
        @Path("schemeId") schemeId: String
    ): ApiResponse<SchemeResponse>

    @POST("schemes/{schemeId}/save")
    suspend fun saveScheme(
        @Path("schemeId") schemeId: String
    ): ApiResponse<Map<String, Any>>

    @DELETE("schemes/{schemeId}")
    suspend fun deleteScheme(
        @Path("schemeId") schemeId: String
    ): ApiResponse<Unit>

    @POST("schemes/{schemeId}/share")
    suspend fun shareScheme(
        @Path("schemeId") schemeId: String
    ): ApiResponse<ShareResponse>

    @GET("schemes/{schemeId}/export")
    suspend fun exportScheme(
        @Path("schemeId") schemeId: String
    ): ApiResponse<ExportResponse>
}
