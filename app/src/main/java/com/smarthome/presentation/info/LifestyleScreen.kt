package com.smarthome.presentation.info

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.smarthome.presentation.common.components.*
import com.smarthome.presentation.common.theme.*

@Composable
fun LifestyleScreen(
    initialData: LifestyleData = LifestyleData(),
    onNextClick: (LifestyleData) -> Unit,
    onSkipClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var sleepPattern by remember { mutableStateOf(initialData.sleepPattern) }
    var homeActivities by remember { mutableStateOf(initialData.homeActivities) }
    var entertainmentHabits by remember { mutableStateOf(initialData.entertainmentHabits) }
    
    val sleepOptions = listOf(
        ChipOption("early", "早睡早起"),
        ChipOption("late", "晚睡晚起"),
        ChipOption("irregular", "作息不规律")
    )
    
    val activityOptions = listOf(
        ChipOption("work", "工作学习"),
        ChipOption("entertainment", "休闲娱乐"),
        ChipOption("fitness", "健身运动"),
        ChipOption("cooking", "烹饪美食"),
        ChipOption("other", "其他")
    )
    
    val entertainmentOptions = listOf(
        ChipOption("movie", "看电影追剧"),
        ChipOption("music", "听音乐"),
        ChipOption("gaming", "玩游戏"),
        ChipOption("reading", "阅读"),
        ChipOption("other", "其他")
    )
    
    InfoCollectionScreen(
        currentStep = 2,
        onBackClick = onBackClick
    ) {
        StepTitle(
            title = stepContents[1].title,
            subtitle = stepContents[1].subtitle
        )
        
        Spacer(modifier = Modifier.height(Dimens.spacingXl))
        
        InfoCollectionContent {
            FormSection(title = "您的作息时间") {
                VerticalChipGroup(
                    options = sleepOptions,
                    selectedIds = sleepPattern?.let { setOf(it) } ?: emptySet(),
                    onSelectionChange = { sleepPattern = it.firstOrNull() },
                    singleSelection = true
                )
            }
            
            Spacer(modifier = Modifier.height(Dimens.spacingLg))
            
            FormSection(title = "在家活动偏好（可多选）") {
                VerticalChipGroup(
                    options = activityOptions,
                    selectedIds = homeActivities,
                    onSelectionChange = { homeActivities = it },
                    singleSelection = false
                )
            }
            
            Spacer(modifier = Modifier.height(Dimens.spacingLg))
            
            FormSection(title = "娱乐习惯（可多选）") {
                VerticalChipGroup(
                    options = entertainmentOptions,
                    selectedIds = entertainmentHabits,
                    onSelectionChange = { entertainmentHabits = it },
                    singleSelection = false
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        StepNavigationButtons(
            onNextClick = {
                onNextClick(
                    LifestyleData(
                        sleepPattern = sleepPattern,
                        homeActivities = homeActivities,
                        entertainmentHabits = entertainmentHabits
                    )
                )
            },
            onSkipClick = onSkipClick
        )
    }
}
