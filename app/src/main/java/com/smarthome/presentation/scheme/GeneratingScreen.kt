package com.smarthome.presentation.scheme

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarthome.presentation.common.theme.*
import kotlinx.coroutines.delay

data class GenerationStep(
    val text: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val progress: Float
)

@Composable
fun GeneratingScreen(
    onNavigateToSchemeDetail: (String) -> Unit,
    onBack: () -> Unit
) {
    var progress by remember { mutableFloatStateOf(0f) }
    var currentStepIndex by remember { mutableIntStateOf(0) }
    var isComplete by remember { mutableStateOf(false) }

    val steps = listOf(
        GenerationStep("正在分析您的需求...", Icons.Default.Person, 0.2f),
        GenerationStep("匹配智能家居设备...", Icons.Default.Devices, 0.4f),
        GenerationStep("生成装修建议...", Icons.Default.Home, 0.6f),
        GenerationStep("优化方案配置...", Icons.Default.Tune, 0.8f),
        GenerationStep("方案生成完成！", Icons.Default.CheckCircle, 1.0f)
    )

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    LaunchedEffect(Unit) {
        steps.forEachIndexed { index, step ->
            currentStepIndex = index
            val targetProgress = step.progress
            while (progress < targetProgress) {
                progress += 0.02f
                delay(80)
            }
            delay(400)
        }
        
        isComplete = true
        delay(800)
        onNavigateToSchemeDetail("scheme_demo")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Background,
                        Primary.copy(alpha = 0.05f),
                        Background
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.spacingXl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = !isComplete,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.size(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .scale(pulseScale)
                                .clip(RoundedCornerShape(Dimens.cornerFull))
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Primary.copy(alpha = 0.2f),
                                            Primary.copy(alpha = 0.05f)
                                        )
                                    )
                                )
                        )

                        CircularProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.size(120.dp),
                            strokeWidth = 6.dp,
                            color = Primary,
                            trackColor = Primary.copy(alpha = 0.1f)
                        )

                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .rotate(rotation),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = Primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(Dimens.spacingXl))

                    Text(
                        text = "AI 方案生成中",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(Dimens.spacingMd))

                    steps.forEachIndexed { index, step ->
                        AnimatedVisibility(
                            visible = currentStepIndex >= index,
                            enter = slideInVertically() + fadeIn(),
                            exit = slideOutVertically() + fadeOut()
                        ) {
                            StepItem(
                                step = step,
                                isActive = currentStepIndex == index,
                                isComplete = currentStepIndex > index || (currentStepIndex == index && progress >= step.progress)
                            )
                            Spacer(modifier = Modifier.height(Dimens.spacingSm))
                        }
                    }

                    Spacer(modifier = Modifier.height(Dimens.spacingLg))

                    Text(
                        text = "${(progress * 100).toInt()}%",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                }
            }

            AnimatedVisibility(
                visible = isComplete,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(Dimens.cornerFull))
                            .background(Success.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Success
                        )
                    }

                    Spacer(modifier = Modifier.height(Dimens.spacingLg))

                    Text(
                        text = "方案生成完成！",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(Dimens.spacingSm))

                    Text(
                        text = "正在跳转到方案详情...",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(Dimens.spacingLg),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "由 DeepSeek AI 提供支持",
                fontSize = 12.sp,
                color = TextTertiary
            )
        }
    }
}

@Composable
private fun StepItem(
    step: GenerationStep,
    isActive: Boolean,
    isComplete: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
    ) {
        Icon(
            imageVector = if (isComplete) Icons.Default.Check else step.icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = when {
                isComplete -> Success
                isActive -> Primary
                else -> TextTertiary
            }
        )
        Text(
            text = step.text,
            fontSize = 14.sp,
            color = when {
                isComplete -> Success
                isActive -> TextPrimary
                else -> TextTertiary
            }
        )
    }
}

private fun Brush.radialGradient(
    colors: List<Color>,
    center: androidx.compose.ui.geometry.Offset = androidx.compose.ui.geometry.Offset.Unspecified
): Brush {
    return Brush.radialGradient(
        colors = colors,
        center = center
    )
}
