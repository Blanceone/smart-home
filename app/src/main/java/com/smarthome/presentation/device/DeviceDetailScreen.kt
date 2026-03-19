package com.smarthome.presentation.device

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smarthome.presentation.common.components.*
import com.smarthome.presentation.common.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceDetailScreen(
    onBack: () -> Unit,
    viewModel: DeviceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showPurchaseDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.purchaseUrl) {
        if (uiState.purchaseUrl != null) {
            showPurchaseDialog = true
        }
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("设备详情") },
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
        when (val state = uiState) {
            is DeviceUiState.Loading -> {
                FullScreenLoading(
                    message = "加载设备信息...",
                    modifier = Modifier.padding(padding)
                )
            }
            is DeviceUiState.Error -> {
                ErrorState(
                    message = state.error ?: "加载失败",
                    onRetryClick = { viewModel.loadDeviceDetail("") },
                    modifier = Modifier.padding(padding)
                )
            }
            is DeviceUiState.Success -> {
                val device = state.device
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                ) {
                    DeviceHeader(device = device)
                    
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimens.horizontalPadding),
                        verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
                    ) {
                        DevicePriceCard(device = device)

                        device.category?.let { category ->
                            DeviceInfoCard(
                                title = "设备类别",
                                icon = Icons.Default.Category,
                                content = category
                            )
                        }

                        device.description?.let { description ->
                            DeviceInfoCard(
                                title = "产品描述",
                                icon = Icons.Default.Description,
                                content = description
                            )
                        }

                        device.features?.let { features ->
                            if (features.isNotEmpty()) {
                                DeviceFeaturesCard(features = features)
                            }
                        }

                        device.brand?.let { brand ->
                            DeviceInfoCard(
                                title = "品牌信息",
                                icon = Icons.Default.Business,
                                content = brand
                            )
                        }

                        device.recommendReason?.let { reason ->
                            DeviceRecommendCard(reason = reason)
                        }

                        Spacer(modifier = Modifier.height(Dimens.spacingMd))

                        PrimaryButton(
                            text = "查看购买链接",
                            onClick = { viewModel.getPurchaseUrl() },
                            isLoading = uiState.isLoading,
                            leadingIcon = Icons.Default.ShoppingCart,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(Dimens.spacingXl))
                    }
                }
            }
        }
    }

    if (showPurchaseDialog && uiState.purchaseUrl != null) {
        PurchaseUrlDialog(
            url = uiState.purchaseUrl!!.url,
            onDismiss = {
                showPurchaseDialog = false
                viewModel.clearPurchaseUrl()
            }
        )
    }
}

@Composable
private fun DeviceHeader(device: com.smarthome.data.remote.dto.DeviceDto) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Primary.copy(alpha = 0.1f),
                        Background
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(Dimens.cornerLarge))
                    .background(Surface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Devices,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = Primary
                )
            }
            
            Spacer(modifier = Modifier.height(Dimens.spacingMd))
            
            Text(
                text = device.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            device.brand?.let { brand ->
                Spacer(modifier = Modifier.height(Dimens.spacingXs))
                Text(
                    text = brand,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun DevicePriceCard(device: com.smarthome.data.remote.dto.DeviceDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.cornerLarge),
        colors = CardDefaults.cardColors(containerColor = Primary.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacingLg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "参考价格",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(Dimens.spacingXs))
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "¥",
                        style = MaterialTheme.typography.titleMedium,
                        color = Primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = String.format("%.0f", device.price),
                        style = MaterialTheme.typography.headlineMedium,
                        color = Primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Box(
                modifier = Modifier
                    .background(
                        color = Success.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(Dimens.cornerSmall)
                    )
                    .padding(horizontal = Dimens.spacingMd, vertical = Dimens.spacingSm)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Success
                    )
                    Spacer(modifier = Modifier.width(Dimens.spacingXs))
                    Text(
                        text = "有货",
                        style = MaterialTheme.typography.labelMedium,
                        color = Success
                    )
                }
            }
        }
    }
}

@Composable
private fun DeviceInfoCard(
    title: String,
    icon: ImageVector,
    content: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.cornerMedium)
    ) {
        Column(
            modifier = Modifier.padding(Dimens.spacingMd)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.size(Dimens.iconSizeMd),
                    tint = Primary
                )
                Spacer(modifier = Modifier.width(Dimens.spacingSm))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(Dimens.spacingSm))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun DeviceFeaturesCard(features: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.cornerMedium)
    ) {
        Column(
            modifier = Modifier.padding(Dimens.spacingMd)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(Dimens.iconSizeMd),
                    tint = Accent
                )
                Spacer(modifier = Modifier.width(Dimens.spacingSm))
                Text(
                    text = "产品特点",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(Dimens.spacingMd))
            
            features.forEach { feature ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier
                            .size(18.dp)
                            .padding(top = 2.dp),
                        tint = Success
                    )
                    Spacer(modifier = Modifier.width(Dimens.spacingSm))
                    Text(
                        text = feature,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(Dimens.spacingSm))
            }
        }
    }
}

@Composable
private fun DeviceRecommendCard(reason: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.cornerMedium),
        colors = CardDefaults.cardColors(containerColor = Secondary.copy(alpha = 0.05f))
    ) {
        Column(
            modifier = Modifier.padding(Dimens.spacingMd)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.ThumbUp,
                    contentDescription = null,
                    modifier = Modifier.size(Dimens.iconSizeMd),
                    tint = Secondary
                )
                Spacer(modifier = Modifier.width(Dimens.spacingSm))
                Text(
                    text = "推荐理由",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Secondary
                )
            }
            Spacer(modifier = Modifier.height(Dimens.spacingSm))
            Text(
                text = reason,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun PurchaseUrlDialog(
    url: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.ShoppingCart,
                contentDescription = null,
                tint = Primary
            )
        },
        title = {
            Text("购买链接")
        },
        text = {
            Column {
                Text(
                    text = "点击下方按钮跳转到淘宝购买页面",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(Dimens.spacingMd))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Dimens.cornerSmall),
                    color = SurfaceVariant
                ) {
                    Text(
                        text = url,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary,
                        modifier = Modifier.padding(Dimens.spacingSm),
                        maxLines = 2
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                // TODO: Open URL in browser
                onDismiss()
            }) {
                Text("打开链接")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
