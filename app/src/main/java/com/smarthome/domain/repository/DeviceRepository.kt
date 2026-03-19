package com.smarthome.domain.repository

import com.smarthome.data.remote.dto.*

interface DeviceRepository {
    suspend fun searchDevices(
        keyword: String?,
        category: String?,
        brand: String?,
        minPrice: Double?,
        maxPrice: Double?,
        page: Int,
        pageSize: Int
    ): Result<DeviceListDto>

    suspend fun getDeviceDetail(deviceId: String): Result<DeviceDto>

    suspend fun getPurchaseUrl(deviceId: String): Result<PurchaseUrlDto>
}
