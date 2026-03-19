package com.smarthome.presentation.info

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smarthome.domain.repository.SchemeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val schemeRepository: SchemeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BudgetUiState())
    val uiState = _uiState.asStateFlow()

    fun updateBudget(budget: Double) {
        _uiState.value = _uiState.value.copy(budget = budget)
    }

    suspend fun generateScheme(): Result<com.smarthome.data.remote.dto.SchemeDto> {
        return schemeRepository.generateScheme(_uiState.value.budget, false)
    }
}

data class BudgetUiState(
    val budget: Double = 0.0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    onNavigateToGenerating: (Double) -> Unit,
    onBack: () -> Unit,
    viewModel: BudgetViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var budgetText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val budgetOptions = listOf(5000, 10000, 15000, 20000, 30000, 50000)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("预算设置") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("设置您的预算", fontSize = 20.sp, fontWeight = FontWeight.Bold)

            Text("您的预算金额将用于生成最适合您的智能家居方案", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))

            Text("快速选择", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                budgetOptions.forEach { budget ->
                    FilterChip(
                        selected = uiState.budget == budget.toDouble(),
                        onClick = {
                            viewModel.updateBudget(budget.toDouble())
                            budgetText = budget.toString()
                        },
                        label = { Text("${budget / 10000}万") }
                    )
                }
            }

            Text("自定义金额", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = budgetText,
                onValueChange = {
                    budgetText = it
                    viewModel.updateBudget(it.toDoubleOrNull() ?: 0.0)
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("请输入预算金额（元）") },
                keyboardOptions = KeyboardOptions(keyType = KeyboardType.Number),
                singleLine = true,
                leadingIcon = { Text("¥") }
            )

            if (uiState.budget > 0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("预算概览", fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("预算金额：¥${String.format("%,.0f", uiState.budget)}")
                        Text("预计可配置：${(uiState.budget / 1000).toInt()}+ 智能设备")
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    isLoading = true
                    kotlinx.coroutines.GlobalScope.launch {
                        val result = viewModel.generateScheme()
                        isLoading = false
                        if (result.isSuccess) {
                            onNavigateToGenerating(uiState.budget)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && uiState.budget > 0
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("生成方案")
                }
            }
        }
    }
}
