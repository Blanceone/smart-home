package com.smarthome.data.remote.interceptor

import com.smarthome.data.local.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val userPreferences: UserPreferences
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            userPreferences.accessToken.first()
        }

        val request = chain.request().newBuilder()
            .apply {
                token?.let {
                    addHeader("Authorization", "Bearer $it")
                }
                addHeader("X-Platform", "android")
                addHeader("X-Version", "1.0.0")
            }
            .build()

        return chain.proceed(request)
    }
}
