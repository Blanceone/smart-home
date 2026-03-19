package com.smarthome.presentation.home

import com.smarthome.data.remote.dto.SchemeItemDto
import com.smarthome.data.remote.dto.UserDto
import org.junit.Assert.*
import org.junit.Test

class HomeUiStateTest {

    @Test
    fun `default state should have correct initial values`() {
        val state = HomeUiState()

        assertFalse(state.isLoading)
        assertNull(state.user)
        assertTrue(state.recentSchemes.isEmpty())
        assertNull(state.error)
    }

    @Test
    fun `state should track loading status`() {
        val loadingState = HomeUiState(isLoading = true)
        val notLoadingState = HomeUiState(isLoading = false)

        assertTrue(loadingState.isLoading)
        assertFalse(notLoadingState.isLoading)
    }

    @Test
    fun `state should contain user data`() {
        val user = UserDto(
            id = "user-123",
            deviceId = "device-abc",
            nickname = "测试用户",
            avatar = "https://example.com/avatar.jpg",
            createdAt = "2026-03-17T10:00:00Z"
        )
        val state = HomeUiState(user = user)

        assertNotNull(state.user)
        assertEquals("user-123", state.user?.id)
        assertEquals("测试用户", state.user?.nickname)
    }

    @Test
    fun `state should contain recent schemes`() {
        val schemes = listOf(
            SchemeItemDto(
                id = "scheme-1",
                name = "智能家居方案1",
                budget = 10000.0,
                totalPrice = 9500.0,
                deviceCount = 8,
                createdAt = "2026-03-15T10:00:00Z"
            ),
            SchemeItemDto(
                id = "scheme-2",
                name = "智能家居方案2",
                budget = 15000.0,
                totalPrice = 14000.0,
                deviceCount = 12,
                createdAt = "2026-03-14T10:00:00Z"
            )
        )
        val state = HomeUiState(recentSchemes = schemes)

        assertEquals(2, state.recentSchemes.size)
        assertEquals("智能家居方案1", state.recentSchemes[0].name)
    }

    @Test
    fun `state should track error message`() {
        val errorState = HomeUiState(error = "网络错误")

        assertEquals("网络错误", errorState.error)
    }

    @Test
    fun `state copy should update fields correctly`() {
        val original = HomeUiState(isLoading = false)
        val updated = original.copy(isLoading = true, error = "发生错误")

        assertFalse(original.isLoading)
        assertTrue(updated.isLoading)
        assertEquals("发生错误", updated.error)
    }

    @Test
    fun `empty schemes list should be handled correctly`() {
        val state = HomeUiState(recentSchemes = emptyList())

        assertTrue(state.recentSchemes.isEmpty())
        assertEquals(0, state.recentSchemes.size)
    }
}
