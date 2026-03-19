package com.smarthome.presentation.info

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smarthome.data.remote.dto.HouseLayoutDto
import com.smarthome.data.remote.dto.RoomDto
import com.smarthome.data.remote.dto.RoomRequest
import com.smarthome.data.remote.dto.SaveHouseLayoutRequest
import com.smarthome.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HouseLayoutViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HouseLayoutUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadHouseLayout()
    }

    private fun loadHouseLayout() {
        viewModelScope.launch {
            val result = userRepository.getHouseLayout()
            if (result.isSuccess && result.getOrNull() != null) {
                val layout = result.getOrNull()!!
                _uiState.value = HouseLayoutUiState(
                    houseType = layout.houseType ?: "",
                    totalArea = layout.totalArea ?: 0.0,
                    rooms = layout.rooms ?: emptyList()
                )
            }
        }
    }

    fun updateHouseType(houseType: String) {
        _uiState.value = _uiState.value.copy(houseType = houseType)
    }

    fun updateTotalArea(area: Double) {
        _uiState.value = _uiState.value.copy(totalArea = area)
    }

    fun addRoom(room: RoomDto) {
        _uiState.value = _uiState.value.copy(
            rooms = _uiState.value.rooms + room
        )
    }

    fun removeRoom(index: Int) {
        _uiState.value = _uiState.value.copy(
            rooms = _uiState.value.rooms.toMutableList().apply { removeAt(index) }
        )
    }

    fun updateRoom(index: Int, room: RoomDto) {
        _uiState.value = _uiState.value.copy(
            rooms = _uiState.value.rooms.toMutableList().apply { set(index, room) }
        )
    }

    suspend fun saveHouseLayout(): Result<HouseLayoutDto> {
        val state = _uiState.value
        val request = SaveHouseLayoutRequest(
            houseType = state.houseType,
            totalArea = state.totalArea,
            rooms = state.rooms.map { RoomRequest(it.name, it.area, it.specialNeeds) }
        )
        return userRepository.saveHouseLayout(request)
    }
}

data class HouseLayoutUiState(
    val houseType: String = "",
    val totalArea: Double = 0.0,
    val rooms: List<RoomDto> = emptyList()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HouseLayoutScreen(
    onNavigateToBudget: () -> Unit,
    onBack: () -> Unit,
    viewModel: HouseLayoutViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isLoading by remember { mutableStateOf(false) }
    var showAddRoomDialog by remember { mutableStateOf(false) }

    val houseTypeOptions = listOf("一居室", "两居室", "三居室", "四居室及以上", "复式/别墅")
    val roomOptions = listOf("客厅", "主卧", "次卧", "书房", "厨房", "卫生间", "阳台", "其他")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("户型设置") },
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
            Text("户型设置", fontSize = 20.sp, fontWeight = FontWeight.Bold)

            Text("房屋类型", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                houseTypeOptions.forEach { type ->
                    FilterChip(
                        selected = uiState.houseType == type,
                        onClick = { viewModel.updateHouseType(type) },
                        label = { Text(type, fontSize = 12.sp) }
                    )
                }
            }

            Text("建筑面积（平方米）", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = if (uiState.totalArea > 0) uiState.totalArea.toInt().toString() else "",
                onValueChange = { viewModel.updateTotalArea(it.toDoubleOrNull() ?: 0.0) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("请输入建筑面积") },
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("房间列表", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                TextButton(onClick = { showAddRoomDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Text("添加房间")
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.rooms) { room ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(room.name, fontWeight = FontWeight.Medium)
                                Text("${room.area}平方米", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                if (!room.specialNeeds.isNullOrBlank()) {
                                    Text(room.specialNeeds, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                            IconButton(onClick = { viewModel.removeRoom(uiState.rooms.indexOf(room)) }) {
                                Icon(Icons.Default.Delete, contentDescription = "删除")
                            }
                        }
                    }
                }
            }

            Button(
                onClick = {
                    isLoading = true
                    kotlinx.coroutines.GlobalScope.launch {
                        val result = viewModel.saveHouseLayout()
                        isLoading = false
                        if (result.isSuccess) {
                            onNavigateToBudget()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && uiState.houseType.isNotEmpty() && uiState.totalArea > 0 && uiState.rooms.isNotEmpty()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("下一步")
                }
            }
        }
    }

    if (showAddRoomDialog) {
        AddRoomDialog(
            roomOptions = roomOptions,
            onDismiss = { showAddRoomDialog = false },
            onAdd = { room ->
                viewModel.addRoom(room)
                showAddRoomDialog = false
            }
        )
    }
}

@Composable
private fun AddRoomDialog(
    roomOptions: List<String>,
    onDismiss: () -> Unit,
    onAdd: (RoomDto) -> Unit
) {
    var selectedRoom by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var specialNeeds by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("添加房间") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("房间类型", fontSize = 14.sp)
                Column {
                    roomOptions.forEach { room ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            RadioButton(
                                selected = selectedRoom == room,
                                onClick = { selectedRoom = room }
                            )
                            Text(room)
                        }
                    }
                }

                OutlinedTextField(
                    value = area,
                    onValueChange = { area = it },
                    label = { Text("面积（平方米）") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = specialNeeds,
                    onValueChange = { specialNeeds = it },
                    label = { Text("特殊需求（选填）") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedRoom.isNotEmpty() && area.isNotEmpty()) {
                        onAdd(RoomDto(
                            name = selectedRoom,
                            area = area.toDoubleOrNull() ?: 0.0,
                            specialNeeds = specialNeeds.ifBlank { null }
                        ))
                    }
                },
                enabled = selectedRoom.isNotEmpty() && area.isNotEmpty()
            ) {
                Text("添加")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
