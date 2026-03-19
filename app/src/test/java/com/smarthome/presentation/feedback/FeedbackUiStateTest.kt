package com.smarthome.presentation.feedback

import com.smarthome.data.remote.dto.FeedbackResponse
import org.junit.Assert.*
import org.junit.Test

class FeedbackUiStateTest {

    @Test
    fun `default state should have correct initial values`() {
        val state = FeedbackUiState()

        assertFalse(state.isLoading)
        assertFalse(state.isSuccess)
        assertNull(state.feedbackResponse)
        assertNull(state.error)
    }

    @Test
    fun `state should track loading status`() {
        val loadingState = FeedbackUiState(isLoading = true)
        val notLoadingState = FeedbackUiState(isLoading = false)

        assertTrue(loadingState.isLoading)
        assertFalse(notLoadingState.isLoading)
    }

    @Test
    fun `state should track success status`() {
        val successState = FeedbackUiState(isSuccess = true)
        val failureState = FeedbackUiState(isSuccess = false)

        assertTrue(successState.isSuccess)
        assertFalse(failureState.isSuccess)
    }

    @Test
    fun `state should contain feedback response on success`() {
        val response = FeedbackResponse(
            id = "feedback-123",
            type = "suggestion",
            content = "建议增加更多品牌选择",
            contact = "test@example.com",
            createdAt = "2026-03-17T10:00:00Z"
        )
        val state = FeedbackUiState(isSuccess = true, feedbackResponse = response)

        assertTrue(state.isSuccess)
        assertNotNull(state.feedbackResponse)
        assertEquals("feedback-123", state.feedbackResponse?.id)
        assertEquals("suggestion", state.feedbackResponse?.type)
    }

    @Test
    fun `state should track error message`() {
        val errorState = FeedbackUiState(error = "提交失败，请稍后重试")

        assertEquals("提交失败，请稍后重试", errorState.error)
    }

    @Test
    fun `state copy should update fields correctly`() {
        val original = FeedbackUiState(isLoading = false, isSuccess = false)
        val updated = original.copy(isLoading = true, error = "网络错误")

        assertFalse(original.isLoading)
        assertTrue(updated.isLoading)
        assertEquals("网络错误", updated.error)
    }

    @Test
    fun `loading state should reset success and error`() {
        val state = FeedbackUiState(isLoading = true)

        assertTrue(state.isLoading)
        assertFalse(state.isSuccess)
        assertNull(state.error)
    }

    @Test
    fun `success state should have response data`() {
        val response = FeedbackResponse(
            id = "feedback-456",
            type = "rating",
            content = "方案很满意",
            contact = null,
            createdAt = "2026-03-16T10:00:00Z"
        )
        val state = FeedbackUiState(isSuccess = true, feedbackResponse = response)

        assertTrue(state.isSuccess)
        assertNotNull(state.feedbackResponse)
        assertEquals("rating", state.feedbackResponse?.type)
    }
}
