package com.smarthome.di

import android.content.Context
import com.smarthome.data.local.UserPreferences
import com.smarthome.data.remote.api.AuthApi
import com.smarthome.data.remote.api.ConfigApi
import com.smarthome.data.remote.api.DeviceApi
import com.smarthome.data.remote.api.FeedbackApi
import com.smarthome.data.remote.api.SchemeApi
import com.smarthome.data.remote.api.UserApi
import com.smarthome.data.remote.interceptor.AuthInterceptor
import com.smarthome.data.repository.AuthRepositoryImpl
import com.smarthome.data.repository.ConfigRepositoryImpl
import com.smarthome.data.repository.DeviceRepositoryImpl
import com.smarthome.data.repository.SchemeRepositoryImpl
import com.smarthome.data.repository.UserRepositoryImpl
import com.smarthome.domain.repository.AuthRepository
import com.smarthome.domain.repository.ConfigRepository
import com.smarthome.domain.repository.DeviceRepository
import com.smarthome.domain.repository.SchemeRepository
import com.smarthome.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://api.smarthome.com/v1/"

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        explicitNulls = false
    }

    @Provides
    @Singleton
    fun provideUserPreferences(
        @ApplicationContext context: Context
    ): UserPreferences = UserPreferences(context)

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        userPreferences: UserPreferences
    ): AuthInterceptor = AuthInterceptor(userPreferences)

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi = retrofit.create(UserApi::class.java)

    @Provides
    @Singleton
    fun provideSchemeApi(retrofit: Retrofit): SchemeApi = retrofit.create(SchemeApi::class.java)

    @Provides
    @Singleton
    fun provideDeviceApi(retrofit: Retrofit): DeviceApi = retrofit.create(DeviceApi::class.java)

    @Provides
    @Singleton
    fun provideFeedbackApi(retrofit: Retrofit): FeedbackApi = retrofit.create(FeedbackApi::class.java)

    @Provides
    @Singleton
    fun provideConfigApi(retrofit: Retrofit): ConfigApi = retrofit.create(ConfigApi::class.java)

    @Provides
    @Singleton
    fun provideConfigRepository(
        configApi: ConfigApi
    ): ConfigRepository = ConfigRepositoryImpl(configApi)

    @Provides
    @Singleton
    fun provideAuthRepository(
        authApi: AuthApi,
        userPreferences: UserPreferences,
        @ApplicationContext context: Context
    ): AuthRepository = AuthRepositoryImpl(authApi, userPreferences, context)

    @Provides
    @Singleton
    fun provideUserRepository(
        userApi: UserApi,
        feedbackApi: FeedbackApi
    ): UserRepository = UserRepositoryImpl(userApi, feedbackApi)

    @Provides
    @Singleton
    fun provideSchemeRepository(
        schemeApi: SchemeApi,
        userPreferences: UserPreferences,
        @ApplicationContext context: Context
    ): SchemeRepository = SchemeRepositoryImpl(schemeApi, userPreferences, context)

    @Provides
    @Singleton
    fun provideDeviceRepository(deviceApi: DeviceApi): DeviceRepository = DeviceRepositoryImpl(deviceApi)
}
