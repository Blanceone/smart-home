package com.smarthome.presentation.scheme

import com.smarthome.data.remote.dto.SchemeDto
import com.smarthome.data.remote.dto.DecorationGuideDto
import com.smarthome.data.remote.dto.SchemeDeviceDto
import com.smarthome.data.remote.dto.ExportDto
import org.junit.Assert.*
import org.junit.Test

class SchemeUiStateTest {

    @Test
    fun `Loading state should be a singleton`() {
        val loading1 = SchemeUiState.Loading
        val loading2 = SchemeUiState.Loading
        assertEquals(loading1, loading2)
    }

    @Test
    fun `Error state should contain correct message`() {
        val errorMessage = "网络错误"
        val errorState = SchemeUiState.Error(errorMessage)
        assertEquals(errorMessage, errorState.message)
    }

    @Test
    fun `Error state should handle different error types`() {
        val networkError = SchemeUiState.Error("网络连接失败")
        val serverError = SchemeUiState.Error("服务器错误")
        val unknownError = SchemeUiState.Error("未知错误")

        assertEquals("网络连接失败", networkError.message)
        assertEquals("服务器错误", serverError.message)
        assertEquals("未知错误", unknownError.message)
    }

    @Test
    fun `Success state should contain scheme data`() {
        val scheme = createTestScheme()
        val successState = SchemeUiState.Success(scheme)

        assertEquals(scheme.id, successState.scheme.id)
        assertEquals(scheme.name, successState.scheme.name)
        assertEquals(scheme.budget, successState.scheme.budget, 0.0)
    }

    @Test
    fun `Success state should track saved status`() {
        val scheme = createTestScheme()
        val savedState = SchemeUiState.Success(scheme, isSaved = true)
        val unsavedState = SchemeUiState.Success(scheme, isSaved = false)

        assertTrue(savedState.isSaved)
        assertFalse(unsavedState.isSaved)
    }

    @Test
    fun `Success state copy should update isSaved`() {
        val scheme = createTestScheme()
        val originalState = SchemeUiState.Success(scheme, isSaved = false)
        val newState = originalState.copy(isSaved = true)

        assertFalse(originalState.isSaved)
        assertTrue(newState.isSaved)
    }

    @Test
    fun `Success state should handle export info`() {
        val scheme = createTestScheme()
        val exportInfo = ExportDto(
            pdfUrl = "https://example.com/scheme.pdf",
            fileName = "智能家居方案.pdf",
            expiresAt = "2026-03-18T10:00:00Z"
        )

        val stateWithExport = SchemeUiState.Success(
            scheme = scheme,
            isSaved = true,
            exportInfo = exportInfo
        )

        val stateWithoutExport = SchemeUiState.Success(
            scheme = scheme,
            isSaved = true,
            exportInfo = null
        )

        assertNotNull(stateWithExport.exportInfo)
        assertEquals("https://example.com/scheme.pdf", stateWithExport.exportInfo?.pdfUrl)
        assertNull(stateWithoutExport.exportInfo)
    }

    private fun createTestScheme(): SchemeDto {
        return SchemeDto(
            id = "scheme-123",
            name = "智能家居方案",
            budget = 10000.0,
            totalPrice = 9500.0,
            status = "completed",
            decorationGuide = DecorationGuideDto(
                summary = "这是一个测试方案",
                professionalAdvice = "建议安装智能门锁",
                rooms = listOf(
                    com.smarthome.data.remote.dto.RoomGuideDto(
                        name = "客厅",
                        advice = "建议安装智能灯具"
                    )
                )
            ),
            devices = listOf(
                SchemeDeviceDto(
                    id = "device-1",
                    name = "智能门锁",
                    brand = "小米",
                    category = "安防",
                    price = 699.0,
                    quantity = 1,
                    description = "智能门锁",
                    recommendReason = "安全性高",
                    imageUrl = null,
                    taobaoUrl = "https://item.taobao.com/123"
                )
            )
        )
    }
}
