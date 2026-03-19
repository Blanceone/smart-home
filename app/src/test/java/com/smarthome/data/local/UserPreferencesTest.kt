package com.smarthome.data.local

import org.junit.Assert.*
import org.junit.Test

class UserPreferencesTest {

    @Test
    fun `UserPreferences should store userId`() {
        val prefs = UserPreferences(
            userId = "user-123",
            deviceId = "device-abc",
            nickname = "测试用户",
            avatar = null,
            isLoggedIn = true
        )
        
        assertEquals("user-123", prefs.userId)
        assertEquals("device-abc", prefs.deviceId)
        assertTrue(prefs.isLoggedIn)
    }

    @Test
    fun `UserPreferences should handle logout state`() {
        val loggedOutPrefs = UserPreferences(
            userId = null,
            deviceId = null,
            nickname = null,
            avatar = null,
            isLoggedIn = false
        )
        
        assertNull(loggedOutPrefs.userId)
        assertNull(loggedOutPrefs.deviceId)
        assertFalse(loggedOutPrefs.isLoggedIn)
    }

    @Test
    fun `UserPreferences copy should update fields`() {
        val original = UserPreferences(
            userId = "user-1",
            deviceId = "device-1",
            nickname = "原昵称",
            avatar = null,
            isLoggedIn = true
        )
        
        val updated = original.copy(nickname = "新昵称")
        
        assertEquals("原昵称", original.nickname)
        assertEquals("新昵称", updated.nickname)
        assertEquals(original.userId, updated.userId)
    }
}
