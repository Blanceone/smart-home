package com.smarthome.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smarthome.presentation.common.components.PrimaryButton
import com.smarthome.presentation.common.theme.*

@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToInfoCollection: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var agreedToTerms by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            val user = uiState.user
            if (user?.isNewUser == true) {
                onNavigateToInfoCollection()
            } else {
                onNavigateToHome()
            }
        }
    }

    val isLoading = uiState.isLoading
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Primary,
                            PrimaryLight.copy(alpha = 0.8f)
                        )
                    )
                )
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(Dimens.horizontalPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))
            
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(Dimens.cornerXLarge))
                    .background(Surface.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = null,
                    modifier = Modifier.size(50.dp),
                    tint = TextOnPrimary
                )
            }
            
            Spacer(modifier = Modifier.height(Dimens.spacingLg))
            
            Text(
                text = "智能家居方案定制",
                style = MaterialTheme.typography.headlineMedium,
                color = TextOnPrimary,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(Dimens.spacingSm))
            
            Text(
                text = "AI 为您定制专属智能家居方案",
                style = MaterialTheme.typography.bodyLarge,
                color = TextOnPrimary.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(80.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(Dimens.cornerLarge),
                colors = CardDefaults.cardColors(containerColor = Surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.spacingLg),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "欢迎使用",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary
                    )
                    
                    Spacer(modifier = Modifier.height(Dimens.spacingXl))
                    
                    PrimaryButton(
                        text = "微信一键登录",
                        onClick = { viewModel.register() },
                        enabled = agreedToTerms && !isLoading,
                        isLoading = isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(Dimens.spacingMd))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = agreedToTerms,
                            onCheckedChange = { agreedToTerms = it },
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        val annotatedString = buildAnnotatedString {
                            append("我已阅读并同意")
                            pushStringAnnotation(tag = "privacy", annotation = "privacy")
                            withStyle(style = SpanStyle(color = Primary, textDecoration = TextDecoration.Underline)) {
                                append("《隐私政策》")
                            }
                            pop()
                            append("和")
                            pushStringAnnotation(tag = "agreement", annotation = "agreement")
                            withStyle(style = SpanStyle(color = Primary, textDecoration = TextDecoration.Underline)) {
                                append("《用户协议》")
                            }
                            pop()
                        }
                        Text(
                            text = annotatedString,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                    
                    uiState.error?.let { error ->
                        Spacer(modifier = Modifier.height(Dimens.spacingSm))
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodySmall,
                            color = Error
                        )
                    }
                }
            }
        }
    }
}
