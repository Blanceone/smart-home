package com.smarthome.presentation.info

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.smarthome.presentation.common.components.*
import com.smarthome.presentation.common.theme.*

@Composable
fun BrandPreferenceScreen(
    initialData: BrandPreferenceData = BrandPreferenceData(),
    onNextClick: (BrandPreferenceData) -> Unit,
    onSkipClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var preferredBrands by remember { mutableStateOf(initialData.preferredBrands) }
    
    val brandOptions = listOf(
        ChipOption("xiaomi", "小米/米家"),
        ChipOption("huawei", "华为"),
        ChipOption("tmall", "天猫精灵"),
        ChipOption("xiaodu", "小度"),
        ChipOption("apple", "Apple HomeKit"),
        ChipOption("other", "其他"),
        ChipOption("none", "无偏好")
    )
    
    InfoCollectionScreen(
        currentStep = 5,
        onBackClick = onBackClick
    ) {
        StepTitle(
            title = stepContents[4].title,
            subtitle = stepContents[4].subtitle
        )
        
        Spacer(modifier = Modifier.height(Dimens.spacingXl))
        
        InfoCollectionContent {
            FormSection(title = "品牌偏好（可多选）") {
                VerticalChipGroup(
                    options = brandOptions,
                    selectedIds = preferredBrands,
                    onSelectionChange = { preferredBrands = it },
                    singleSelection = false
                )
            }
            
            Spacer(modifier = Modifier.height(Dimens.spacingLg))
            
            BrandInfoTip()
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        StepNavigationButtons(
            onNextClick = {
                onNextClick(
                    BrandPreferenceData(
                        preferredBrands = preferredBrands
                    )
                )
            },
            onSkipClick = onSkipClick
        )
    }
}

@Composable
private fun BrandInfoTip() {
    SmartCard(
        backgroundColor = Info.copy(alpha = 0.1f)
    ) {
        Row(
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Info,
                contentDescription = null,
                tint = Info,
                modifier = Modifier.size(Dimens.iconSizeMd)
            )
            Spacer(modifier = Modifier.width(Dimens.spacingSm))
            Column {
                Text(
                    text = "品牌选择建议",
                    style = MaterialTheme.typography.titleSmall,
                    color = Info
                )
                Spacer(modifier = Modifier.height(Dimens.spacingXs))
                Text(
                    text = "不同品牌的智能设备生态不同，选择同一品牌的产品可以获得更好的联动体验。如果不确定，可以选择"无偏好"，我们会推荐性价比最高的组合。",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}
