import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../core/services/app_state.dart';
import '../../core/constants/app_theme.dart';
import '../../shared/models/house.dart';
import '../../shared/widgets/app_button.dart';
import '../../shared/widgets/app_input.dart';

class HouseInputPage extends StatefulWidget {
  final House? house;

  const HouseInputPage({Key? key, this.house}) : super(key: key);

  @override
  State<HouseInputPage> createState() => _HouseInputPageState();
}

class _HouseInputPageState extends State<HouseInputPage> {
  final _formKey = GlobalKey<FormState>();
  final _totalAreaController = TextEditingController();

  List<Room> _rooms = [];
  bool _isLoading = false;
  bool get _isEditing => widget.house != null;

  @override
  void initState() {
    super.initState();
    if (_isEditing) {
      _totalAreaController.text = widget.house!.totalArea.toString();
      _rooms = List.from(widget.house!.rooms);
    } else {
      _rooms = [
        Room(roomName: '客厅', roomType: RoomType.livingRoom, length: 5.0, width: 4.0),
        Room(roomName: '主卧', roomType: RoomType.masterBedroom, length: 4.0, width: 3.5),
      ];
      _updateTotalArea();
    }
  }

  void _updateTotalArea() {
    double total = 0;
    for (var room in _rooms) {
      total += room.area;
    }
    _totalAreaController.text = total.toStringAsFixed(1);
  }

  void _addRoom() {
    setState(() {
      _rooms.add(Room(
        roomName: '新房间',
        roomType: RoomType.livingRoom,
        length: 4.0,
        width: 3.0,
      ));
    });
    _updateTotalArea();
  }

  void _updateRoom(int index, Room room) {
    setState(() {
      _rooms[index] = room;
    });
    _updateTotalArea();
  }

  void _removeRoom(int index) {
    setState(() {
      _rooms.removeAt(index);
    });
    _updateTotalArea();
  }

  Future<void> _saveHouse() async {
    if (!_formKey.currentState!.validate()) return;

    setState(() => _isLoading = true);

    final appState = context.read<AppState>();
    final totalArea = double.tryParse(_totalAreaController.text) ?? 0;

    if (_isEditing) {
      final updatedHouse = House(
        id: widget.house!.id,
        totalArea: totalArea,
        rooms: _rooms,
        createdAt: widget.house!.createdAt,
      );
      await appState.updateHouse(updatedHouse);
    } else {
      final newHouse = House(
        totalArea: totalArea,
        rooms: _rooms,
      );
      await appState.addHouse(newHouse);
    }

    if (mounted) {
      setState(() => _isLoading = false);
      Navigator.of(context).pop();
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(_isEditing ? '户型更新成功' : '户型创建成功')),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.gray100,
      appBar: AppBar(
        title: Text(_isEditing ? '编辑户型' : '创建户型'),
        backgroundColor: Colors.white,
        foregroundColor: AppColors.gray900,
        elevation: 0,
      ),
      body: Form(
        key: _formKey,
        child: Column(
          children: [
            Expanded(
              child: SingleChildScrollView(
                padding: const EdgeInsets.all(AppSpacing.md),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Container(
                      padding: const EdgeInsets.all(AppSpacing.md),
                      decoration: BoxDecoration(
                        color: Colors.white,
                        borderRadius: BorderRadius.circular(AppRadius.card),
                      ),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text('基本信息', style: AppTextStyles.h4),
                          const SizedBox(height: AppSpacing.md),
                          AppInput(
                            label: '总面积 (㎡)',
                            hint: '请输入总面积',
                            controller: _totalAreaController,
                            keyboardType: TextInputType.number,
                          ),
                        ],
                      ),
                    ),
                    const SizedBox(height: AppSpacing.md),
                    Container(
                      padding: const EdgeInsets.all(AppSpacing.md),
                      decoration: BoxDecoration(
                        color: Colors.white,
                        borderRadius: BorderRadius.circular(AppRadius.card),
                      ),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Row(
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Text('房间信息', style: AppTextStyles.h4),
                              TextButton.icon(
                                onPressed: _addRoom,
                                icon: const Icon(Icons.add, size: 20),
                                label: const Text('添加房间'),
                              ),
                            ],
                          ),
                          const SizedBox(height: AppSpacing.sm),
                          ..._rooms.asMap().entries.map((entry) {
                            final index = entry.key;
                            final room = entry.value;
                            return _buildRoomItem(index, room);
                          }),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
            ),
            Container(
              padding: const EdgeInsets.all(AppSpacing.md),
              decoration: const BoxDecoration(
                color: Colors.white,
                border: Border(
                  top: BorderSide(color: AppColors.gray200),
                ),
              ),
              child: SafeArea(
                child: AppButton(
                  text: '保存户型',
                  onPressed: _saveHouse,
                  isLoading: _isLoading,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildRoomItem(int index, Room room) {
    return Container(
      margin: const EdgeInsets.only(bottom: AppSpacing.md),
      padding: const EdgeInsets.all(AppSpacing.md),
      decoration: BoxDecoration(
        color: AppColors.gray100,
        borderRadius: BorderRadius.circular(AppRadius.button),
      ),
      child: Column(
        children: [
          Row(
            children: [
              Expanded(
                child: DropdownButtonFormField<String>(
                  value: room.roomType,
                  decoration: const InputDecoration(
                    labelText: '房间类型',
                    border: OutlineInputBorder(),
                    contentPadding: EdgeInsets.symmetric(horizontal: 12, vertical: 8),
                  ),
                  items: RoomType.names.entries.map((entry) {
                    return DropdownMenuItem(value: entry.key, child: Text(entry.value));
                  }).toList(),
                  onChanged: (value) {
                    if (value != null) {
                      _updateRoom(index, Room(
                        roomName: RoomType.names[value] ?? '',
                        roomType: value,
                        length: room.length,
                        width: room.width,
                      ));
                    }
                  },
                ),
              ),
              const SizedBox(width: AppSpacing.sm),
              IconButton(
                onPressed: _rooms.length > 1 ? () => _removeRoom(index) : null,
                icon: const Icon(Icons.delete_outline),
                color: AppColors.error,
              ),
            ],
          ),
          const SizedBox(height: AppSpacing.sm),
          Row(
            children: [
              Expanded(
                child: TextFormField(
                  initialValue: room.length.toString(),
                  decoration: const InputDecoration(
                    labelText: '长度 (m)',
                    border: OutlineInputBorder(),
                    contentPadding: EdgeInsets.symmetric(horizontal: 12, vertical: 8),
                  ),
                  keyboardType: TextInputType.number,
                  onChanged: (value) {
                    final length = double.tryParse(value) ?? 0;
                    _updateRoom(index, Room(
                      roomName: room.roomName,
                      roomType: room.roomType,
                      length: length,
                      width: room.width,
                    ));
                  },
                ),
              ),
              const SizedBox(width: AppSpacing.sm),
              Expanded(
                child: TextFormField(
                  initialValue: room.width.toString(),
                  decoration: const InputDecoration(
                    labelText: '宽度 (m)',
                    border: OutlineInputBorder(),
                    contentPadding: EdgeInsets.symmetric(horizontal: 12, vertical: 8),
                  ),
                  keyboardType: TextInputType.number,
                  onChanged: (value) {
                    final width = double.tryParse(value) ?? 0;
                    _updateRoom(index, Room(
                      roomName: room.roomName,
                      roomType: room.roomType,
                      length: room.length,
                      width: width,
                    ));
                  },
                ),
              ),
              const SizedBox(width: AppSpacing.sm),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
                decoration: BoxDecoration(
                  color: AppColors.primary.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(AppRadius.button),
                ),
                child: Text(
                  '${room.area.toStringAsFixed(1)}㎡',
                  style: AppTextStyles.body2.copyWith(color: AppColors.primary),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  @override
  void dispose() {
    _totalAreaController.dispose();
    super.dispose();
  }
}
