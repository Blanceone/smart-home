package com.smarthome.data.repository

import com.smarthome.data.local.UserPreferences
import com.smarthome.data.remote.api.AuthApi
import com.smarthome.data.remote.dto.request.RefreshTokenRequest
import com.smarthome.data.remote.dto.request.WechatLoginRequest
import com.smarthome.data.remote.dto.response.LoginResponse
import com.smarthome.domain.repository.AuthRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val userPreferences: UserPreferences
) : AuthRepository {

    override suspend fun wechatLogin(
        code: String,
        nickname: String?,
        avatar: String?
    ): Result<LoginResponse> {
        return try {
            val request = WechatLoginRequest(
                code = code,
                userInfo = WechatLoginRequest.UserInfo(nickname, avatar)
            )
            val response = authApi.wechatLogin(request)
            if (response.code == 0 && response.data != null) {
                userPreferences.saveTokens(
                    response.data.accessToken,
                    response.data.refreshToken,
                    response.data.user.id
                )
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun refreshToken(refreshToken: String): Result<LoginResponse> {
        return try {
            val response = authApi.refreshToken(RefreshTokenRequest(refreshToken))
            if (response.code == 0 && response.data != null) {
                val userId = userPreferences.userId.first()
                userPreferences.saveTokens(
                    response.data.accessToken,
                    response.data.refreshToken,
                    userId ?: ""
                )
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            authApi.logout()
            userPreferences.clearTokens()
            Result.success(Unit)
        } catch (e: Exception) {
            userPreferences.clearTokens()
            Result.failure(e)
        }
    }

    override suspend fun isLoggedIn(): Boolean {
        return userPreferences.accessToken.first() != null
    }

    override suspend fun getAccessToken(): String? {
        return userPreferences.accessToken.first()
    }
}
