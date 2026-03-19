package com.smarthome.data.remote.api

import com.smarthome.data.remote.dto.*
import retrofit2.http.Body
import retrofit2.http.POST

interface FeedbackApi {
    @POST("feedback/scheme-rating")
    suspend fun submitSchemeRating(@Body request: SchemeRatingRequest): ApiResponse<FeedbackResponse>

    @POST("feedback/suggestion")
    suspend fun submitSuggestion(@Body request: SuggestionRequest): ApiResponse<FeedbackResponse>

    @POST("feedback/data-correction")
    suspend fun submitDataCorrection(@Body request: DataCorrectionRequest): ApiResponse<FeedbackResponse>
}
