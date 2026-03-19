package com.smarthome.data.repository

import android.content.Context
import com.google.gson.Gson
import com.smarthome.data.local.UserPreferences
import com.smarthome.data.remote.api.SchemeApi
import com.smarthome.data.remote.dto.*
import com.smarthome.domain.repository.SchemeRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SchemeRepositoryImpl @Inject constructor(
    private val schemeApi: SchemeApi,
    private val userPreferences: UserPreferences,
    private val context: Context
) : SchemeRepository {

    private val gson = com.google.gson.Gson()

    override suspend fun generateScheme(budget: Double, regenerate: Boolean): Result<SchemeDto> {
        return try {
            val response = schemeApi.generateScheme(GenerateSchemeRequest(budget, regenerate))
            if (response.code == 0 && response.data != null) {
                val json = gson.toJson(response.data)
                userPreferences.cacheSchemes(json)
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSchemeDetail(schemeId: String): Result<SchemeDto> {
        return try {
            val response = schemeApi.getSchemeDetail(schemeId)
            if (response.code == 0 && response.data != null) {
                val json = gson.toJson(response.data)
                userPreferences.cacheSchemes(json)
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            val cached = getCachedSchemes()
            if (cached != null) {
                Result.success(cached)
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun saveScheme(schemeId: String): Result<SaveSchemeResponse> {
        return try {
            val response = schemeApi.saveScheme(schemeId)
            if (response.code == 0 && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteScheme(schemeId: String): Result<Unit> {
        return try {
            val response = schemeApi.deleteScheme(schemeId)
            if (response.code == 0) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun exportScheme(schemeId: String): Result<ExportDto> {
        return try {
            val response = schemeApi.exportScheme(schemeId)
            if (response.code == 0 && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getCachedSchemes(): SchemeDto? {
        return try {
            val (data, timestamp) = userPreferences.getCachedSchemes().first()
            if (data != null && userPreferences.isCacheValid(timestamp)) {
                gson.fromJson(data, SchemeDto::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
