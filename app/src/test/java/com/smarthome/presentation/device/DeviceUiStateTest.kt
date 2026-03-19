package com.smarthome.presentation.device

import com.smarthome.data.remote.dto.DeviceDto
import com.smarthome.data.remote.dto.PurchaseUrlDto
import org.junit.Assert.*
import org.junit.Test

class DeviceUiStateTest {

    @Test
    fun `default state should have correct initial values`() {
        val state = DeviceUiState()

        assertFalse(state.isLoading)
        assertNull(state.device)
        assertNull(state.purchaseUrl)
        assertNull(state.error)
    }

    @Test
    fun `state should track loading status`() {
        val loadingState = DeviceUiState(isLoading = true)
        val notLoadingState = DeviceUiState(isLoading = false)

        assertTrue(loadingState.isLoading)
        assertFalse(notLoadingState.isLoading)
    }

    @Test
    fun `state should contain device data`() {
        val device = DeviceDto(
            id = "device-1",
            name = "智能门锁",
            brand = "小米",
            category = "安防",
            price = 699.0,
            originalPrice = 799.0,
            description = "智能门锁，支持指纹解锁",
            features = listOf("指纹解锁", "密码解锁", "远程开锁"),
            specifications = mapOf("供电" to "锂电池", "适用门厚" to "40-120mm"),
            applicableScenes = listOf("入户门", "防盗门"),
            imageUrl = "https://example.com/lock.jpg",
            images = listOf("https://example.com/lock1.jpg"),
            taobaoUrl = "https://item.taobao.com/123",
            priceUpdatedAt = "2026-03-17T10:00:00Z"
        )
        val state = DeviceUiState(device = device)

        assertNotNull(state.device)
        assertEquals("device-1", state.device?.id)
        assertEquals("智能门锁", state.device?.name)
        assertEquals(699.0, state.device?.price, 0.0)
    }

    @Test
    fun `state should contain purchase URL`() {
        val purchaseUrl = PurchaseUrlDto(
            purchaseUrl = "https://s.click.taobao.com/abc123",
            price = 699.0,
            originalPrice = 799.0,
            coupon = null
        )
        val state = DeviceUiState(purchaseUrl = purchaseUrl)

        assertNotNull(state.purchaseUrl)
        assertEquals("https://s.click.taobao.com/abc123", state.purchaseUrl?.purchaseUrl)
        assertEquals(699.0, state.purchaseUrl?.price, 0.0)
    }

    @Test
    fun `state should track error message`() {
        val errorState = DeviceUiState(error = "加载失败")

        assertEquals("加载失败", errorState.error)
    }

    @Test
    fun `state copy should update fields correctly`() {
        val original = DeviceUiState(isLoading = false)
        val updated = original.copy(isLoading = true, error = "发生错误")

        assertFalse(original.isLoading)
        assertTrue(updated.isLoading)
        assertEquals("发生错误", updated.error)
    }

    @Test
    fun `device with minimal fields should be handled correctly`() {
        val device = DeviceDto(
            id = "device-min",
            name = "智能插座",
            brand = null,
            category = "插座",
            price = 59.0,
            originalPrice = null,
            description = null,
            features = null,
            specifications = null,
            applicableScenes = null,
            imageUrl = null,
            images = null,
            taobaoUrl = null,
            priceUpdatedAt = null
        )
        val state = DeviceUiState(device = device)

        assertNotNull(state.device)
        assertNull(state.device.brand)
        assertNull(state.device.originalPrice)
        assertTrue(state.device.features.isNullOrEmpty())
    }

    @Test
    fun `loading state should clear previous errors`() {
        val state = DeviceUiState(isLoading = true)

        assertTrue(state.isLoading)
        assertNull(state.error)
    }
}
