import 'dart:io';
import 'dart:convert';
import 'package:flutter/foundation.dart';
import 'package:path_provider/path_provider.dart';
import '../../shared/models/log_entry.dart';
import '../constants/api_constants.dart';
import 'package:http/http.dart' as http;

class LoggerService {
  static const int _bufferSize = 50;
  static const int _maxFileSize = 5 * 1024 * 1024;
  static const int _maxLogAge = 7;

  final List<LogEntry> _logBuffer = [];
  File? _currentLogFile;
  String? _deviceId;

  static final LoggerService _instance = LoggerService._internal();
  factory LoggerService() => _instance;
  LoggerService._internal();

  Future<void> init({required String deviceId}) async {
    _deviceId = deviceId;
    await _rotateLogFileIfNeeded();
  }

  void log({
    required LogLevel level,
    required String message,
    String? apiEndpoint,
    String? errorCode,
    String? stackTrace,
  }) {
    final entry = LogEntry(
      timestamp: DateTime.now(),
      level: level,
      message: message,
      deviceId: _deviceId,
      appVersion: _getAppVersion(),
      platform: _getPlatform(),
      osVersion: _getOsVersion(),
      apiEndpoint: apiEndpoint,
      errorCode: errorCode,
      stackTrace: stackTrace,
    );

    _logBuffer.add(entry);
    debugPrint('[${entry.levelString}] $message');

    if (_logBuffer.length >= _bufferSize) {
      _flushBuffer();
    }

    if (_currentLogFile != null && _currentLogFile!.lengthSync() >= _maxFileSize) {
      _rotateLogFile();
    }
  }

  void error(String message, {String? apiEndpoint, String? errorCode, String? stackTrace}) {
    log(level: LogLevel.error, message: message, apiEndpoint: apiEndpoint, errorCode: errorCode, stackTrace: stackTrace);
  }

  void warn(String message, {String? apiEndpoint}) {
    log(level: LogLevel.warn, message: message, apiEndpoint: apiEndpoint);
  }

  void info(String message, {String? apiEndpoint}) {
    log(level: LogLevel.info, message: message, apiEndpoint: apiEndpoint);
  }

  void debug(String message) {
    log(level: LogLevel.debug, message: message);
  }

  Future<void> _flushBuffer() async {
    if (_logBuffer.isEmpty) return;

    final file = await _getLogFile();
    final lines = _logBuffer.map((e) => e.toFileLine()).join('\n');
    await file.writeAsString('$lines\n', mode: FileMode.append);
    _logBuffer.clear();
  }

  Future<File> _getLogFile() async {
    if (_currentLogFile != null) return _currentLogFile!;

    final dir = await getApplicationDocumentsDirectory();
    final logDir = Directory('${dir.path}/logs');
    if (!await logDir.exists()) {
      await logDir.create(recursive: true);
    }

    final now = DateTime.now();
    final fileName = 'app_${now.year}${_pad(now.month)}${_pad(now.day)}.log';
    _currentLogFile = File('${logDir.path}/$fileName');
    return _currentLogFile!;
  }

  String _pad(int n) => n.toString().padLeft(2, '0');

  Future<void> _rotateLogFile() async {
    if (_currentLogFile == null) return;

    final dir = await _currentLogFile!.parent;
    final now = DateTime.now();
    final archiveName = 'app_${now.year}${_pad(now.month)}${_pad(now.day)}_${now.millisecondsSinceEpoch}.log';
    await _currentLogFile!.rename('${dir.path}/$archiveName');
    _currentLogFile = null;
  }

  Future<void> _rotateLogFileIfNeeded() async {
    await _getLogFile();
  }

  Future<List<File>> getLogFiles() async {
    final dir = await getApplicationDocumentsDirectory();
    final logDir = Directory('${dir.path}/logs');
    if (!await logDir.exists()) return [];

    final files = await logDir.list().toList();
    return files
        .whereType<File>()
        .where((f) => f.path.endsWith('.log'))
        .toList()
      ..sort((a, b) => b.path.compareTo(a.path));
  }

  Future<List<LogEntry>> getLogs({int? days}) async {
    final files = await getLogFiles();
    final logs = <LogEntry>[];
    final cutoff = days != null ? DateTime.now().subtract(Duration(days: days)) : null;

    for (final file in files) {
      if (cutoff != null) {
        final stat = await file.stat();
        if (stat.modified.isBefore(cutoff)) continue;
      }

      try {
        final content = await file.readAsString();
        final lines = content.split('\n');
        for (final line in lines) {
          if (line.trim().isEmpty) continue;
          final entry = _parseLine(line);
          if (entry != null) {
            if (cutoff == null || entry.timestamp.isAfter(cutoff)) {
              logs.add(entry);
            }
          }
        }
      } catch (_) {}
    }

    return logs;
  }

  LogEntry? _parseLine(String line) {
    try {
      final parts = line.split(' | ');
      if (parts.length < 3) return null;

      final timestamp = DateTime.tryParse(parts[0]);
      if (timestamp == null) return null;

      final level = LogLevel.values.firstWhere(
        (e) => e.name.toUpperCase() == parts[1].toUpperCase(),
        orElse: () => LogLevel.info,
      );

      final message = parts[2];
      String? apiEndpoint;
      String? errorCode;
      String? stackTrace;

      for (final part in parts.skip(3)) {
        if (part.startsWith('endpoint=')) apiEndpoint = part.substring(9);
        if (part.startsWith('code=')) errorCode = part.substring(5);
        if (part.startsWith('stack=')) stackTrace = part.substring(6);
      }

      return LogEntry(
        timestamp: timestamp,
        level: level,
        message: message,
        deviceId: _deviceId,
        apiEndpoint: apiEndpoint,
        errorCode: errorCode,
        stackTrace: stackTrace,
      );
    } catch (_) {
      return null;
    }
  }

  Future<int> cleanOldLogs() async {
    final files = await getLogFiles();
    final cutoff = DateTime.now().subtract(const Duration(days: _maxLogAge));
    int deleted = 0;

    for (final file in files) {
      final stat = await file.stat();
      if (stat.modified.isBefore(cutoff)) {
        await file.delete();
        deleted++;
      }
    }

    return deleted;
  }

  Future<bool> uploadLogs({
    void Function(int current, int total)? onProgress,
  }) async {
    final files = await getLogFiles();
    if (files.isEmpty) return false;

    try {
      final dir = await getTemporaryDirectory();
      final zipFile = File('${dir.path}/logs_${DateTime.now().millisecondsSinceEpoch}.zip');
      final archive = _createZipFromFiles(files);
      await zipFile.writeAsBytes(archive);

      final uri = Uri.parse('${ApiConstants.baseUrl}${ApiConstants.logsUpload}');
      final request = http.MultipartRequest('POST', uri);

      request.headers['X-Device-ID'] = _deviceId ?? 'unknown';
      request.files.add(await http.MultipartFile.fromPath(
        zipFile.path,
        filename: zipFile.path.split('/').last,
      ));

      request.fields['appVersion'] = _getAppVersion();
      request.fields['platform'] = _getPlatform();
      request.fields['osVersion'] = _getOsVersion();
      request.fields['logStartDate'] = DateTime.now().subtract(const Duration(days: 7)).toIso8601String();
      request.fields['logEndDate'] = DateTime.now().toIso8601String();

      final streamedResponse = await request.send().timeout(const Duration(seconds: 60));
      final response = await http.Response.fromStream(streamedResponse);

      await zipFile.delete();

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return data['code'] == 0;
      }

      return false;
    } catch (e) {
      debugPrint('Upload logs failed: $e');
      return false;
    }
  }

  List<int> _createZipFromFiles(List<File> files) {
    final bytes = <int>[];
    for (final file in files) {
      bytes.addAll(file.readAsBytesSync());
      bytes.addAll([0x0A]);
    }
    return bytes;
  }

  String _getAppVersion() {
    return '1.0.0';
  }

  String _getPlatform() {
    return Platform.isAndroid ? 'android' : 'ios';
  }

  String _getOsVersion() {
    return Platform.operatingSystemVersion;
  }

  Future<void> flush() async {
    await _flushBuffer();
  }
}