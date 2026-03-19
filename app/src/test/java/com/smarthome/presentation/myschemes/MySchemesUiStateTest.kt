package com.smarthome.presentation.myschemes

import com.smarthome.data.remote.dto.ExportDto
import com.smarthome.data.remote.dto.SchemeItemDto
import org.junit.Assert.*
import org.junit.Test

class MySchemesUiStateTest {

    @Test
    fun `default state should have correct initial values`() {
        val state = MySchemesUiState()

        assertFalse(state.isLoading)
        assertTrue(state.schemes.isEmpty())
        assertNull(state.exportInfo)
        assertNull(state.deletedSchemeId)
        assertNull(state.error)
    }

    @Test
    fun `state should track loading status`() {
        val loadingState = MySchemesUiState(isLoading = true)
        val notLoadingState = MySchemesUiState(isLoading = false)

        assertTrue(loadingState.isLoading)
        assertFalse(notLoadingState.isLoading)
    }

    @Test
    fun `state should contain schemes list`() {
        val schemes = listOf(
            SchemeItemDto(
                id = "scheme-1",
                name = "我的智能家居方案",
                budget = 10000.0,
                totalPrice = 9500.0,
                deviceCount = 8,
                createdAt = "2026-03-15T10:00:00Z"
            ),
            SchemeItemDto(
                id = "scheme-2",
                name = "升级版方案",
                budget = 15000.0,
                totalPrice = 14000.0,
                deviceCount = 12,
                createdAt = "2026-03-14T10:00:00Z"
            ),
            SchemeItemDto(
                id = "scheme-3",
                name = "精简版方案",
                budget = 5000.0,
                totalPrice = 4800.0,
                deviceCount = 5,
                createdAt = "2026-03-13T10:00:00Z"
            )
        )
        val state = MySchemesUiState(schemes = schemes)

        assertEquals(3, state.schemes.size)
        assertEquals("我的智能家居方案", state.schemes[0].name)
        assertEquals("升级版方案", state.schemes[1].name)
        assertEquals("精简版方案", state.schemes[2].name)
    }

    @Test
    fun `state should track export info`() {
        val exportInfo = ExportDto(
            pdfUrl = "https://api.example.com/export/scheme-123.pdf",
            fileName = "智能家居方案_20260317.pdf",
            expiresAt = "2026-03-18T10:00:00Z"
        )
        val state = MySchemesUiState(exportInfo = exportInfo)

        assertNotNull(state.exportInfo)
        assertEquals("https://api.example.com/export/scheme-123.pdf", state.exportInfo?.pdfUrl)
    }

    @Test
    fun `state should track deleted scheme id`() {
        val state = MySchemesUiState(deletedSchemeId = "scheme-2")

        assertEquals("scheme-2", state.deletedSchemeId)
    }

    @Test
    fun `state should track error message`() {
        val errorState = MySchemesUiState(error = "加载失败")

        assertEquals("加载失败", errorState.error)
    }

    @Test
    fun `state copy should update fields correctly`() {
        val original = MySchemesUiState(isLoading = false)
        val updated = original.copy(isLoading = true, error = "网络错误")

        assertFalse(original.isLoading)
        assertTrue(updated.isLoading)
        assertEquals("网络错误", updated.error)
    }

    @Test
    fun `empty schemes should be handled correctly`() {
        val state = MySchemesUiState(schemes = emptyList())

        assertTrue(state.schemes.isEmpty())
        assertEquals(0, state.schemes.size)
    }

    @Test
    fun `loading state should clear previous errors`() {
        val state = MySchemesUiState(isLoading = true)

        assertTrue(state.isLoading)
        assertNull(state.error)
    }
}
