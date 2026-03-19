package com.smarthome.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.smarthome.presentation.common.theme.*

@Composable
fun StepProgressIndicator(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.horizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingXs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..totalSteps) {
            val isCompleted = i < currentStep
            val isCurrent = i == currentStep
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        color = when {
                            isCompleted -> Primary
                            isCurrent -> Primary.copy(alpha = 0.5f)
                            else -> Border
                        }
                    )
            )
        }
    }
}

@Composable
fun StepIndicatorWithLabels(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        StepProgressIndicator(
            currentStep = currentStep,
            totalSteps = totalSteps
        )
        Spacer(modifier = Modifier.height(Dimens.spacingSm))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.horizontalPadding),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (i in 1..totalSteps) {
                val isCompleted = i < currentStep
                val isCurrent = i == currentStep
                
                Text(
                    text = "$i/$totalSteps",
                    style = MaterialTheme.typography.labelSmall,
                    color = when {
                        isCompleted -> Primary
                        isCurrent -> Primary
                        else -> TextTertiary
                    }
                )
            }
        }
    }
}

@Composable
fun VerticalStepIndicator(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier,
    stepLabels: List<String> = emptyList()
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingNone)
    ) {
        for (i in 1..totalSteps) {
            val isCompleted = i < currentStep
            val isCurrent = i == currentStep
            
            Row(
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(Dimens.cornerFull))
                            .background(
                                color = when {
                                    isCompleted -> Primary
                                    isCurrent -> Primary
                                    else -> SurfaceVariant
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.Check,
                                contentDescription = null,
                                tint = TextOnPrimary,
                                modifier = Modifier.size(Dimens.iconSizeSm)
                            )
                        } else {
                            Text(
                                text = i.toString(),
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isCurrent) TextOnPrimary else TextSecondary
                            )
                        }
                    }
                    
                    if (i < totalSteps) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(40.dp)
                                .background(
                                    color = if (isCompleted) Primary else Border
                                )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(Dimens.spacingMd))
                
                if (stepLabels.isNotEmpty() && i - 1 < stepLabels.size) {
                    Text(
                        text = stepLabels[i - 1],
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isCurrent) TextPrimary else TextSecondary
                    )
                }
            }
        }
    }
}
