package com.smarthome.data.repository

import android.content.Context
import com.smarthome.data.local.UserPreferences
import com.smarthome.data.remote.api.AuthApi
import com.smarthome.data.remote.dto.*
import com.smarthome.domain.repository.AuthRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import android.provider.Settings

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val userPreferences: UserPreferences,
    @ApplicationContext private val context: Context
) : AuthRepository {

    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    override suspend fun register(
        deviceId: String,
        nickname: String?,
        avatar: String?
    ): Result<RegisterResponse> {
        return try {
            val response = authApi.register(RegisterRequest(deviceId, nickname, avatar))
            if (response.code == 0 && response.data != null) {
                userPreferences.setCachedDeviceId(deviceId)
                setDeviceId(deviceId)
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun wechatLogin(
        code: String,
        nickname: String?,
        avatar: String?
    ): Result<LoginResponse> {
        return try {
            val userInfo = if (nickname != null || avatar != null) {
                WechatLoginRequest.UserInfo(nickname, avatar)
            } else null
            val response = authApi.wechatLogin(WechatLoginRequest(code, userInfo))
            if (response.code == 0 && response.data != null) {
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
            val response = authApi.logout()
            if (response.code == 0) {
                prefs.edit().clear().apply()
                userPreferences.setCachedDeviceId(null)
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getDeviceId(): String {
        var deviceId = prefs.getString("device_id", null)
        if (deviceId.isNullOrEmpty()) {
            deviceId = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            )
            setDeviceId(deviceId)
        }
        userPreferences.setCachedDeviceId(deviceId)
        return deviceId
    }

    override fun setDeviceId(deviceId: String) {
        prefs.edit().putString("device_id", deviceId).apply()
        userPreferences.setCachedDeviceId(deviceId)
    }
}
