package com.smarthome.presentation.profile

import com.smarthome.data.remote.dto.UserDto
import org.junit.Assert.*
import org.junit.Test

class ProfileUiStateTest {

    @Test
    fun `default state should have correct initial values`() {
        val state = ProfileUiState()

        assertFalse(state.isLoading)
        assertNull(state.user)
        assertEquals(0, state.schemeCount)
        assertFalse(state.isLoggedOut)
        assertNull(state.error)
    }

    @Test
    fun `state should track loading status`() {
        val loadingState = ProfileUiState(isLoading = true)
        val notLoadingState = ProfileUiState(isLoading = false)

        assertTrue(loadingState.isLoading)
        assertFalse(notLoadingState.isLoading)
    }

    @Test
    fun `state should contain user data`() {
        val user = UserDto(
            id = "user-123",
            deviceId = "device-abc",
            nickname = "智能用户",
            avatar = "https://example.com/avatar.jpg",
            createdAt = "2026-03-17T10:00:00Z"
        )
        val state = ProfileUiState(user = user)

        assertNotNull(state.user)
        assertEquals("user-123", state.user?.id)
        assertEquals("智能用户", state.user?.nickname)
    }

    @Test
    fun `state should track scheme count`() {
        val state = ProfileUiState(schemeCount = 5)

        assertEquals(5, state.schemeCount)
    }

    @Test
    fun `state should track logout status`() {
        val loggedOutState = ProfileUiState(isLoggedOut = true)
        val loggedInState = ProfileUiState(isLoggedOut = false)

        assertTrue(loggedOutState.isLoggedOut)
        assertFalse(loggedInState.isLoggedOut)
    }

    @Test
    fun `state should track error message`() {
        val errorState = ProfileUiState(error = "加载失败")

        assertEquals("加载失败", errorState.error)
    }

    @Test
    fun `state copy should update fields correctly`() {
        val original = ProfileUiState(isLoading = false)
        val updated = original.copy(isLoading = true, error = "网络错误")

        assertFalse(original.isLoading)
        assertTrue(updated.isLoading)
        assertEquals("网络错误", updated.error)
    }

    @Test
    fun `loading state should clear previous errors`() {
        val state = ProfileUiState(isLoading = true)

        assertTrue(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `user with minimal fields should be handled correctly`() {
        val user = UserDto(
            id = "user-min",
            deviceId = "device-min",
            nickname = null,
            avatar = null,
            createdAt = null
        )
        val state = ProfileUiState(user = user)

        assertNotNull(state.user)
        assertNull(state.user.nickname)
        assertNull(state.user.avatar)
    }
}
