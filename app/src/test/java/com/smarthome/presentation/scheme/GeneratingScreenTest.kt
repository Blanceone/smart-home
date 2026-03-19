package com.smarthome.presentation.scheme

import org.junit.Assert.*
import org.junit.Test

class GeneratingScreenTest {

    @Test
    fun `GenerationStep should store step information`() {
        val step = GenerationStep(
            text = "正在分析您的需求...",
            icon = androidx.compose.material.icons.Icons.Default.Person,
            progress = 0.2f
        )

        assertEquals("正在分析您的需求...", step.text)
        assertEquals(0.2f, step.progress, 0.0f)
    }

    @Test
    fun `GenerationStep should handle different progress values`() {
        val steps = listOf(
            GenerationStep("步骤1", androidx.compose.material.icons.Icons.Default.Person, 0.0f),
            GenerationStep("步骤2", androidx.compose.material.icons.Icons.Default.Devices, 0.25f),
            GenerationStep("步骤3", androidx.compose.material.icons.Icons.Default.Home, 0.5f),
            GenerationStep("步骤4", androidx.compose.material.icons.Icons.Default.Tune, 0.75f),
            GenerationStep("完成", androidx.compose.material.icons.Icons.Default.CheckCircle, 1.0f)
        )

        assertEquals(5, steps.size)
        assertEquals(0.0f, steps[0].progress, 0.0f)
        assertEquals(1.0f, steps[4].progress, 0.0f)
    }

    @Test
    fun `GenerationStep progress should be between 0 and 1`() {
        val step = GenerationStep(
            text = "测试步骤",
            icon = androidx.compose.material.icons.Icons.Default.Home,
            progress = 0.5f
        )

        assertTrue(step.progress >= 0.0f)
        assertTrue(step.progress <= 1.0f)
    }
}
