package com.smarthome.data.repository

import com.smarthome.data.remote.api.ConfigApi
import com.smarthome.data.remote.dto.ConfigDto
import com.smarthome.domain.repository.ConfigRepository
import javax.inject.Inject

class ConfigRepositoryImpl @Inject constructor(
    private val configApi: ConfigApi
) : ConfigRepository {

    override suspend fun getConfig(): Result<ConfigDto> {
        return try {
            val response = configApi.getConfig()
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
