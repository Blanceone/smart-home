class ApiConstants {
  static const String baseUrl = 'http://8.137.174.58:8000';
  static const String apiVersion = '/api/v1';

  static const String schemesGenerate = '$apiVersion/schemes/generate';
  static const String schemesTasks = '$apiVersion/schemes/tasks';

  static const String products = '$apiVersion/products';
  static const String productsMatch = '$apiVersion/products/match';
  static const String brands = '$apiVersion/brands';
  static const String categories = '$apiVersion/categories';

  static const String logsUpload = '$apiVersion/logs/upload';

  static const String health = '/health';
}
