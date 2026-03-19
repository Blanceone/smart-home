package com.smarthome.domain.repository

import com.smarthome.data.remote.dto.ConfigDto

interface ConfigRepository {
    suspend fun getConfig(): Result<ConfigDto>
}
