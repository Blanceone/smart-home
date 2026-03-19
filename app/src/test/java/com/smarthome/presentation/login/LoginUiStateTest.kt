package com.smarthome.presentation.login

import com.smarthome.data.remote.dto.UserDto
import org.junit.Assert.*
import org.junit.Test

class LoginUiStateTest {

    @Test
    fun `default state should have correct initial values`() {
        val state = LoginUiState()

        assertFalse(state.isLoading)
        assertFalse(state.isLoggedIn)
        assertNull(state.user)
        assertNull(state.error)
    }

    @Test
    fun `state should track loading status`() {
        val loadingState = LoginUiState(isLoading = true)
        val notLoadingState = LoginUiState(isLoading = false)

        assertTrue(loadingState.isLoading)
        assertFalse(notLoadingState.isLoading)
    }

    @Test
    fun `state should track login status`() {
        val loggedInState = LoginUiState(isLoggedIn = true)
        val loggedOutState = LoginUiState(isLoggedIn = false)

        assertTrue(loggedInState.isLoggedIn)
        assertFalse(loggedOutState.isLoggedIn)
    }

    @Test
    fun `state should contain user data when logged in`() {
        val user = UserDto(
            id = "user-123",
            deviceId = "device-abc",
            nickname = "测试用户",
            avatar = "https://example.com/avatar.jpg",
            createdAt = "2026-03-17T10:00:00Z"
        )
        val state = LoginUiState(isLoggedIn = true, user = user)

        assertTrue(state.isLoggedIn)
        assertNotNull(state.user)
        assertEquals("user-123", state.user?.id)
    }

    @Test
    fun `state should track error message`() {
        val errorState = LoginUiState(error = "登录失败")

        assertEquals("登录失败", errorState.error)
    }

    @Test
    fun `state should handle null user when not logged in`() {
        val state = LoginUiState(isLoggedIn = false, user = null)

        assertFalse(state.isLoggedIn)
        assertNull(state.user)
    }

    @Test
    fun `state copy should update fields correctly`() {
        val original = LoginUiState(isLoading = false, isLoggedIn = false)
        val updated = original.copy(isLoading = true, error = "网络错误")

        assertFalse(original.isLoading)
        assertTrue(updated.isLoading)
        assertEquals("网络错误", updated.error)
    }

    @Test
    fun `loading state should clear previous errors`() {
        val state = LoginUiState(isLoading = true)

        assertTrue(state.isLoading)
        assertNull(state.error)
    }
}
