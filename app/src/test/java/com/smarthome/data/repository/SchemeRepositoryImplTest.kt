package com.smarthome.data.repository

import com.smarthome.data.remote.api.SchemeApi
import com.smarthome.data.remote.dto.*
import com.smarthome.data.local.UserPreferences
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import kotlinx.coroutines.runBlocking

@RunWith(MockitoJUnitRunner::class)
class SchemeRepositoryImplTest {

    @Mock
    private lateinit var schemeApi: SchemeApi

    @Mock
    private lateinit var userPreferences: UserPreferences

    private lateinit var repository: SchemeRepositoryImpl

    @Before
    fun setup() {
        repository = SchemeRepositoryImpl(schemeApi, userPreferences, mockk())
    }

    @Test
    fun `generateScheme should return success when API returns valid response`() = runBlocking {
        val mockResponse = ApiResponse(
            code = 0,
            message = "success",
            data = createTestSchemeDto(),
            timestamp = System.currentTimeMillis()
        )
        every { runBlocking { schemeApi.generateScheme(any()) } }
            .returns(mockResponse)

        val result = repository.generateScheme(10000.0, false)

        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
        assertEquals("智能家居方案", result.getOrNull()?.name)
    }

    @Test
    fun `generateScheme should return failure when API returns error`() = runBlocking {
        val mockResponse = ApiResponse(
            code = 3001,
            message = "生成方案失败",
            data = null,
            timestamp = System.currentTimeMillis()
        )
        every { runBlocking { schemeApi.generateScheme(any()) } }
            .returns(mockResponse)

        val result = repository.generateScheme(10000.0, false)

        assertTrue(result.isFailure)
    }

    @Test
    fun `getSchemeDetail should return success when API returns valid response`() = runBlocking {
        val mockResponse = ApiResponse(
            code = 0,
            message = "success",
            data = createTestSchemeDto(),
            timestamp = System.currentTimeMillis()
        )
        every { runBlocking { schemeApi.getSchemeDetail(any()) } }
            .returns(mockResponse)

        val result = repository.getSchemeDetail("scheme-123")

        assertTrue(result.isSuccess)
        assertEquals("scheme-123", result.getOrNull()?.id)
    }

    @Test
    fun `getUserSchemes should return success when API returns valid response`() = runBlocking {
        val mockSchemeList = createTestSchemeListDto()
        val mockResponse = ApiResponse(
            code = 0,
            message = "success",
            data = mockSchemeList,
            timestamp = System.currentTimeMillis()
        )
        every { runBlocking { schemeApi.getUserSchemes(any(), any()) } }
            .returns(mockResponse)

        val result = repository.getUserSchemes()

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.list?.size)
    }

    @Test
    fun `saveScheme should return success when API returns valid response`() = runBlocking {
        val mockResponse = ApiResponse(
            code = 0,
            message = "success",
            data = SaveSchemeResponse("scheme-123", "2026-03-18T10:00:00Z"),
            timestamp = System.currentTimeMillis()
        )
        every { runBlocking { schemeApi.saveScheme(any()) } }
            .returns(mockResponse)

        val result = repository.saveScheme("scheme-123")

        assertTrue(result.isSuccess)
    }

    @Test
    fun `deleteScheme should return success when API returns valid response`() = runBlocking {
        val mockResponse = ApiResponse(
            code = 0,
            message = "success",
            data = null,
            timestamp = System.currentTimeMillis()
        )
        every { runBlocking { schemeApi.deleteScheme(any()) } }
            .returns(mockResponse)

        val result = repository.deleteScheme("scheme-123")

        assertTrue(result.isSuccess)
    }

    @Test
    fun `exportScheme should return success when API returns valid response`() = runBlocking {
        val mockExportInfo = ExportDto(
            pdfUrl = "https://example.com/scheme.pdf",
            fileName = "智能家居方案.pdf",
            expiresAt = "2026-03-18T10:00:00Z"
        )
        val mockResponse = ApiResponse(
            code = 0,
            message = "success",
            data = mockExportInfo,
            timestamp = System.currentTimeMillis()
        )
        every { runBlocking { schemeApi.exportScheme(any()) } }
            .returns(mockResponse)

        val result = repository.exportScheme("scheme-123")

        assertTrue(result.isSuccess)
        assertEquals("https://example.com/scheme.pdf", result.getOrNull()?.pdfUrl)
    }

    private fun createTestSchemeDto(): SchemeDto {
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
                    RoomGuideDto(
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

    private fun createTestSchemeListDto(): SchemeListDto {
        return SchemeListDto(
            list = listOf(
                SchemeItemDto(
                    id = "scheme-1",
                    name = "方案1",
                    budget = 10000.0,
                    totalPrice = 9500.0,
                    status = "completed",
                    deviceCount = 5,
                    createdAt = "2026-03-18T10:00:00Z"
                ),
                SchemeItemDto(
                    id = "scheme-2",
                    name = "方案2",
                    budget = 15000.0,
                    totalPrice = 14000.0,
                    status = "completed",
                    deviceCount = 8,
                    createdAt = "2026-03-17T10:00:00Z"
                )
            ),
            pagination = PaginationDto(
                page = 1,
                pageSize = 20,
                total = 2,
                totalPages = 1
            )
        )
    }
}
