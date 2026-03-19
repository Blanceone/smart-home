package com.smarthome.data.repository

import com.smarthome.data.remote.api.DeviceApi
import com.smarthome.data.remote.dto.response.DeviceListResponse
import com.smarthome.data.remote.dto.response.DeviceResponse
import com.smarthome.data.remote.dto.response.PurchaseUrlResponse
import com.smarthome.domain.repository.DeviceRepository
import javax.inject.Inject

class DeviceRepositoryImpl @Inject constructor(
    private val deviceApi: DeviceApi
) : DeviceRepository {

    override suspend fun searchDevices(
        keyword: String?,
        category: String?,
        brand: String?,
        minPrice: Double?,
        maxPrice: Double?,
        page: Int,
        pageSize: Int
    ): Result<DeviceListResponse> {
        return try {
            val response = deviceApi.searchDevices(
                keyword, category, brand, minPrice, maxPrice, page, pageSize
            )
            if (response.code == 0 && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDeviceDetail(deviceId: String): Result<DeviceResponse> {
        return try {
            val response = deviceApi.getDeviceDetail(deviceId)
            if (response.code == 0 && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDevicePurchaseUrl(deviceId: String): Result<PurchaseUrlResponse> {
        return try {
            val response = deviceApi.getDevicePurchaseUrl(deviceId)
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
