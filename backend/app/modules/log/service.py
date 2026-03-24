import re
from datetime import datetime, timedelta
from typing import List, Dict, Optional
from sqlalchemy.orm import Session
from app.modules.log.models import DeviceLog


class LogUploadService:
    MAX_FILE_SIZE = 5 * 1024 * 1024
    ALLOWED_EXTENSIONS = ['.txt', '.log', '.zip']

    @staticmethod
    def validate_file_size(size: int) -> bool:
        return size <= LogUploadService.MAX_FILE_SIZE

    @staticmethod
    def validate_file_extension(filename: str) -> bool:
        return any(filename.lower().endswith(ext) for ext in LogUploadService.ALLOWED_EXTENSIONS)

    @staticmethod
    def parse_log_content(content: str) -> List[Dict]:
        logs = []
        lines = content.split('\n')

        for line in lines:
            line = line.strip()
            if not line:
                continue

            parsed = LogUploadService._parse_line(line)
            if parsed:
                logs.append(parsed)

        return logs

    @staticmethod
    def _parse_line(line: str) -> Optional[Dict]:
        try:
            parts = line.split(' | ')
            if len(parts) >= 3:
                timestamp_str = parts[0].strip()
                level = parts[1].strip()
                message = parts[2].strip()
                timestamp = None
                try:
                    timestamp = datetime.fromisoformat(timestamp_str.replace('Z', '+00:00'))
                except (ValueError, AttributeError):
                    pass
                api_endpoint = None
                error_code = None
                stack_trace = None
                for part in parts[3:]:
                    if part.startswith('endpoint='):
                        api_endpoint = part[9:].strip()
                    elif part.startswith('code='):
                        error_code = part[5:].strip()
                    elif part.startswith('stack='):
                        stack_trace = part[6:].strip()
                return {
                    'log_level': level,
                    'message': message,
                    'api_endpoint': api_endpoint,
                    'error_code': error_code,
                    'stack_trace': stack_trace,
                    'log_timestamp': timestamp,
                }

            bracket_pattern = re.match(r'\[([^\]]+)\]\s*(\w+):\s*(.+)', line)
            if bracket_pattern:
                timestamp_str = bracket_pattern.group(1)
                level = bracket_pattern.group(2)
                message = bracket_pattern.group(3)
                timestamp = None
                try:
                    ts_clean = timestamp_str.replace('Z', '+00:00')
                    timestamp = datetime.fromisoformat(ts_clean)
                except (ValueError, AttributeError):
                    try:
                        timestamp = datetime.strptime(timestamp_str, '%Y-%m-%d %H:%M:%S')
                    except (ValueError, AttributeError):
                        pass
                return {
                    'log_level': level.upper(),
                    'message': message,
                    'api_endpoint': None,
                    'error_code': None,
                    'stack_trace': None,
                    'log_timestamp': timestamp,
                }

            colon_pattern = re.match(r'(\d{4}-\d{2}-\d{2}[T\s]\d{2}:\d{2}:\d{2}[^\s]*)\s+(\w+)[:\s]+(.+)', line)
            if colon_pattern:
                timestamp_str = colon_pattern.group(1)
                level = colon_pattern.group(2)
                message = colon_pattern.group(3)
                timestamp = None
                try:
                    timestamp = datetime.fromisoformat(timestamp_str.replace('Z', '+00:00'))
                except (ValueError, AttributeError):
                    pass
                return {
                    'log_level': level.upper(),
                    'message': message,
                    'api_endpoint': None,
                    'error_code': None,
                    'stack_trace': None,
                    'log_timestamp': timestamp,
                }

            if len(line) > 0:
                return {
                    'log_level': 'INFO',
                    'message': line,
                    'api_endpoint': None,
                    'error_code': None,
                    'stack_trace': None,
                    'log_timestamp': None,
                }

            return None
        except Exception:
            return None

    @staticmethod
    def save_logs(logs: List[Dict], device_id: Optional[str], db: Session) -> int:
        count = 0
        for log_data in logs:
            log_entry = DeviceLog(
                device_id=device_id,
                log_level=log_data.get('log_level', 'INFO'),
                message=log_data.get('message', ''),
                api_endpoint=log_data.get('api_endpoint'),
                error_code=log_data.get('error_code'),
                stack_trace=log_data.get('stack_trace'),
                log_timestamp=log_data.get('log_timestamp'),
            )
            db.add(log_entry)
            count += 1

        db.commit()
        return count

    @staticmethod
    def cleanup_old_logs(days: int, db: Session) -> int:
        cutoff = datetime.now() - timedelta(days=days)
        result = db.query(DeviceLog).filter(DeviceLog.received_at < cutoff).delete()
        db.commit()
        return result