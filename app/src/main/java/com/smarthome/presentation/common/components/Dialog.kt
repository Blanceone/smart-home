package com.smarthome.presentation.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.smarthome.presentation.common.theme.*

enum class DialogType {
    INFO,
    SUCCESS,
    WARNING,
    ERROR,
    CONFIRM
}

@Composable
fun SmartDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    title: String,
    message: String? = null,
    type: DialogType = DialogType.INFO,
    icon: ImageVector? = null,
    confirmText: String = "确定",
    dismissText: String? = "取消",
    onConfirm: (() -> Unit)? = null,
    onDismissRequest: () -> Unit = onDismiss
) {
    if (show) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.spacingLg),
                shape = RoundedCornerShape(Dimens.cornerLarge)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.spacingXl),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val (defaultIcon, iconColor) = when (type) {
                        DialogType.INFO -> Icons.Default.Info to Primary
                        DialogType.SUCCESS -> Icons.Default.CheckCircle to Success
                        DialogType.WARNING -> Icons.Default.Warning to Warning
                        DialogType.ERROR -> Icons.Default.Error to Error
                        DialogType.CONFIRM -> Icons.Default.HelpOutline to Primary
                    }

                    Box(
                        modifier = Modifier
                            .size(56.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon ?: defaultIcon,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = iconColor
                        )
                    }

                    Spacer(modifier = Modifier.height(Dimens.spacingMd))

                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )

                    message?.let {
                        Spacer(modifier = Modifier.height(Dimens.spacingSm))
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(Dimens.spacingLg))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
                    ) {
                        dismissText?.let {
                            SmartButton(
                                text = it,
                                onClick = onDismiss,
                                type = ButtonType.Outline,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        SmartButton(
                            text = confirmText,
                            onClick = {
                                onConfirm?.invoke()
                                onDismiss()
                            },
                            type = ButtonType.Primary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ConfirmDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    title: String,
    message: String? = null,
    confirmText: String = "确定",
    dismissText: String = "取消"
) {
    SmartDialog(
        show = show,
        onDismiss = onDismiss,
        title = title,
        message = message,
        type = DialogType.CONFIRM,
        confirmText = confirmText,
        dismissText = dismissText,
        onConfirm = onConfirm
    )
}

@Composable
fun DeleteConfirmDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    itemName: String = "此项目"
) {
    ConfirmDialog(
        show = show,
        onDismiss = onDismiss,
        onConfirm = onConfirm,
        title = "确认删除",
        message = "确定要删除$itemName吗？此操作无法撤销。",
        confirmText = "删除",
        dismissText = "取消"
    )
}

@Composable
fun SuccessDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    title: String,
    message: String? = null
) {
    SmartDialog(
        show = show,
        onDismiss = onDismiss,
        title = title,
        message = message,
        type = DialogType.SUCCESS,
        confirmText = "好的"
    )
}

@Composable
fun ErrorDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    title: String = "出错了",
    message: String? = null
) {
    SmartDialog(
        show = show,
        onDismiss = onDismiss,
        title = title,
        message = message,
        type = DialogType.ERROR,
        confirmText = "知道了"
    )
}

@Composable
fun InfoDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    title: String,
    message: String? = null
) {
    SmartDialog(
        show = show,
        onDismiss = onDismiss,
        title = title,
        message = message,
        type = DialogType.INFO,
        confirmText = "知道了"
    )
}

@Composable
fun ActionSheetDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    title: String? = null,
    items: List<ActionSheetItem>
) {
    if (show) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.spacingLg),
                shape = RoundedCornerShape(Dimens.cornerLarge)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Dimens.spacingMd)
                ) {
                    title?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Dimens.spacingLg, vertical = Dimens.spacingSm),
                            textAlign = TextAlign.Center
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = Dimens.spacingSm),
                            color = Divider
                        )
                    }

                    items.forEach { item ->
                        TextButton(
                            onClick = {
                                item.onClick()
                                onDismiss()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = Dimens.spacingLg, vertical = Dimens.spacingSm),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                item.icon?.let {
                                    Icon(
                                        imageVector = it,
                                        contentDescription = null,
                                        tint = item.iconTint ?: TextPrimary,
                                        modifier = Modifier.size(Dimens.iconSizeMd)
                                    )
                                    Spacer(modifier = Modifier.width(Dimens.spacingMd))
                                }
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = item.textColor ?: TextPrimary
                                )
                            }
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = Dimens.spacingSm),
                        color = Divider
                    )

                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "取消",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextSecondary,
                            modifier = Modifier.padding(vertical = Dimens.spacingSm)
                        )
                    }
                }
            }
        }
    }
}

data class ActionSheetItem(
    val label: String,
    val icon: ImageVector? = null,
    val iconTint: androidx.compose.ui.graphics.Color? = null,
    val textColor: androidx.compose.ui.graphics.Color? = null,
    val onClick: () -> Unit
)
