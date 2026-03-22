import 'dart:convert';
import 'package:http/http.dart' as http;
import '../constants/api_constants.dart';

class ApiService {
  static final ApiService _instance = ApiService._internal();
  factory ApiService() => _instance;
  ApiService._internal();

  Map<String, String> get _headers => {
        'Content-Type': 'application/json',
      };

  Future<ApiResponse> get(
    String endpoint, {
    Map<String, dynamic>? params,
  }) async {
    try {
      var uri = Uri.parse('${ApiConstants.baseUrl}$endpoint');
      if (params != null && params.isNotEmpty) {
        uri = uri.replace(queryParameters: params.map((k, v) => MapEntry(k, v.toString())));
      }

      final response = await http.get(
        uri,
        headers: _headers,
      ).timeout(const Duration(seconds: 30));

      return _handleResponse(response);
    } catch (e) {
      return ApiResponse(success: false, message: e.toString());
    }
  }

  Future<ApiResponse> post(
    String endpoint, {
    Map<String, dynamic>? body,
  }) async {
    try {
      final uri = Uri.parse('${ApiConstants.baseUrl}$endpoint');

      final response = await http.post(
        uri,
        headers: _headers,
        body: body != null ? jsonEncode(body) : null,
      ).timeout(const Duration(seconds: 60));

      return _handleResponse(response);
    } catch (e) {
      return ApiResponse(success: false, message: e.toString());
    }
  }

  ApiResponse _handleResponse(http.Response response) {
    try {
      final data = jsonDecode(response.body);
      final code = data['code'] ?? response.statusCode;
      final message = data['message'] ?? '';
      final responseData = data['data'];

      if (response.statusCode == 200 || code == 0) {
        return ApiResponse(
          success: true,
          data: responseData,
          message: message,
        );
      } else {
        return ApiResponse(
          success: false,
          data: responseData,
          message: message.isNotEmpty ? message : 'Request failed',
          statusCode: response.statusCode,
        );
      }
    } catch (e) {
      return ApiResponse(
        success: false,
        message: 'Failed to parse response: $e',
      );
    }
  }
}

class ApiResponse {
  final bool success;
  final dynamic data;
  final String message;
  final int? statusCode;

  ApiResponse({
    required this.success,
    this.data,
    this.message = '',
    this.statusCode,
  });
}
