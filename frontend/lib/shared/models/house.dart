class House {
  String id;
  double totalArea;
  String? imageUrl;
  DateTime createdAt;
  List<Room> rooms;

  House({
    String? id,
    required this.totalArea,
    this.imageUrl,
    DateTime? createdAt,
    this.rooms = const [],
  })  : id = id ?? DateTime.now().millisecondsSinceEpoch.toString(),
        createdAt = createdAt ?? DateTime.now();

  factory House.fromJson(Map<String, dynamic> json) {
    return House(
      id: json['id']?.toString(),
      totalArea: (json['total_area'] ?? json['totalArea'] ?? 0).toDouble(),
      imageUrl: json['image_url'] ?? json['imageUrl'],
      createdAt: json['created_at'] != null
          ? DateTime.parse(json['created_at'])
          : json['createdAt'] != null
              ? DateTime.parse(json['createdAt'])
              : DateTime.now(),
      rooms: json['rooms'] != null
          ? (json['rooms'] as List).map((r) => Room.fromJson(r)).toList()
          : [],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'total_area': totalArea,
      'image_url': imageUrl,
      'created_at': createdAt.toIso8601String(),
      'rooms': rooms.map((r) => r.toJson()).toList(),
    };
  }
}

class Room {
  String roomName;
  String roomType;
  double length;
  double width;

  Room({
    required this.roomName,
    required this.roomType,
    required this.length,
    required this.width,
  });

  double get area => length * width;

  factory Room.fromJson(Map<String, dynamic> json) {
    return Room(
      roomName: json['room_name'] ?? json['roomName'] ?? '',
      roomType: json['room_type'] ?? json['roomType'] ?? '',
      length: (json['length'] ?? 0).toDouble(),
      width: (json['width'] ?? 0).toDouble(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'room_name': roomName,
      'room_type': roomType,
      'length': length,
      'width': width,
      'area': area,
    };
  }
}

class RoomType {
  static const String livingRoom = 'living_room';
  static const String masterBedroom = 'master_bedroom';
  static const String secondBedroom = 'second_bedroom';
  static const String kitchen = 'kitchen';
  static const String bathroom = 'bathroom';
  static const String balcony = 'balcony';
  static const String study = 'study';
  static const String entrance = 'entrance';

  static const Map<String, String> names = {
    livingRoom: '客厅',
    masterBedroom: '主卧',
    secondBedroom: '次卧',
    kitchen: '厨房',
    bathroom: '卫生间',
    balcony: '阳台',
    study: '书房',
    entrance: '玄关',
  };
}
