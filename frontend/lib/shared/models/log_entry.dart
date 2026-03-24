enum LogLevel { debug, info, warn, error }

class LogEntry {
  final DateTime timestamp;
  final LogLevel level;
  final String message;
  final String? deviceId;
  final String? appVersion;
  final String? platform;
  final String? osVersion;
  final String? apiEndpoint;
  final String? errorCode;
  final String? stackTrace;

  LogEntry({
    required this.timestamp,
    required this.level,
    required this.message,
    this.deviceId,
    this.appVersion,
    this.platform,
    this.osVersion,
    this.apiEndpoint,
    this.errorCode,
    this.stackTrace,
  });

  String get levelString {
    switch (level) {
      case LogLevel.debug:
        return 'DEBUG';
      case LogLevel.info:
        return 'INFO';
      case LogLevel.warn:
        return 'WARN';
      case LogLevel.error:
        return 'ERROR';
    }
  }

  Map<String, dynamic> toJson() {
    return {
      'timestamp': timestamp.toIso8601String(),
      'level': levelString,
      'message': message,
      'deviceId': deviceId,
      'appVersion': appVersion,
      'platform': platform,
      'osVersion': osVersion,
      'apiEndpoint': apiEndpoint,
      'errorCode': errorCode,
      'stackTrace': stackTrace,
    };
  }

  factory LogEntry.fromJson(Map<String, dynamic> json) {
    return LogEntry(
      timestamp: DateTime.parse(json['timestamp']),
      level: LogLevel.values.firstWhere(
        (e) => e.name.toUpperCase() == json['level'].toUpperCase(),
        orElse: () => LogLevel.info,
      ),
      message: json['message'] ?? '',
      deviceId: json['deviceId'],
      appVersion: json['appVersion'],
      platform: json['platform'],
      osVersion: json['osVersion'],
      apiEndpoint: json['apiEndpoint'],
      errorCode: json['errorCode'],
      stackTrace: json['stackTrace'],
    );
  }

  String toFileLine() {
    final parts = [
      timestamp.toIso8601String(),
      levelString,
      message,
    ];
    if (apiEndpoint != null) parts.add('endpoint=$apiEndpoint');
    if (errorCode != null) parts.add('code=$errorCode');
    if (stackTrace != null) parts.add('stack=$stackTrace');
    return parts.join(' | ');
  }
}