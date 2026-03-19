package com.smarthome.domain.repository

import com.smarthome.data.remote.dto.*

interface SchemeRepository {
    suspend fun generateScheme(budget: Double, regenerate: Boolean): Result<SchemeDto>
    suspend fun getSchemeDetail(schemeId: String): Result<SchemeDto>
    suspend fun saveScheme(schemeId: String): Result<SaveSchemeResponse>
    suspend fun deleteScheme(schemeId: String): Result<Unit>
    suspend fun exportScheme(schemeId: String): Result<ExportDto>
}
