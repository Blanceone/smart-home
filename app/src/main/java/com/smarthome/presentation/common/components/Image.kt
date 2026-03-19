package com.smarthome.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.smarthome.presentation.common.theme.*

@Composable
fun SmartImage(
    modifier: Modifier = Modifier,
    url: String? = null,
    painter: Painter? = null,
    contentDescription: String? = null,
    placeholder: ImageVector = Icons.Default.Image,
    error: ImageVector = Icons.Default.BrokenImage,
    contentScale: ContentScale = ContentScale.Crop,
    showLoading: Boolean = true
) {
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Dimens.cornerMedium))
            .background(SurfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        when {
            url != null -> {
                // TODO: Use Coil or Glide for actual image loading
                // For now, show placeholder
                if (showLoading && isLoading) {
                    ShimmerLoading(
                        modifier = Modifier.fillMaxSize()
                    )
                }
                
                if (hasError) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = error,
                            contentDescription = null,
                            modifier = Modifier.size(Dimens.iconSizeLg),
                            tint = TextTertiary
                        )
                        Spacer(modifier = Modifier.height(Dimens.spacingXs))
                        Text(
                            text = "加载失败",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextTertiary
                        )
                    }
                }
            }
            painter != null -> {
                androidx.compose.foundation.Image(
                    painter = painter,
                    contentDescription = contentDescription,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = contentScale
                )
            }
            else -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = placeholder,
                        contentDescription = null,
                        modifier = Modifier.size(Dimens.iconSizeLg),
                        tint = TextTertiary
                    )
                }
            }
        }
    }
}

@Composable
fun ImageCard(
    modifier: Modifier = Modifier,
    url: String? = null,
    painter: Painter? = null,
    title: String? = null,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(Dimens.cornerMedium)
    ) {
        Column {
            SmartImage(
                url = url,
                painter = painter,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )
            
            if (title != null || subtitle != null) {
                Column(
                    modifier = Modifier.padding(Dimens.spacingSm)
                ) {
                    title?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.titleSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    subtitle?.let {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DeviceImageCard(
    modifier: Modifier = Modifier,
    deviceName: String,
    brand: String? = null,
    price: Double? = null,
    url: String? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .width(140.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(Dimens.cornerMedium)
    ) {
        Column {
            SmartImage(
                url = url,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            )
            
            Column(
                modifier = Modifier.padding(Dimens.spacingSm)
            ) {
                Text(
                    text = deviceName,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                brand?.let {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        maxLines = 1
                    )
                }
                
                price?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "¥${String.format("%.0f", it)}",
                        style = MaterialTheme.typography.titleSmall,
                        color = Primary,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun AvatarImage(
    modifier: Modifier = Modifier,
    url: String? = null,
    name: String = "",
    size: androidx.compose.ui.unit.Dp = 48.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(Dimens.cornerFull))
            .background(Primary.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
    ) {
        if (url != null) {
            SmartImage(
                url = url,
                modifier = Modifier.fillMaxSize(),
                showLoading = false
            )
        } else {
            Text(
                text = name.take(1).ifEmpty { "用" },
                style = MaterialTheme.typography.titleMedium,
                color = Primary,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
        }
    }
}

@Composable
fun ImagePlaceholder(
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Image,
    text: String? = null
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Dimens.cornerMedium))
            .background(SurfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(Dimens.iconSizeLg),
                tint = TextTertiary
            )
            text?.let {
                Spacer(modifier = Modifier.height(Dimens.spacingSm))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary
                )
            }
        }
    }
}
