package com.smarthome.presentation.common.theme

import org.junit.Assert.*
import org.junit.Test

class DimensionConstantsTest {

    @Test
    fun `spacing values should follow 4dp grid system`() {
        assertEquals(4f, Dimens.spacingXs.value)
        assertEquals(8f, Dimens.spacingSm.value)
        assertEquals(16f, Dimens.spacingMd.value)
        assertEquals(24f, Dimens.spacingLg.value)
        assertEquals(32f, Dimens.spacingXl.value)
        assertEquals(48f, Dimens.spacingXxl.value)
    }

    @Test
    fun `corner values should be reasonable`() {
        assertTrue(Dimens.cornerSmall.value <= 12f)
        assertTrue(Dimens.cornerMedium.value <= 16f)
        assertTrue(Dimens.cornerLarge.value >= 12f)
        assertTrue(Dimens.cornerXLarge.value >= 20f)
    }

    @Test
    fun `icon sizes should follow standard sizes`() {
        assertEquals(16f, Dimens.iconSizeXs.value)
        assertEquals(20f, Dimens.iconSizeSm.value)
        assertEquals(24f, Dimens.iconSizeMd.value)
        assertEquals(32f, Dimens.iconSizeLg.value)
        assertEquals(48f, Dimens.iconSizeXl.value)
    }

    @Test
    fun `button heights should follow design spec`() {
        assertEquals(36f, Dimens.buttonHeightSm.value)
        assertEquals(48f, Dimens.buttonHeightMd.value)
        assertEquals(56f, Dimens.buttonHeightLg.value)
    }

    @Test
    fun `navigation heights should be correct`() {
        assertEquals(80f, Dimens.bottomNavHeight.value)
        assertEquals(56f, Dimens.topBarHeight.value)
    }

    @Test
    fun `padding values should be correct`() {
        assertEquals(20f, Dimens.horizontalPadding.value)
        assertEquals(16f, Dimens.verticalPadding.value)
    }

    @Test
    fun `card elevation should be subtle`() {
        assertTrue(Dimens.cardElevation.value <= 4f)
        assertTrue(Dimens.cardElevationPressed.value <= 8f)
    }
}
