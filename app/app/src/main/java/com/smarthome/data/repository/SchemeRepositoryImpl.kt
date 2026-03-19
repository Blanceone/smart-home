package com.smarthome.data.repository

import com.smarthome.data.remote.api.SchemeApi
import com.smarthome.data.remote.dto.request.GenerateSchemeRequest
import com.smarthome.data.remote.dto.response.ExportResponse
import com.smarthome.data.remote.dto.response.SchemeResponse
import com.smarthome.data.remote.dto.response.ShareResponse
import com.smarthome.domain.repository.SchemeRepository
import javax.inject.Inject

class SchemeRepositoryImpl @Inject constructor(
    private val schemeApi: SchemeApi
) : SchemeRepository {

    override suspend fun generateScheme(
        budget: Double,
        regenerate: Boolean
    ): Result<SchemeResponse> {
        return try {
            val response = schemeApi.generateScheme(
                GenerateSchemeRequest(budget, regenerate)
            )
            if (response.code == 0 && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSchemeDetail(schemeId: String): Result<SchemeResponse> {
        return try {
            val response = schemeApi.getSchemeDetail(schemeId)
            if (response.code == 0 && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveScheme(schemeId: String): Result<Map<String, Any>> {
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

    override suspend fun shareScheme(schemeId: String): Result<ShareResponse> {
        return try {
            val response = schemeApi.shareScheme(schemeId)
            if (response.code == 0 && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun exportScheme(schemeId: String): Result<ExportResponse> {
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
}
