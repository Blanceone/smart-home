package com.smarthome.presentation.feedback

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smarthome.presentation.common.components.*
import com.smarthome.presentation.common.theme.*

data class FeedbackTypeOption(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackScreen(
    onBack: () -> Unit,
    viewModel: FeedbackViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedType by remember { mutableStateOf<FeedbackTypeOption?>(null) }
    var feedbackContent by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }

    val feedbackTypes = listOf(
        FeedbackTypeOption("suggestion", "功能建议", Icons.Default.Lightbulb, "希望增加什么新功能"),
        FeedbackTypeOption("bug", "问题反馈", Icons.Default.BugReport, "遇到什么问题或Bug"),
        FeedbackTypeOption("scheme", "方案评价", Icons.Default.Star, "对生成的方案进行评价"),
        FeedbackTypeOption("data", "数据纠错", Icons.Default.Edit, "设备信息或价格有误"),
        FeedbackTypeOption("other", "其他", Icons.Default.MoreHoriz, "其他意见或建议")
    )

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            selectedType = null
            feedbackContent = ""
            contact = ""
        }
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("意见反馈") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(Dimens.horizontalPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
        ) {
            AnimatedVisibility(
                visible = uiState.isSuccess,
                enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.slideInVertically(),
                exit = androidx.compose.animation.fadeOut()
            ) {
                SuccessFeedbackCard()
            }

            Text(
                text = "选择反馈类型",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
            ) {
                feedbackTypes.forEach { type ->
                    FeedbackTypeCard(
                        type = type,
                        selected = selectedType == type,
                        onClick = { selectedType = type }
                    )
                }
            }

            Text(
                text = "反馈内容",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = feedbackContent,
                onValueChange = { feedbackContent = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                placeholder = { 
                    Text(
                        when (selectedType?.id) {
                            "suggestion" -> "请描述您希望增加的功能..."
                            "bug" -> "请描述遇到的问题..."
                            "scheme" -> "请描述您对方案的评价..."
                            "data" -> "请描述需要纠正的信息..."
                            else -> "请详细描述您的反馈..."
                        }
                    )
                },
                shape = RoundedCornerShape(Dimens.cornerMedium),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = Border,
                    cursorColor = Primary
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "${feedbackContent.length}/500",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (feedbackContent.length > 500) Error else TextTertiary
                )
            }

            Text(
                text = "联系方式（选填）",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = contact,
                onValueChange = { contact = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("邮箱或手机号，方便我们回复您") },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        Icons.Default.ContactPhone,
                        contentDescription = null,
                        tint = TextSecondary
                    )
                },
                shape = RoundedCornerShape(Dimens.cornerMedium),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = Border,
                    cursorColor = Primary
                )
            )

            uiState.error?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Error.copy(alpha = 0.1f))
                ) {
                    Row(
                        modifier = Modifier.padding(Dimens.spacingMd),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = Error,
                            modifier = Modifier.size(Dimens.iconSizeSm)
                        )
                        Spacer(modifier = Modifier.width(Dimens.spacingSm))
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodySmall,
                            color = Error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimens.spacingMd))

            PrimaryButton(
                text = "提交反馈",
                onClick = {
                    selectedType?.let { type ->
                        viewModel.submitFeedback(type.label, feedbackContent, contact.ifBlank { null })
                    }
                },
                isLoading = uiState.isLoading,
                enabled = selectedType != null && feedbackContent.isNotBlank() && feedbackContent.length <= 500,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Dimens.spacingLg))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(Dimens.spacingMd)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(Dimens.iconSizeSm)
                        )
                        Spacer(modifier = Modifier.width(Dimens.spacingSm))
                        Text(
                            text = "反馈说明",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(Dimens.spacingSm))
                    Text(
                        text = "我们会认真对待每一条反馈，并在1-3个工作日内处理。如需紧急联系，请拨打客服电话：400-XXX-XXXX",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.spacingXl))
        }
    }
}

@Composable
private fun FeedbackTypeCard(
    type: FeedbackTypeOption,
    selected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(Dimens.cornerMedium)
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                color = if (selected) Primary.copy(alpha = 0.05f) else Surface
            )
            .then(
                if (selected) {
                    Modifier.background(
                        color = Primary.copy(alpha = 0.1f),
                        shape = shape
                    )
                } else {
                    Modifier
                }
            )
            .clip(shape)
            .background(
                color = if (selected) Primary.copy(alpha = 0.05f) else Surface
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacingMd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(Dimens.cornerSmall))
                    .background(
                        color = if (selected) Primary.copy(alpha = 0.1f) else SurfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    type.icon,
                    contentDescription = null,
                    tint = if (selected) Primary else TextSecondary,
                    modifier = Modifier.size(Dimens.iconSizeMd)
                )
            }
            
            Spacer(modifier = Modifier.width(Dimens.spacingMd))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = type.label,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    color = if (selected) Primary else TextPrimary
                )
                Text(
                    text = type.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            
            RadioButton(
                selected = selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = Primary,
                    unselectedColor = TextTertiary
                )
            )
        }
    }
}

@Composable
private fun SuccessFeedbackCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Success.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(Dimens.cornerMedium)
    ) {
        Row(
            modifier = Modifier.padding(Dimens.spacingMd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(Dimens.cornerFull))
                    .background(Success.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = Success,
                    modifier = Modifier.size(Dimens.iconSizeMd)
                )
            }
            Spacer(modifier = Modifier.width(Dimens.spacingMd))
            Column {
                Text(
                    text = "提交成功",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Success
                )
                Text(
                    text = "感谢您的反馈！我们会认真处理。",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}
