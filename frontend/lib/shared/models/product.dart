class Product {
  final int id;
  final String productId;
  final String productName;
  final int? brandId;
  final int categoryId;
  final double price;
  final double? originalPrice;
  final String imageUrl;
  final String productUrl;
  final Map<String, dynamic>? specs;
  final double? rating;
  final int salesCount;
  final int status;
  final DateTime? lastSyncedAt;
  final String? brandName;
  final String? categoryName;

  Product({
    required this.id,
    required this.productId,
    required this.productName,
    this.brandId,
    required this.categoryId,
    required this.price,
    this.originalPrice,
    required this.imageUrl,
    required this.productUrl,
    this.specs,
    this.rating,
    required this.salesCount,
    required this.status,
    this.lastSyncedAt,
    this.brandName,
    this.categoryName,
  });

  factory Product.fromJson(Map<String, dynamic> json) {
    return Product(
      id: json['id'] ?? 0,
      productId: json['product_id'] ?? '',
      productName: json['product_name'] ?? '',
      brandId: json['brand_id'],
      categoryId: json['category_id'] ?? 0,
      price: (json['price'] ?? 0).toDouble(),
      originalPrice: json['original_price']?.toDouble(),
      imageUrl: json['image_url'] ?? '',
      productUrl: json['product_url'] ?? '',
      specs: json['specs'],
      rating: json['rating']?.toDouble(),
      salesCount: json['sales_count'] ?? 0,
      status: json['status'] ?? 1,
      lastSyncedAt: json['last_synced_at'] != null
          ? DateTime.parse(json['last_synced_at'])
          : null,
      brandName: json['brand_name'],
      categoryName: json['category_name'],
    );
  }
}

class Brand {
  final int id;
  final String brandName;
  final String brandCode;
  final String? logoUrl;
  final int sortOrder;

  Brand({
    required this.id,
    required this.brandName,
    required this.brandCode,
    this.logoUrl,
    this.sortOrder = 0,
  });

  factory Brand.fromJson(Map<String, dynamic> json) {
    return Brand(
      id: json['id'] ?? 0,
      brandName: json['brand_name'] ?? '',
      brandCode: json['brand_code'] ?? '',
      logoUrl: json['logo_url'],
      sortOrder: json['sort_order'] ?? 0,
    );
  }
}

class Category {
  final int id;
  final String categoryName;
  final String categoryCode;
  final int? parentId;
  final int level;
  final int sortOrder;

  Category({
    required this.id,
    required this.categoryName,
    required this.categoryCode,
    this.parentId,
    required this.level,
    this.sortOrder = 0,
  });

  factory Category.fromJson(Map<String, dynamic> json) {
    return Category(
      id: json['id'] ?? 0,
      categoryName: json['category_name'] ?? '',
      categoryCode: json['category_code'] ?? '',
      parentId: json['parent_id'],
      level: json['level'] ?? 1,
      sortOrder: json['sort_order'] ?? 0,
    );
  }
}
