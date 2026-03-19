package com.smarthome.presentation.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.smarthome.presentation.common.theme.*

enum class EmptyStateType {
    NO_SCHEMES,
    NO_DEVICES,
    NO_RESULTS,
    NO_NETWORK,
    ERROR,
    CUSTOM
}

@Composable
fun EmptyState(
    modifier: Modifier = Modifier,
    type: EmptyStateType = EmptyStateType.NO_RESULTS,
    title: String? = null,
    message: String? = null,
    icon: ImageVector? = null,
    action: @Composable (() -> Unit)? = null
) {
    val (defaultIcon, defaultTitle, defaultMessage) = when (type) {
        EmptyStateType.NO_SCHEMES -> Triple(
            Icons.Default.Description,
            "暂无方案",
            "您还没有保存任何方案，快去生成一个吧"
        )
        EmptyStateType.NO_DEVICES -> Triple(
            Icons.Default.Devices,
            "暂无设备",
            "当前方案暂无推荐设备"
        )
        EmptyStateType.NO_RESULTS -> Triple(
            Icons.Default.SearchOff,
            "暂无结果",
            "没有找到相关内容"
        )
        EmptyStateType.NO_NETWORK -> Triple(
            Icons.Default.WifiOff,
            "网络异常",
            "请检查网络连接后重试"
        )
        EmptyStateType.ERROR -> Triple(
            Icons.Default.ErrorOutline,
            "出错了",
            "加载失败，请稍后重试"
        )
        EmptyStateType.CUSTOM -> Triple(
            Icons.Default.Info,
            title ?: "暂无数据",
            message ?: ""
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimens.spacingXl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon ?: defaultIcon,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = TextTertiary.copy(alpha = 0.5f)
            )
        }
        
        Spacer(modifier = Modifier.height(Dimens.spacingMd))
        
        Text(
            text = title ?: defaultTitle,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        
        if ((message ?: defaultMessage).isNotEmpty()) {
            Spacer(modifier = Modifier.height(Dimens.spacingSm))
            Text(
                text = message ?: defaultMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
        
        if (action != null) {
            Spacer(modifier = Modifier.height(Dimens.spacingLg))
            action()
        }
    }
}

@Composable
fun EmptyStateWithAction(
    title: String,
    message: String,
    buttonText: String,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Inbox,
    type: EmptyStateType = EmptyStateType.CUSTOM
) {
    EmptyState(
        modifier = modifier,
        type = type,
        title = title,
        message = message,
        icon = icon,
        action = {
            SmartButton(
                text = buttonText,
                onClick = onButtonClick,
                type = ButtonType.Primary
            )
        }
    )
}

@Composable
fun NoSchemesEmptyState(
    onGenerateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmptyStateWithAction(
        title = "暂无方案",
        message = "您还没有保存任何方案\n点击下方按钮开始生成",
        buttonText = "生成新方案",
        onButtonClick = onGenerateClick,
        modifier = modifier,
        icon = Icons.Default.Description,
        type = EmptyStateType.NO_SCHEMES
    )
}

@Composable
fun NoDevicesEmptyState(
    modifier: Modifier = Modifier
) {
    EmptyState(
        modifier = modifier,
        type = EmptyStateType.NO_DEVICES
    )
}

@Composable
fun NetworkErrorState(
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmptyStateWithAction(
        title = "网络异常",
        message = "请检查网络连接后重试",
        buttonText = "重试",
        onButtonClick = onRetryClick,
        modifier = modifier,
        icon = Icons.Default.WifiOff,
        type = EmptyStateType.NO_NETWORK
    )
}

@Composable
fun ErrorState(
    message: String = "加载失败，请稍后重试",
    onRetryClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    EmptyState(
        modifier = modifier,
        type = EmptyStateType.ERROR,
        message = message,
        action = if (onRetryClick != null) {
            {
                SmartButton(
                    text = "重试",
                    onClick = onRetryClick,
                    type = ButtonType.Outline
                )
            }
        } else null
    )
}
