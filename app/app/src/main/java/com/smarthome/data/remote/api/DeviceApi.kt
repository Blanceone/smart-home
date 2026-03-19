package com.smarthome.data.remote.api

import com.smarthome.data.remote.dto.response.ApiResponse
import com.smarthome.data.remote.dto.response.DeviceResponse
import com.smarthome.data.remote.dto.response.PurchaseUrlResponse
import com.smarthome.data.remote.dto.response.DeviceListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DeviceApi {

    @GET("devices")
    suspend fun searchDevices(
        @Query("keyword") keyword: String? = null,
        @Query("category") category: String? = null,
        @Query("brand") brand: String? = null,
        @Query("minPrice") minPrice: Double? = null,
        @Query("maxPrice") maxPrice: Double? = null,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): ApiResponse<DeviceListResponse>

    @GET("devices/{deviceId}")
    suspend fun getDeviceDetail(
        @Path("deviceId") deviceId: String
    ): ApiResponse<DeviceResponse>

    @GET("devices/{deviceId}/purchase-url")
    suspend fun getDevicePurchaseUrl(
        @Path("deviceId") deviceId: String
    ): ApiResponse<PurchaseUrlResponse>
}
