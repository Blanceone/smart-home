package com.smarthome.presentation.info

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.smarthome.presentation.common.components.*
import com.smarthome.presentation.common.theme.*

@Composable
fun BasicInfoScreen(
    initialData: BasicInfoData = BasicInfoData(),
    onNextClick: (BasicInfoData) -> Unit,
    onSkipClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var age by remember { mutableStateOf(initialData.age) }
    var occupation by remember { mutableStateOf(initialData.occupation) }
    var familyMembers by remember { mutableStateOf(initialData.familyMembers) }
    var city by remember { mutableStateOf(initialData.city) }
    
    val ageOptions = listOf(
        ChipOption("18-25", "18-25岁"),
        ChipOption("26-30", "26-30岁"),
        ChipOption("31-35", "31-35岁"),
        ChipOption("35+", "35岁以上")
    )
    
    val occupationOptions = listOf(
        ChipOption("student", "学生"),
        ChipOption("office", "上班族"),
        ChipOption("freelance", "自由职业"),
        ChipOption("other", "其他")
    )
    
    val familyOptions = listOf(
        ChipOption("alone", "独居"),
        ChipOption("couple", "情侣"),
        ChipOption("family", "夫妻+孩子"),
        ChipOption("parents", "与父母同住"),
        ChipOption("other", "其他")
    )
    
    InfoCollectionScreen(
        currentStep = 1,
        onBackClick = onBackClick
    ) {
        StepTitle(
            title = stepContents[0].title,
            subtitle = stepContents[0].subtitle
        )
        
        Spacer(modifier = Modifier.height(Dimens.spacingXl))
        
        InfoCollectionContent {
            FormSection(title = "您的年龄段") {
                VerticalChipGroup(
                    options = ageOptions,
                    selectedIds = age?.let { setOf(it) } ?: emptySet(),
                    onSelectionChange = { age = it.firstOrNull() },
                    singleSelection = true
                )
            }
            
            Spacer(modifier = Modifier.height(Dimens.spacingLg))
            
            FormSection(title = "您的职业") {
                VerticalChipGroup(
                    options = occupationOptions,
                    selectedIds = occupation?.let { setOf(it) } ?: emptySet(),
                    onSelectionChange = { occupation = it.firstOrNull() },
                    singleSelection = true
                )
            }
            
            Spacer(modifier = Modifier.height(Dimens.spacingLg))
            
            FormSection(title = "家庭成员") {
                VerticalChipGroup(
                    options = familyOptions,
                    selectedIds = familyMembers,
                    onSelectionChange = { familyMembers = it },
                    singleSelection = false
                )
            }
            
            Spacer(modifier = Modifier.height(Dimens.spacingLg))
            
            FormSection(title = "居住城市") {
                SmartTextField(
                    value = city,
                    onValueChange = { city = it },
                    placeholder = "请输入您所在的城市",
                    leadingIcon = androidx.compose.material.icons.Icons.Default.LocationOn
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        StepNavigationButtons(
            onNextClick = {
                onNextClick(
                    BasicInfoData(
                        age = age,
                        occupation = occupation,
                        familyMembers = familyMembers,
                        city = city
                    )
                )
            },
            onSkipClick = onSkipClick,
            nextEnabled = age != null && occupation != null
        )
    }
}

@Composable
fun StepTitle(
    title: String,
    subtitle: String
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(Dimens.spacingXs))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
    }
}

@Composable
fun FormSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(Dimens.spacingSm))
        content()
    }
}

@Composable
fun InfoCollectionContent(
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        content = content
    )
}

@Composable
fun StepNavigationButtons(
    onNextClick: () -> Unit,
    onSkipClick: () -> Unit,
    nextEnabled: Boolean = true,
    nextText: String = "下一步",
    showSkip: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.spacingMd),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
    ) {
        if (showSkip) {
            SmartButton(
                text = "跳过",
                onClick = onSkipClick,
                type = ButtonType.Text,
                modifier = Modifier.weight(1f)
            )
        }
        
        SmartButton(
            text = nextText,
            onClick = onNextClick,
            type = ButtonType.Primary,
            enabled = nextEnabled,
            modifier = Modifier.weight(if (showSkip) 1f else 1f)
        )
    }
}
