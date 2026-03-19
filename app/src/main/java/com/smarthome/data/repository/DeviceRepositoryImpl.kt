package com.smarthome.data.repository

import com.smarthome.data.remote.api.DeviceApi
import com.smarthome.data.remote.dto.*
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
    ): Result<DeviceListDto> {
        return try {
            val response = deviceApi.searchDevices(
                keyword = keyword,
                category = category,
                brand = brand,
                minPrice = minPrice,
                maxPrice = maxPrice,
                page = page,
                pageSize = pageSize
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

    override suspend fun getDeviceDetail(deviceId: String): Result<DeviceDto> {
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

    override suspend fun getPurchaseUrl(deviceId: String): Result<PurchaseUrlDto> {
        return try {
            val response = deviceApi.getPurchaseUrl(deviceId)
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
