import 'product.dart';

class Scheme {
  String id;
  String? houseId;
  String schemeName;
  String? description;
  double totalPrice;
  int status;
  DateTime createdAt;
  List<SchemeDevice> devices;

  Scheme({
    String? id,
    this.houseId,
    required this.schemeName,
    this.description,
    this.totalPrice = 0,
    this.status = 1,
    DateTime? createdAt,
    this.devices = const [],
  })  : id = id ?? DateTime.now().millisecondsSinceEpoch.toString(),
        createdAt = createdAt ?? DateTime.now();

  factory Scheme.fromJson(Map<String, dynamic> json) {
    return Scheme(
      id: json['id']?.toString(),
      houseId: json['house_id']?.toString() ?? json['houseId']?.toString(),
      schemeName: json['scheme_name'] ?? json['schemeName'] ?? '',
      description: json['description'],
      totalPrice: (json['total_price'] ?? json['totalPrice'] ?? 0).toDouble(),
      status: json['status'] ?? 1,
      createdAt: json['created_at'] != null
          ? DateTime.parse(json['created_at'])
          : json['createdAt'] != null
              ? DateTime.parse(json['createdAt'])
              : DateTime.now(),
      devices: json['devices'] != null
          ? (json['devices'] as List).map((d) => SchemeDevice.fromJson(d)).toList()
          : [],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'house_id': houseId,
      'scheme_name': schemeName,
      'description': description,
      'total_price': totalPrice,
      'status': status,
      'created_at': createdAt.toIso8601String(),
      'devices': devices.map((d) => d.toJson()).toList(),
    };
  }
}

class SchemeDevice {
  String? id;
  String? productId;
  String productName;
  String? brandName;
  String roomName;
  int quantity;
  double unitPrice;
  double subtotal;
  String? reason;
  String? imageUrl;
  String? productUrl;

  SchemeDevice({
    this.id,
    this.productId,
    required this.productName,
    this.brandName,
    required this.roomName,
    this.quantity = 1,
    this.unitPrice = 0,
    this.subtotal = 0,
    this.reason,
    this.imageUrl,
    this.productUrl,
  });

  factory SchemeDevice.fromJson(Map<String, dynamic> json) {
    return SchemeDevice(
      id: json['id']?.toString(),
      productId: json['product_id']?.toString() ?? json['productId']?.toString(),
      productName: json['product_name'] ?? json['productName'] ?? '',
      brandName: json['brand_name'] ?? json['brandName'],
      roomName: json['room_name'] ?? json['roomName'] ?? '',
      quantity: json['quantity'] ?? 1,
      unitPrice: (json['unit_price'] ?? json['unitPrice'] ?? 0).toDouble(),
      subtotal: (json['subtotal'] ?? 0).toDouble(),
      reason: json['reason'],
      imageUrl: json['image_url'] ?? json['imageUrl'],
      productUrl: json['product_url'] ?? json['productUrl'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'product_id': productId,
      'product_name': productName,
      'brand_name': brandName,
      'room_name': roomName,
      'quantity': quantity,
      'unit_price': unitPrice,
      'subtotal': subtotal,
      'reason': reason,
      'image_url': imageUrl,
      'product_url': productUrl,
    };
  }
}

class TaskResult {
  final String taskId;
  final String status;
  final Map<String, dynamic>? result;
  final String? error;

  TaskResult({
    required this.taskId,
    required this.status,
    this.result,
    this.error,
  });

  factory TaskResult.fromJson(Map<String, dynamic> json) {
    return TaskResult(
      taskId: json['task_id'] ?? json['taskId'] ?? '',
      status: json['status'] ?? 'pending',
      result: json['result'],
      error: json['error'],
    );
  }
}
