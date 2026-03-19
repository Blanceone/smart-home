package com.smarthome.di

import com.smarthome.data.local.UserPreferences
import com.smarthome.data.remote.api.AuthApi
import com.smarthome.data.remote.api.DeviceApi
import com.smarthome.data.remote.api.SchemeApi
import com.smarthome.data.remote.api.UserApi
import com.smarthome.data.repository.AuthRepositoryImpl
import com.smarthome.data.repository.DeviceRepositoryImpl
import com.smarthome.data.repository.SchemeRepositoryImpl
import com.smarthome.data.repository.UserRepositoryImpl
import com.smarthome.domain.repository.AuthRepository
import com.smarthome.domain.repository.DeviceRepository
import com.smarthome.domain.repository.SchemeRepository
import com.smarthome.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        authApi: AuthApi,
        userPreferences: UserPreferences
    ): AuthRepository {
        return AuthRepositoryImpl(authApi, userPreferences)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        userApi: UserApi
    ): UserRepository {
        return UserRepositoryImpl(userApi)
    }

    @Provides
    @Singleton
    fun provideSchemeRepository(
        schemeApi: SchemeApi
    ): SchemeRepository {
        return SchemeRepositoryImpl(schemeApi)
    }

    @Provides
    @Singleton
    fun provideDeviceRepository(
        deviceApi: DeviceApi
    ): DeviceRepository {
        return DeviceRepositoryImpl(deviceApi)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSchemeApi(retrofit: Retrofit): SchemeApi {
        return retrofit.create(SchemeApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDeviceApi(retrofit: Retrofit): DeviceApi {
        return retrofit.create(DeviceApi::class.java)
    }
}
