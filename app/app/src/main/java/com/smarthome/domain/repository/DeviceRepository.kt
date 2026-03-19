package com.smarthome.domain.repository

import com.smarthome.data.remote.dto.response.DeviceListResponse
import com.smarthome.data.remote.dto.response.DeviceResponse
import com.smarthome.data.remote.dto.response.PurchaseUrlResponse

interface DeviceRepository {
    suspend fun searchDevices(
        keyword: String?,
        category: String?,
        brand: String?,
        minPrice: Double?,
        maxPrice: Double?,
        page: Int,
        pageSize: Int
    ): Result<DeviceListResponse>

    suspend fun getDeviceDetail(deviceId: String): Result<DeviceResponse>
    suspend fun getDevicePurchaseUrl(deviceId: String): Result<PurchaseUrlResponse>
}
