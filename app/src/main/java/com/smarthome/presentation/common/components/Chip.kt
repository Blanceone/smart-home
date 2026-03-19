package com.smarthome.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.smarthome.presentation.common.theme.*

data class ChipOption(
    val id: String,
    val label: String,
    val icon: String? = null
)

@Composable
fun SmartChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val shape = RoundedCornerShape(Dimens.cornerFull)
    
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                color = if (selected) Primary.copy(alpha = 0.1f) else SurfaceVariant
            )
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = if (selected) Primary else Border,
                shape = shape
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = Dimens.spacingMd, vertical = Dimens.spacingSm),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) Primary else TextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun SmartChipGroup(
    options: List<ChipOption>,
    selectedIds: Set<String>,
    onSelectionChange: (Set<String>) -> Unit,
    modifier: Modifier = Modifier,
    singleSelection: Boolean = false,
    enabled: Boolean = true
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
    ) {
        items(options) { option ->
            SmartChip(
                label = option.label,
                selected = option.id in selectedIds,
                onClick = {
                    if (singleSelection) {
                        onSelectionChange(setOf(option.id))
                    } else {
                        val newSelection = selectedIds.toMutableSet()
                        if (option.id in newSelection) {
                            newSelection.remove(option.id)
                        } else {
                            newSelection.add(option.id)
                        }
                        onSelectionChange(newSelection)
                    }
                },
                enabled = enabled
            )
        }
    }
}

@Composable
fun VerticalChipGroup(
    options: List<ChipOption>,
    selectedIds: Set<String>,
    onSelectionChange: (Set<String>) -> Unit,
    modifier: Modifier = Modifier,
    singleSelection: Boolean = false,
    enabled: Boolean = true
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
    ) {
        options.forEach { option ->
            SmartChip(
                label = option.label,
                selected = option.id in selectedIds,
                onClick = {
                    if (singleSelection) {
                        onSelectionChange(setOf(option.id))
                    } else {
                        val newSelection = selectedIds.toMutableSet()
                        if (option.id in newSelection) {
                            newSelection.remove(option.id)
                        } else {
                            newSelection.add(option.id)
                        }
                        onSelectionChange(newSelection)
                    }
                },
                enabled = enabled,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun SelectionCard(
    title: String,
    subtitle: String? = null,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: String? = null
) {
    val shape = RoundedCornerShape(Dimens.cornerLarge)
    
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                color = if (selected) Primary.copy(alpha = 0.05f) else Surface
            )
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) Primary else Border,
                shape = shape
            )
            .clickable(onClick = onClick)
            .padding(Dimens.spacingMd),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.let {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = if (selected) Primary.copy(alpha = 0.1f) else SurfaceVariant,
                            shape = RoundedCornerShape(Dimens.cornerMedium)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
                Spacer(modifier = Modifier.width(Dimens.spacingMd))
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (selected) Primary else TextPrimary
                )
                subtitle?.let {
                    Spacer(modifier = Modifier.height(Dimens.spacingXs))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
            
            if (selected) {
                Spacer(modifier = Modifier.width(Dimens.spacingSm))
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(Dimens.iconSizeMd)
                )
            }
        }
    }
}
