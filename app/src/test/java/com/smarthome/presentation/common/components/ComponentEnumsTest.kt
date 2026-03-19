package com.smarthome.presentation.common.components

import org.junit.Assert.*
import org.junit.Test

class ButtonEnumsTest {

    @Test
    fun `ButtonType enum should have all variants`() {
        val types = ButtonType.values()
        assertTrue(types.size >= 4)
        assertTrue(types.contains(ButtonType.Primary))
        assertTrue(types.contains(ButtonType.Secondary))
        assertTrue(types.contains(ButtonType.Outline))
        assertTrue(types.contains(ButtonType.Text))
    }

    @Test
    fun `ButtonSize enum should have all variants`() {
        val sizes = ButtonSize.values()
        assertEquals(3, sizes.size)
        assertTrue(sizes.contains(ButtonSize.Small))
        assertTrue(sizes.contains(ButtonSize.Medium))
        assertTrue(sizes.contains(ButtonSize.Large))
    }
}

class DialogEnumsTest {

    @Test
    fun `DialogType enum should have variants`() {
        val types = DialogType.values()
        assertTrue(types.isNotEmpty())
    }
}

class EmptyStateEnumsTest {

    @Test
    fun `EmptyStateType enum should have variants`() {
        val types = EmptyStateType.values()
        assertTrue(types.isNotEmpty())
    }
}
