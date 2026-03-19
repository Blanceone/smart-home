package com.smarthome.presentation.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.smarthome.presentation.common.theme.*

enum class ButtonType {
    Primary, Secondary, Outline, Text, Gradient
}

enum class ButtonSize {
    Small, Medium, Large
}

@Composable
fun SmartButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    type: ButtonType = ButtonType.Primary,
    size: ButtonSize = ButtonSize.Medium,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null
) {
    val height = when (size) {
        ButtonSize.Small -> Dimens.buttonHeightSm
        ButtonSize.Medium -> Dimens.buttonHeightMd
        ButtonSize.Large -> Dimens.buttonHeightLg
    }

    val shape = RoundedCornerShape(Dimens.cornerMedium)

    when (type) {
        ButtonType.Primary -> {
            Button(
                onClick = onClick,
                modifier = modifier.height(height),
                enabled = enabled && !isLoading,
                shape = shape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary,
                    contentColor = TextOnPrimary,
                    disabledContainerColor = Primary.copy(alpha = 0.5f),
                    disabledContentColor = TextOnPrimary.copy(alpha = 0.7f)
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 4.dp
                )
            ) {
                ButtonContent(text, isLoading, leadingIcon, trailingIcon)
            }
        }
        ButtonType.Secondary -> {
            Button(
                onClick = onClick,
                modifier = modifier.height(height),
                enabled = enabled && !isLoading,
                shape = shape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Secondary,
                    contentColor = TextOnSecondary,
                    disabledContainerColor = Secondary.copy(alpha = 0.5f),
                    disabledContentColor = TextOnSecondary.copy(alpha = 0.7f)
                )
            ) {
                ButtonContent(text, isLoading, leadingIcon, trailingIcon)
            }
        }
        ButtonType.Outline -> {
            OutlinedButton(
                onClick = onClick,
                modifier = modifier.height(height),
                enabled = enabled && !isLoading,
                shape = shape,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Primary,
                    disabledContentColor = Primary.copy(alpha = 0.5f)
                ),
                border = ButtonDefaults.outlinedButtonBorder(enabled = enabled)
            ) {
                ButtonContent(text, isLoading, leadingIcon, trailingIcon)
            }
        }
        ButtonType.Text -> {
            TextButton(
                onClick = onClick,
                modifier = modifier.height(height),
                enabled = enabled && !isLoading
            ) {
                ButtonContent(text, isLoading, leadingIcon, trailingIcon)
            }
        }
        ButtonType.Gradient -> {
            Button(
                onClick = onClick,
                modifier = modifier.height(height),
                enabled = enabled && !isLoading,
                shape = shape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary,
                    contentColor = TextOnPrimary
                )
            ) {
                ButtonContent(text, isLoading, leadingIcon, trailingIcon)
            }
        }
    }
}

@Composable
private fun RowScope.ButtonContent(
    text: String,
    isLoading: Boolean,
    leadingIcon: ImageVector?,
    trailingIcon: ImageVector?
) {
    if (isLoading) {
        CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            color = TextOnPrimary,
            strokeWidth = 2.dp
        )
    } else {
        leadingIcon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                modifier = Modifier.size(Dimens.iconSizeSm)
            )
            Spacer(modifier = Modifier.width(Dimens.spacingXs))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center
        )
        trailingIcon?.let {
            Spacer(modifier = Modifier.width(Dimens.spacingXs))
            Icon(
                imageVector = it,
                contentDescription = null,
                modifier = Modifier.size(Dimens.iconSizeSm)
            )
        }
    }
}

@Composable
fun WechatLoginButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.buttonHeightLg),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(Dimens.cornerLarge),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF07C160),
            contentColor = TextOnPrimary,
            disabledContainerColor = Color(0xFF07C160).copy(alpha = 0.5f)
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = TextOnPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Chat,
                contentDescription = null,
                modifier = Modifier.size(Dimens.iconSizeMd)
            )
            Spacer(modifier = Modifier.width(Dimens.spacingSm))
            Text(
                text = "微信登录",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
