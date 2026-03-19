package com.smarthome.domain.repository

import com.smarthome.data.remote.dto.response.ExportResponse
import com.smarthome.data.remote.dto.response.SchemeResponse
import com.smarthome.data.remote.dto.response.ShareResponse

interface SchemeRepository {
    suspend fun generateScheme(budget: Double, regenerate: Boolean): Result<SchemeResponse>
    suspend fun getSchemeDetail(schemeId: String): Result<SchemeResponse>
    suspend fun saveScheme(schemeId: String): Result<Map<String, Any>>
    suspend fun deleteScheme(schemeId: String): Result<Unit>
    suspend fun shareScheme(schemeId: String): Result<ShareResponse>
    suspend fun exportScheme(schemeId: String): Result<ExportResponse>
}
