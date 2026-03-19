package com.smarthome.presentation.info

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smarthome.presentation.common.components.PrimaryButton
import com.smarthome.presentation.common.components.StepIndicator
import com.smarthome.presentation.common.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoCollectionScreen(
    onNavigateToHouseLayout: () -> Unit,
    onNavigateToGenerating: () -> Unit,
    onBack: () -> Unit,
    viewModel: InfoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isCompleted) {
        if (uiState.isCompleted) {
            onNavigateToGenerating()
        }
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("信息采集") },
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
            StepIndicator(
                currentStep = uiState.currentStep,
                totalSteps = uiState.totalSteps,
                modifier = Modifier.padding(Dimens.horizontalPadding)
            )

            when (uiState.currentStep) {
                1 -> BasicInfoStep(
                    data = uiState.basicInfo,
                    onNext = { viewModel.setBasicInfo(it) }
                )
                2 -> LifestyleStep(
                    data = uiState.lifestyle,
                    onNext = { viewModel.setLifestyle(it) },
                    onBack = { viewModel.goToStep(1) }
                )
                3 -> DeviceExperienceStep(
                    data = uiState.deviceExperience,
                    onNext = { viewModel.setDeviceExperience(it) },
                    onBack = { viewModel.goToStep(2) }
                )
                4 -> AestheticPreferenceStep(
                    data = uiState.aestheticPreference,
                    onNext = { viewModel.setAestheticPreference(it) },
                    onBack = { viewModel.goToStep(3) }
                )
                5 -> BrandPreferenceStep(
                    data = uiState.brandPreference,
                    onNext = { viewModel.setBrandPreference(it) },
                    onBack = { viewModel.goToStep(4) }
                )
                6 -> HouseLayoutStep(
                    data = uiState.houseLayout,
                    onNext = { viewModel.setHouseLayout(it) },
                    onBack = { viewModel.goToStep(5) }
                )
                7 -> BudgetStep(
                    budget = uiState.budget,
                    isLoading = uiState.isLoading,
                    error = uiState.error,
                    onSubmit = { budget ->
                        viewModel.setBudget(budget)
                        viewModel.saveAllAndGenerate()
                    },
                    onBack = { viewModel.goToStep(6) }
                )
            }
        }
    }
}

@Composable
private fun BasicInfoStep(
    data: BasicInfoData?,
    onNext: (BasicInfoData) -> Unit
) {
    var age by remember { mutableStateOf(data?.age ?: "") }
    var occupation by remember { mutableStateOf(data?.occupation ?: "") }
    var city by remember { mutableStateOf(data?.city ?: "") }
    var familyMembers by remember { mutableStateOf(data?.familyMembers ?: emptySet()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.horizontalPadding)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
    ) {
        Text(
            text = "基本信息",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = age,
            onValueChange = { age = it },
            label = { Text("年龄段") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = occupation,
            onValueChange = { occupation = it },
            label = { Text("职业") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("所在城市") },
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "家庭成员",
            style = MaterialTheme.typography.titleMedium
        )

        val memberOptions = listOf("独居", "夫妻", "有小孩", "有老人", "有宠物")
        memberOptions.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = familyMembers.contains(option),
                    onCheckedChange = { checked ->
                        familyMembers = if (checked) {
                            familyMembers + option
                        } else {
                            familyMembers - option
                        }
                    }
                )
                Text(option)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        PrimaryButton(
            text = "下一步",
            onClick = {
                onNext(BasicInfoData(age, occupation, familyMembers, city))
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun LifestyleStep(
    data: LifestyleData?,
    onNext: (LifestyleData) -> Unit,
    onBack: () -> Unit
) {
    var sleepPattern by remember { mutableStateOf(data?.sleepPattern) }
    var homeActivities by remember { mutableStateOf(data?.homeActivities ?: emptySet()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.horizontalPadding)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
    ) {
        Text(
            text = "生活习惯",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "作息时间",
            style = MaterialTheme.typography.titleMedium
        )

        val sleepOptions = listOf("早睡早起", "晚睡晚起", "作息不规律")
        sleepOptions.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = sleepPattern == option,
                    onClick = { sleepPattern = option }
                )
                Text(option)
            }
        }

        Text(
            text = "居家活动",
            style = MaterialTheme.typography.titleMedium
        )

        val activityOptions = listOf("工作", "娱乐", "健身", "烹饪", "阅读")
        activityOptions.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = homeActivities.contains(option),
                    onCheckedChange = { checked ->
                        homeActivities = if (checked) {
                            homeActivities + option
                        } else {
                            homeActivities - option
                        }
                    }
                )
                Text(option)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f)
            ) {
                Text("上一步")
            }
            PrimaryButton(
                text = "下一步",
                onClick = {
                    onNext(LifestyleData(sleepPattern, homeActivities))
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun DeviceExperienceStep(
    data: DeviceExperienceData?,
    onNext: (DeviceExperienceData) -> Unit,
    onBack: () -> Unit
) {
    var knowledgeLevel by remember { mutableStateOf(data?.knowledgeLevel) }
    var usedDevices by remember { mutableStateOf(data?.usedDevices ?: emptySet()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.horizontalPadding)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
    ) {
        Text(
            text = "设备使用经验",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "智能家居了解程度",
            style = MaterialTheme.typography.titleMedium
        )

        val levelOptions = listOf("完全不了解", "了解一些", "比较熟悉", "非常熟悉")
        levelOptions.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = knowledgeLevel == option,
                    onClick = { knowledgeLevel = option }
                )
                Text(option)
            }
        }

        Text(
            text = "使用过的智能设备",
            style = MaterialTheme.typography.titleMedium
        )

        val deviceOptions = listOf("智能音箱", "智能灯", "智能插座", "智能门锁", "智能摄像头", "扫地机器人")
        deviceOptions.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = usedDevices.contains(option),
                    onCheckedChange = { checked ->
                        usedDevices = if (checked) {
                            usedDevices + option
                        } else {
                            usedDevices - option
                        }
                    }
                )
                Text(option)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f)
            ) {
                Text("上一步")
            }
            PrimaryButton(
                text = "下一步",
                onClick = {
                    onNext(DeviceExperienceData(knowledgeLevel, usedDevices))
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun AestheticPreferenceStep(
    data: AestheticPreferenceData?,
    onNext: (AestheticPreferenceData) -> Unit,
    onBack: () -> Unit
) {
    var decorStyle by remember { mutableStateOf(data?.decorStyle) }
    var colorPreferences by remember { mutableStateOf(data?.colorPreferences ?: emptySet()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.horizontalPadding)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
    ) {
        Text(
            text = "审美偏好",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "装修风格",
            style = MaterialTheme.typography.titleMedium
        )

        val styleOptions = listOf("现代简约", "北欧风", "中式", "日式", "工业风", "轻奢")
        styleOptions.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = decorStyle == option,
                    onClick = { decorStyle = option }
                )
                Text(option)
            }
        }

        Text(
            text = "颜色偏好",
            style = MaterialTheme.typography.titleMedium
        )

        val colorOptions = listOf("白色", "黑色", "灰色", "木色", "金色", "蓝色")
        colorOptions.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = colorPreferences.contains(option),
                    onCheckedChange = { checked ->
                        colorPreferences = if (checked) {
                            colorPreferences + option
                        } else {
                            colorPreferences - option
                        }
                    }
                )
                Text(option)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f)
            ) {
                Text("上一步")
            }
            PrimaryButton(
                text = "下一步",
                onClick = {
                    onNext(AestheticPreferenceData(decorStyle, colorPreferences))
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun BrandPreferenceStep(
    data: BrandPreferenceData?,
    onNext: (BrandPreferenceData) -> Unit,
    onBack: () -> Unit
) {
    var preferredBrands by remember { mutableStateOf(data?.preferredBrands ?: emptySet()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.horizontalPadding)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
    ) {
        Text(
            text = "品牌偏好",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "选择您偏好的品牌（可多选）",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        val brandOptions = listOf("小米", "华为", "苹果", "海尔", "美的", "飞利浦", "欧普", "涂鸦")
        brandOptions.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = preferredBrands.contains(option),
                    onCheckedChange = { checked ->
                        preferredBrands = if (checked) {
                            preferredBrands + option
                        } else {
                            preferredBrands - option
                        }
                    }
                )
                Text(option)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f)
            ) {
                Text("上一步")
            }
            PrimaryButton(
                text = "下一步",
                onClick = {
                    onNext(BrandPreferenceData(preferredBrands))
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun HouseLayoutStep(
    data: HouseLayoutData?,
    onNext: (HouseLayoutData) -> Unit,
    onBack: () -> Unit
) {
    var houseType by remember { mutableStateOf(data?.houseType) }
    var totalArea by remember { mutableStateOf(data?.totalArea?.toString() ?: "") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.horizontalPadding)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
    ) {
        Text(
            text = "户型信息",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "户型类型",
            style = MaterialTheme.typography.titleMedium
        )

        val typeOptions = listOf("一室一厅", "两室一厅", "三室一厅", "三室两厅", "四室及以上", "复式", "别墅")
        typeOptions.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = houseType == option,
                    onClick = { houseType = option }
                )
                Text(option)
            }
        }

        OutlinedTextField(
            value = totalArea,
            onValueChange = { totalArea = it },
            label = { Text("建筑面积（平方米）") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f)
            ) {
                Text("上一步")
            }
            PrimaryButton(
                text = "下一步",
                onClick = {
                    onNext(HouseLayoutData(houseType, totalArea.toDoubleOrNull()))
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun BudgetStep(
    budget: Double?,
    isLoading: Boolean,
    error: String?,
    onSubmit: (Double) -> Unit,
    onBack: () -> Unit
) {
    var budgetValue by remember { mutableStateOf(budget?.toString() ?: "") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.horizontalPadding)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
    ) {
        Text(
            text = "预算设置",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "请设置您的智能家居预算",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        OutlinedTextField(
            value = budgetValue,
            onValueChange = { budgetValue = it },
            label = { Text("预算金额（元）") },
            modifier = Modifier.fillMaxWidth(),
            prefix = { Text("¥") }
        )

        Text(
            text = "建议预算范围：5000 - 50000 元",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        error?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = Error
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f)
            ) {
                Text("上一步")
            }
            PrimaryButton(
                text = "生成方案",
                onClick = {
                    budgetValue.toDoubleOrNull()?.let { onSubmit(it) }
                },
                isLoading = isLoading,
                enabled = budgetValue.isNotBlank(),
                modifier = Modifier.weight(1f)
            )
        }
    }
}
