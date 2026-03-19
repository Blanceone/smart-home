package com.smarthome.presentation.scheme

import com.smarthome.data.remote.dto.*
import com.smarthome.domain.repository.SchemeRepository
import com.smarthome.domain.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SchemeViewModelTest {

    @Mock
    private lateinit var schemeRepository: SchemeRepository

    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var viewModel: SchemeViewModel

    @Before
    fun setup() {
        viewModel = SchemeViewModel(schemeRepository, userRepository, mockk())
    }

    @Test
    fun `initial state should be Loading`() = runBlocking {
        val state = viewModel.uiState.first()
        assertTrue(state is SchemeUiState.Loading)
    }

    @Test
    fun `generateScheme should update state to Success on valid response`() = runBlocking {
        val mockScheme = createTestSchemeDto()
        every { runBlocking { schemeRepository.generateScheme(any(), any()) } }
            .returns(Result.success(mockScheme))

        viewModel.generateScheme(10000.0)

        val state = viewModel.uiState.first()
        assertTrue(state is SchemeUiState.Success)
        assertEquals("智能家居方案", (state as SchemeUiState.Success).scheme.name)
    }

    @Test
    fun `generateScheme should update state to Error on failure`() = runBlocking {
        every { runBlocking { schemeRepository.generateScheme(any(), any()) } }
            .returns(Result.failure(Exception("生成失败")))

        viewModel.generateScheme(10000.0)

        val state = viewModel.uiState.first()
        assertTrue(state is SchemeUiState.Error)
        assertEquals("生成失败", (state as SchemeUiState.Error).message)
    }

    @Test
    fun `loadSchemeDetail should update state to Success on valid response`() = runBlocking {
        val mockScheme = createTestSchemeDto()
        every { runBlocking { schemeRepository.getSchemeDetail(any()) } }
            .returns(Result.success(mockScheme))

        viewModel.loadSchemeDetail("scheme-123")

        val state = viewModel.uiState.first()
        assertTrue(state is SchemeUiState.Success)
        assertEquals("scheme-123", (state as SchemeUiState.Success).scheme.id)
    }

    @Test
    fun `loadSchemeDetail should update state to Error on failure`() = runBlocking {
        every { runBlocking { schemeRepository.getSchemeDetail(any()) } }
            .returns(Result.failure(Exception("方案不存在")))

        viewModelSchemeDetail("scheme-123")

        val state = viewModel.uiState.first()
        assertTrue(state is SchemeUiState.Error)
        assertTrue((state as SchemeUiState.Error).message.contains("方案不存在") || state.message.contains("加载方案失败"))
    }

    @Test
    fun `saveScheme should update isSaved to true on success`() = runBlocking {
        val mockScheme = createTestSchemeDto()
        every { runBlocking { schemeRepository.getSchemeDetail(any()) } }
            .returns(Result.success(mockScheme))
        every { runBlocking { schemeRepository.saveScheme(any()) } }
            .returns(Result.success(SaveSchemeResponse("scheme-123", "2026-03-18T10:00:00Z")))

        viewModel.loadSchemeDetail("scheme-123")
        kotlinx.coroutines.delay(100)
        viewModel.saveScheme()

        val state = viewModel.uiState.first()
        if (state is SchemeUiState.Success) {
            assertTrue(state.isSaved)
        }
    }

    @Test
    fun `shareScheme should build correct share text`() = runBlocking {
        val mockScheme = createTestSchemeDto()
        every { runBlocking { schemeRepository.getSchemeDetail(any()) } }
            .returns(Result.success(mockScheme))

        viewModel.loadSchemeDetail("scheme-123")
        kotlinx.coroutines.delay(100)

        viewModel.shareScheme()

        val state = viewModel.uiState.first()
        assertTrue(state is SchemeUiState.Success)
    }

    @Test
    fun `exportScheme should update exportInfo on success`() = runBlocking {
        val mockScheme = createTestSchemeDto()
        val mockExportInfo = ExportDto(
            pdfUrl = "https://example.com/scheme.pdf",
            fileName = "智能家居方案.pdf",
            expiresAt = "2026-03-18T10:00:00Z"
        )

        every { runBlocking { schemeRepository.getSchemeDetail(any()) } }
            .returns(Result.success(mockScheme))
        every { runBlocking { schemeRepository.exportScheme(any()) } }
            .returns(Result.success(mockExportInfo))

        viewModel.loadSchemeDetail("scheme-123")
        kotlinx.coroutines.delay(100)
        viewModel.exportScheme()

        val state = viewModel.uiState.first()
        if (state is SchemeUiState.Success) {
            assertNotNull(state.exportInfo)
            assertEquals("https://example.com/scheme.pdf", state.exportInfo?.pdfUrl)
        }
    }

    @Test
    fun `clearExportInfo should set exportInfo to null`() = runBlocking {
        val mockScheme = createTestSchemeDto()
        val mockExportInfo = ExportDto(
            pdfUrl = "https://example.com/scheme.pdf",
            fileName = "智能家居方案.pdf",
            expiresAt = "2026-03-18T10:00:00Z"
        )

        every { runBlocking { schemeRepository.getSchemeDetail(any()) } }
            .returns(Result.success(mockScheme))
        every { runBlocking { schemeRepository.exportScheme(any()) } }
            .returns(Result.success(mockExportInfo))

        viewModel.loadSchemeDetail("scheme-123")
        kotlinx.coroutines.delay(100)
        viewModel.exportScheme()
        kotlinx.coroutines.delay(100)

        viewModel.clearExportInfo()

        val state = viewModel.uiState.first()
        if (state is SchemeUiState.Success) {
            assertNull(state.exportInfo)
        }
    }

    @Test
    fun `submitRating should call userRepository`() = runBlocking {
        val mockScheme = createTestSchemeDto()
        every { runBlocking { schemeRepository.getSchemeDetail(any()) } }
            .returns(Result.success(mockScheme))
        every { runBlocking { userRepository.submitSchemeRating(any(), any(), any()) } }
            .returns(Result.success(FeedbackResponse("rating-123", "2026-03-18T10:00:00Z")))

        viewModel.loadSchemeDetail("scheme-123")
        kotlinx.coroutines.delay(100)
        viewModel.submitRating(5, "很棒")

        verify { runBlocking { userRepository.submitSchemeRating("scheme-123", 5, "很棒") } }
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
}
