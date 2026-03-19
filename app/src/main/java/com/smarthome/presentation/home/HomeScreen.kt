package com.smarthome.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smarthome.presentation.common.components.SmartBottomNavBar
import com.smarthome.presentation.common.theme.*
import com.smarthome.presentation.navigation.BottomNavItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToInfoCollection: () -> Unit,
    onNavigateToMySchemes: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToSchemeDetail: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "智能家居方案定制",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Surface
                ),
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.Person, contentDescription = "个人中心")
                    }
                }
            )
        },
        bottomBar = {
            SmartBottomNavBar(
                items = listOf(
                    BottomNavItem.Home,
                    BottomNavItem.MySchemes,
                    BottomNavItem.Profile
                ),
                currentRoute = BottomNavItem.Home.route,
                onNavigate = { route ->
                    when (route) {
                        BottomNavItem.MySchemes.route -> onNavigateToMySchemes()
                        BottomNavItem.Profile.route -> onNavigateToProfile()
                    }
                }
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is HomeUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is HomeUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(Dimens.spacingMd))
                        Button(onClick = { viewModel.loadData() }) {
                            Text("重试")
                        }
                    }
                }
            }
            is HomeUiState.Success -> {
                val user = state.user
                val recentSchemes = state.recentSchemes

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(Dimens.horizontalPadding),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(Dimens.cornerLarge),
                        colors = CardDefaults.cardColors(containerColor = PrimaryLight.copy(alpha = 0.1f))
                    ) {
                        Column(
                            modifier = Modifier.padding(Dimens.spacingLg)
                        ) {
                            Text(
                                text = "欢迎回来，${user?.nickname ?: "用户"}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(Dimens.spacingSm))
                            Text(
                                text = "让 AI 为您定制专属智能家居方案",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(Dimens.spacingMd))
                            Button(
                                onClick = onNavigateToInfoCollection,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(Dimens.spacingSm))
                                Text("生成新方案")
                            }
                        }
                    }

                    if (recentSchemes.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(Dimens.spacingSm))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "最近方案",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            TextButton(onClick = onNavigateToMySchemes) {
                                Text("查看全部")
                                Icon(
                                    Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        recentSchemes.forEach { scheme ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(Dimens.cornerMedium),
                                onClick = { onNavigateToSchemeDetail(scheme.id) }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(Dimens.spacingMd),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Description,
                                        contentDescription = null,
                                        modifier = Modifier.size(40.dp),
                                        tint = Primary
                                    )
                                    Spacer(modifier = Modifier.width(Dimens.spacingMd))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "方案 #${scheme.id.take(6)}",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "预算: ¥${scheme.totalBudget.toInt()}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary
                                        )
                                    }
                                    Icon(
                                        Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        tint = TextSecondary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(Dimens.spacingMd))

                    Text(
                        text = "功能介绍",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
                    ) {
                        FeatureCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Home,
                            title = "户型设置",
                            description = "配置房间信息"
                        )
                        FeatureCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.AutoAwesome,
                            title = "智能推荐",
                            description = "AI生成方案"
                        )
                    }

                    Spacer(modifier = Modifier.height(Dimens.spacingMd))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
                    ) {
                        FeatureCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.ShoppingCart,
                            title = "设备清单",
                            description = "查看推荐设备"
                        )
                        FeatureCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Share,
                            title = "分享方案",
                            description = "导出PDF"
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(Dimens.spacingXl))
                }
            }
        }
    }
}

@Composable
private fun FeatureCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    description: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(Dimens.cornerMedium)
    ) {
        Column(
            modifier = Modifier.padding(Dimens.spacingMd),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = Primary
            )
            Spacer(modifier = Modifier.height(Dimens.spacingSm))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}
