package com.smarthome.domain.repository

import com.smarthome.data.remote.dto.LoginResponse
import com.smarthome.data.remote.dto.RegisterResponse

interface AuthRepository {
    suspend fun register(deviceId: String, nickname: String?, avatar: String?): Result<RegisterResponse>
    suspend fun wechatLogin(code: String, nickname: String?, avatar: String?): Result<LoginResponse>
    suspend fun refreshToken(refreshToken: String): Result<LoginResponse>
    suspend fun logout(): Result<Unit>
    fun getDeviceId(): String
    fun setDeviceId(deviceId: String)
}
