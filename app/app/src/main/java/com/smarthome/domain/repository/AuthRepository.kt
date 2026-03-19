package com.smarthome.domain.repository

import com.smarthome.data.remote.dto.response.LoginResponse

interface AuthRepository {
    suspend fun wechatLogin(code: String, nickname: String?, avatar: String?): Result<LoginResponse>
    suspend fun refreshToken(refreshToken: String): Result<LoginResponse>
    suspend fun logout(): Result<Unit>
    suspend fun isLoggedIn(): Boolean
    suspend fun getAccessToken(): String?
}
