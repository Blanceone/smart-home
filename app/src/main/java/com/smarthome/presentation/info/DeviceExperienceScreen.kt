package com.smarthome.presentation.info

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.smarthome.presentation.common.components.*
import com.smarthome.presentation.common.theme.*

@Composable
fun DeviceExperienceScreen(
    initialData: DeviceExperienceData = DeviceExperienceData(),
    onNextClick: (DeviceExperienceData) -> Unit,
    onSkipClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var knowledgeLevel by remember { mutableStateOf(initialData.knowledgeLevel) }
    var usedDevices by remember { mutableStateOf(initialData.usedDevices) }
    
    val knowledgeOptions = listOf(
        ChipOption("none", "完全不了解"),
        ChipOption("heard", "听说过但没用过"),
        ChipOption("some", "用过一些"),
        ChipOption("expert", "非常熟悉")
    )
    
    val deviceOptions = listOf(
        ChipOption("speaker", "智能音箱"),
        ChipOption("light", "智能灯具"),
        ChipOption("lock", "智能门锁"),
        ChipOption("robot", "扫地机器人"),
        ChipOption("curtain", "智能窗帘"),
        ChipOption("other", "其他")
    )
    
    InfoCollectionScreen(
        currentStep = 3,
        onBackClick = onBackClick
    ) {
        StepTitle(
            title = stepContents[2].title,
            subtitle = stepContents[2].subtitle
        )
        
        Spacer(modifier = Modifier.height(Dimens.spacingXl))
        
        InfoCollectionContent {
            FormSection(title = "您对智能设备的了解程度") {
                VerticalChipGroup(
                    options = knowledgeOptions,
                    selectedIds = knowledgeLevel?.let { setOf(it) } ?: emptySet(),
                    onSelectionChange = { knowledgeLevel = it.firstOrNull() },
                    singleSelection = true
                )
            }
            
            Spacer(modifier = Modifier.height(Dimens.spacingLg))
            
            FormSection(title = "您使用过哪些智能设备（可多选）") {
                VerticalChipGroup(
                    options = deviceOptions,
                    selectedIds = usedDevices,
                    onSelectionChange = { usedDevices = it },
                    singleSelection = false
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        StepNavigationButtons(
            onNextClick = {
                onNextClick(
                    DeviceExperienceData(
                        knowledgeLevel = knowledgeLevel,
                        usedDevices = usedDevices
                    )
                )
            },
            onSkipClick = onSkipClick
        )
    }
}
