package com.smarthome.presentation.info

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.smarthome.presentation.common.components.*
import com.smarthome.presentation.common.theme.*

data class StyleOption(
    val id: String,
    val label: String,
    val emoji: String,
    val description: String
)

@Composable
fun AestheticPreferenceScreen(
    initialData: AestheticPreferenceData = AestheticPreferenceData(),
    onNextClick: (AestheticPreferenceData) -> Unit,
    onSkipClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var decorStyle by remember { mutableStateOf(initialData.decorStyle) }
    var colorPreferences by remember { mutableStateOf(initialData.colorPreferences) }
    
    val styleOptions = listOf(
        StyleOption("modern", "现代简约", "🏠", "简洁线条，功能至上"),
        StyleOption("nordic", "北欧风", "🌲", "自然材质，清新淡雅"),
        StyleOption("japanese", "日式", "🏯", "禅意空间，原木质感"),
        StyleOption("industrial", "工业风", "🏭", "金属元素，复古粗犷"),
        StyleOption("chinese", "中式", "🏮", "传统韵味，典雅大气"),
        StyleOption("other", "其他", "✨", "独特风格")
    )
    
    val colorOptions = listOf(
        ChipOption("white", "白色系"),
        ChipOption("gray", "灰色系"),
        ChipOption("wood", "原木色"),
        ChipOption("black", "黑色系"),
        ChipOption("colorful", "彩色系")
    )
    
    InfoCollectionScreen(
        currentStep = 4,
        onBackClick = onBackClick
    ) {
        StepTitle(
            title = stepContents[3].title,
            subtitle = stepContents[3].subtitle
        )
        
        Spacer(modifier = Modifier.height(Dimens.spacingXl))
        
        FormSection(title = "您喜欢的装修风格") {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
            ) {
                items(styleOptions) { option ->
                    StyleCard(
                        option = option,
                        selected = decorStyle == option.id,
                        onClick = { decorStyle = option.id }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(Dimens.spacingLg))
        
        FormSection(title = "颜色偏好（可多选）") {
            VerticalChipGroup(
                options = colorOptions,
                selectedIds = colorPreferences,
                onSelectionChange = { colorPreferences = it },
                singleSelection = false
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        StepNavigationButtons(
            onNextClick = {
                onNextClick(
                    AestheticPreferenceData(
                        decorStyle = decorStyle,
                        colorPreferences = colorPreferences
                    )
                )
            },
            onSkipClick = onSkipClick
        )
    }
}

@Composable
private fun StyleCard(
    option: StyleOption,
    selected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(Dimens.cornerLarge)
    
    Box(
        modifier = Modifier
            .aspectRatio(1.2f)
            .clip(shape)
            .background(
                color = if (selected) Primary.copy(alpha = 0.1f) else SurfaceVariant
            )
            .then(
                if (selected) {
                    Modifier.background(
                        color = Primary.copy(alpha = 0.05f),
                        shape = shape
                    )
                } else Modifier
            )
            .clip(shape)
            .then(
                Modifier.padding(1.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.spacingMd),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = option.emoji,
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(Dimens.spacingXs))
            Text(
                text = option.label,
                style = MaterialTheme.typography.titleSmall,
                color = if (selected) Primary else TextPrimary
            )
            Spacer(modifier = Modifier.height(Dimens.spacingXs))
            Text(
                text = option.description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
        
        if (selected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(Dimens.spacingSm)
                    .size(Dimens.iconSizeSm)
            )
        }
    }
}
