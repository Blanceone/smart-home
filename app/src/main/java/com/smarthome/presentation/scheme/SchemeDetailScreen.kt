package com.smarthome.presentation.scheme

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.smarthome.data.remote.dto.SchemeDeviceDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchemeDetailScreen(
    onBack: () -> Unit,
    onNavigateToDeviceDetail: (String) -> Unit = {},
    viewModel: SchemeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }
    var showRatingDialog by remember { mutableStateOf(false) }
    var hasRated by remember { mutableStateOf(false) }
    val tabs = listOf("装修说明", "设备清单")
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("方案详情") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { showRatingDialog = true }) {
                        Icon(Icons.Default.Star, contentDescription = "评价")
                    }
                    IconButton(onClick = { viewModel.saveScheme() }) {
                        Icon(Icons.Default.BookmarkBorder, contentDescription = "保存")
                    }
                    IconButton(onClick = { 
                        val state = uiState
                        if (state is SchemeUiState.Success) {
                            val shareText = buildString {
                                append("智能家居方案推荐\n")
                                append("方案名称：${state.scheme.name}\n")
                                append("预算：¥${state.scheme.budget.toInt()}\n")
                                append("设备数量：${state.scheme.devices?.size ?: 0}件\n")
                                state.scheme.decorationGuide?.summary?.let {
                                    append("\n${it}")
                                }
                                append("\n\n由智能家居方案定制APP生成")
                            }
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            context.startActivity(Intent.createChooser(intent, "分享方案"))
                        }
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "分享")
                    }
                    IconButton(onClick = { viewModel.exportScheme() }) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = "导出")
                    }
                }
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is SchemeUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is SchemeUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onBack) {
                            Text("返回")
                        }
                    }
                }
            }
            is SchemeUiState.Success -> {
                val scheme = state.scheme
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "方案概览",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("总预算", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                Text(
                                    "¥${scheme.budget.toInt()}",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("设备数量", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                Text("${scheme.devices?.size ?: 0} 件")
                            }
                            if (state.isSaved) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("保存状态", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                    Text("已保存", color = MaterialTheme.colorScheme.primary)
                                }
                            }
                            state.exportInfo?.let { exportInfo ->
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "PDF已生成: ${exportInfo.fileName}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    TabRow(selectedTabIndex = selectedTab) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = { Text(title) }
                            )
                        }
                    }

                    when (selectedTab) {
                        0 -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                item {
                                    Text(
                                        text = "装修建议",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp)
                                        ) {
                                            Text(
                                                text = scheme.decorationGuide?.summary ?: "暂无装修建议",
                                                fontSize = 14.sp,
                                                lineHeight = 20.sp
                                            )
                                        }
                                    }
                                }
                                scheme.decorationGuide?.rooms?.let { rooms ->
                                    if (rooms.isNotEmpty()) {
                                        item {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "各房间建议",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp
                                            )
                                        }
                                        items(rooms) { room ->
                                            Card(
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Column(
                                                    modifier = Modifier.padding(16.dp)
                                                ) {
                                                    Text(
                                                        text = room.name,
                                                        fontWeight = FontWeight.Medium,
                                                        fontSize = 15.sp
                                                    )
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    room.layout?.let {
                                                        Text(
                                                            text = "布局建议: $it",
                                                            fontSize = 13.sp,
                                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                                        )
                                                    }
                                                    room.devices?.let {
                                                        Text(
                                                            text = "推荐设备: ${it.joinToString(", ")}",
                                                            fontSize = 13.sp,
                                                            color = MaterialTheme.colorScheme.primary
                                                        )
                                                    }
                                                    room.installationPoints?.let {
                                                        if (it.isNotEmpty()) {
                                                            Text(
                                                                text = "安装要点: ${it.joinToString(", ")}",
                                                                fontSize = 12.sp,
                                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                scheme.decorationGuide?.professionalAdvice?.let { advice ->
                                    item {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.primaryContainer
                                            )
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(16.dp)
                                            ) {
                                                Text(
                                                    text = "专业建议",
                                                    fontWeight = FontWeight.Medium,
                                                    fontSize = 14.sp
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = advice,
                                                    fontSize = 13.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        1 -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(scheme.devices ?: emptyList()) { device ->
                                    DeviceItem(
                                        device = device,
                                        onClick = { onNavigateToDeviceDetail(device.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DeviceItem(
    device: SchemeDeviceDto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Devices,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = device.name,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = device.brand ?: "未知品牌",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = "¥${device.price.toInt()}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
    }

    if (showRatingDialog && uiState is SchemeUiState.Success) {
        RatingDialog(
            schemeId = (uiState as SchemeUiState.Success).scheme.id,
            onDismiss = { showRatingDialog = false },
            onRatingSubmitted = { rating, content ->
                viewModel.submitRating(rating, content)
                hasRated = true
                showRatingDialog = false
            }
        )
    }
}

@Composable
private fun RatingDialog(
    schemeId: String,
    onDismiss: () -> Unit,
    onRatingSubmitted: (Int, String?) -> Unit
) {
    var rating by remember { mutableIntStateOf(0) }
    var content by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text("方案评价")
        },
        text = {
            Column {
                Text(
                    text = "您对这个方案满意吗？",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    (1..5).forEach { index ->
                        Icon(
                            imageVector = if (index <= rating) Icons.Default.Star else Icons.Default.StarOutline,
                            contentDescription = "第$index星",
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { rating = index },
                            tint = if (index <= rating) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    placeholder = { Text("请输入您的评价（选填）") },
                    maxLines = 4
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onRatingSubmitted(rating, content.ifBlank { null }) },
                enabled = rating > 0
            ) {
                Text("提交")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
