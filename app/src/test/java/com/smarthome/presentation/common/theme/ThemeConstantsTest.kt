package com.smarthome.presentation.common.theme

import org.junit.Assert.*
import org.junit.Test

class ThemeConstantsTest {

    @Test
    fun `Primary colors should have correct values`() {
        assertEquals(0xFF4F46E5.toLong(), Primary.value.toLong())
        assertEquals(0xFF818CF8.toLong(), PrimaryLight.value.toLong())
        assertEquals(0xFF3730A3.toLong(), PrimaryDark.value.toLong())
    }

    @Test
    fun `Secondary colors should have correct values`() {
        assertEquals(0xFF10B981.toLong(), Secondary.value.toLong())
        assertEquals(0xFF34D399.toLong(), SecondaryLight.value.toLong())
        assertEquals(0xFF059669.toLong(), SecondaryDark.value.toLong())
    }

    @Test
    fun `Accent colors should have correct values`() {
        assertEquals(0xFFF59E0B.toLong(), Accent.value.toLong())
        assertEquals(0xFFFBBF24.toLong(), AccentLight.value.toLong())
        assertEquals(0xFFD97706.toLong(), AccentDark.value.toLong())
    }

    @Test
    fun `Background colors should have correct values`() {
        assertEquals(0xFFF8FAFC.toLong(), Background.value.toLong())
        assertEquals(0xFFF1F5F9.toLong(), BackgroundDark.value.toLong())
        assertEquals(0xFFFFFFFF.toLong(), Surface.value.toLong())
        assertEquals(0xFFF1F5F9.toLong(), SurfaceVariant.value.toLong())
    }

    @Test
    fun `Text colors should have correct values`() {
        assertEquals(0xFF1E293B.toLong(), TextPrimary.value.toLong())
        assertEquals(0xFF64748B.toLong(), TextSecondary.value.toLong())
        assertEquals(0xFF94A3B8.toLong(), TextTertiary.value.toLong())
        assertEquals(0xFFFFFFFF.toLong(), TextOnPrimary.value.toLong())
    }

    @Test
    fun `Status colors should have correct values`() {
        assertEquals(0xFF10B981.toLong(), Success.value.toLong())
        assertEquals(0xFFF59E0B.toLong(), Warning.value.toLong())
        assertEquals(0xFFEF4444.toLong(), Error.value.toLong())
        assertEquals(0xFF3B82F6.toLong(), Info.value.toLong())
    }

    @Test
    fun `Gradient colors should have correct count`() {
        assertEquals(2, GradientPrimary.size)
        assertEquals(2, GradientSecondary.size)
        assertEquals(2, GradientAccent.size)
        assertEquals(2, GradientBackground.size)
    }
}
