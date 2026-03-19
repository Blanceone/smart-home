package com.smarthome.presentation.screens.info

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoCollectionScreen(
    onComplete: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: InfoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.generatedSchemeId) {
        uiState.generatedSchemeId?.let { schemeId ->
            onComplete(schemeId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "信息采集 (${uiState.currentStep + 1}/${uiState.totalSteps})"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LinearProgressIndicator(
                progress = { (uiState.currentStep + 1).toFloat() / uiState.totalSteps },
                modifier = Modifier.fillMaxWidth()
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                when (uiState.currentStep) {
                    0 -> BasicInfoStep(
                        data = uiState.basicInfo,
                        onUpdate = { viewModel.updateBasicInfo(it) }
                    )
                    1 -> LifestyleStep(
                        data = uiState.lifestyle,
                        onUpdate = { viewModel.updateLifestyle(it) }
                    )
                    2 -> DeviceExperienceStep(
                        data = uiState.deviceExperience,
                        onUpdate = { viewModel.updateDeviceExperience(it) }
                    )
                    3 -> AestheticPreferenceStep(
                        data = uiState.aestheticPreference,
                        onUpdate = { viewModel.updateAestheticPreference(it) }
                    )
                    4 -> BrandPreferenceStep(
                        data = uiState.brandPreference,
                        onUpdate = { viewModel.updateBrandPreference(it) }
                    )
                    5 -> HouseLayoutStep(
                        data = uiState.houseLayout,
                        onUpdate = { viewModel.updateHouseLayout(it) }
                    )
                    6 -> BudgetStep(
                        budget = uiState.budget,
                        isLoading = uiState.isGenerating,
                        onUpdate = { viewModel.updateBudget(it) },
                        onGenerate = { viewModel.generateScheme() }
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (uiState.currentStep > 0) {
                    OutlinedButton(
                        onClick = { viewModel.previousStep() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("上一步")
                    }
                }

                if (uiState.currentStep < uiState.totalSteps - 1) {
                    Button(
                        onClick = { viewModel.nextStep() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("下一步")
                    }
                }
            }
        }
    }
}

@Composable
fun BasicInfoStep(
    data: BasicInfoData,
    onUpdate: (BasicInfoData) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("了解一下您的基本情况", style = MaterialTheme.typography.titleLarge)

        Text("年龄段", style = MaterialTheme.typography.titleSmall)
        val ageOptions = listOf("18-25", "26-30", "31-35", "35+")
        ageOptions.forEach { age ->
            FilterChip(
                selected = data.age == age,
                onClick = { onUpdate(data.copy(age = age)) },
                label = { Text(age) }
            )
        }

        Text("职业", style = MaterialTheme.typography.titleSmall)
        val occupationOptions = listOf("学生", "上班族", "自由职业", "其他")
        occupationOptions.forEach { occupation ->
            FilterChip(
                selected = data.occupation == occupation,
                onClick = { onUpdate(data.copy(occupation = occupation)) },
                label = { Text(occupation) }
            )
        }

        OutlinedTextField(
            value = data.city,
            onValueChange = { onUpdate(data.copy(city = it)) },
            label = { Text("居住城市") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}

@Composable
fun LifestyleStep(
    data: LifestyleData,
    onUpdate: (LifestyleData) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("您的生活习惯是怎样的？", style = MaterialTheme.typography.titleLarge)

        Text("作息时间", style = MaterialTheme.typography.titleSmall)
        val sleepOptions = listOf("早睡早起", "晚睡晚起", "作息不规律")
        sleepOptions.forEach { sleep ->
            FilterChip(
                selected = data.sleepPattern == sleep,
                onClick = { onUpdate(data.copy(sleepPattern = sleep)) },
                label = { Text(sleep) }
            )
        }
    }
}

@Composable
fun DeviceExperienceStep(
    data: DeviceExperienceData,
    onUpdate: (DeviceExperienceData) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("您对智能设备了解多少？", style = MaterialTheme.typography.titleLarge)

        Text("了解程度", style = MaterialTheme.typography.titleSmall)
        val levelOptions = listOf("完全不了解", "听说过但没用过", "用过一些", "非常熟悉")
        levelOptions.forEach { level ->
            FilterChip(
                selected = data.knowledgeLevel == level,
                onClick = { onUpdate(data.copy(knowledgeLevel = level)) },
                label = { Text(level) }
            )
        }
    }
}

@Composable
fun AestheticPreferenceStep(
    data: AestheticPreferenceData,
    onUpdate: (AestheticPreferenceData) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("您喜欢什么样的风格？", style = MaterialTheme.typography.titleLarge)

        Text("装修风格", style = MaterialTheme.typography.titleSmall)
        val styleOptions = listOf("现代简约", "北欧风", "日式", "工业风", "中式", "其他")
        styleOptions.forEach { style ->
            FilterChip(
                selected = data.decorStyle == style,
                onClick = { onUpdate(data.copy(decorStyle = style)) },
                label = { Text(style) }
            )
        }
    }
}

@Composable
fun BrandPreferenceStep(
    data: BrandPreferenceData,
    onUpdate: (BrandPreferenceData) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("您有偏好的品牌吗？", style = MaterialTheme.typography.titleLarge)

        val brandOptions = listOf("小米/米家", "华为", "天猫精灵", "小度", "Apple HomeKit", "无偏好")
        brandOptions.forEach { brand ->
            FilterChip(
                selected = data.preferredBrands.contains(brand),
                onClick = {
                    val newBrands = if (data.preferredBrands.contains(brand)) {
                        data.preferredBrands - brand
                    } else {
                        data.preferredBrands + brand
                    }
                    onUpdate(data.copy(preferredBrands = newBrands))
                },
                label = { Text(brand) }
            )
        }
    }
}

@Composable
fun HouseLayoutStep(
    data: HouseLayoutData,
    onUpdate: (HouseLayoutData) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("您的家是什么样的？", style = MaterialTheme.typography.titleLarge)

        Text("房屋类型", style = MaterialTheme.typography.titleSmall)
        val houseTypeOptions = listOf("一居室", "两居室", "三居室", "四居室及以上", "复式/别墅")
        houseTypeOptions.forEach { type ->
            FilterChip(
                selected = data.houseType == type,
                onClick = { onUpdate(data.copy(houseType = type)) },
                label = { Text(type) }
            )
        }

        OutlinedTextField(
            value = data.totalArea?.toString() ?: "",
            onValueChange = { 
                val area = it.toDoubleOrNull()
                onUpdate(data.copy(totalArea = area))
            },
            label = { Text("建筑面积 (平方米)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}

@Composable
fun BudgetStep(
    budget: Double?,
    isLoading: Boolean,
    onUpdate: (Double) -> Unit,
    onGenerate: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("您的预算是多少？", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = budget?.toInt()?.toString() ?: "",
            onValueChange = { 
                val amount = it.toDoubleOrNull()
                amount?.let { onUpdate(it) }
            },
            label = { Text("预算金额 (元)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Text(
            text = "系统将根据您的预算推荐最合适的设备组合",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onGenerate,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = budget != null && budget > 0 && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("生成方案")
            }
        }
    }
}

data class BasicInfoData(
    val age: String = "",
    val occupation: String = "",
    val city: String = ""
)

data class LifestyleData(
    val sleepPattern: String = ""
)

data class DeviceExperienceData(
    val knowledgeLevel: String = ""
)

data class AestheticPreferenceData(
    val decorStyle: String = ""
)

data class BrandPreferenceData(
    val preferredBrands: List<String> = emptyList()
)

data class HouseLayoutData(
    val houseType: String = "",
    val totalArea: Double? = null
)
