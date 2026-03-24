from sqlalchemy import Column, BigInteger, String, Text, DateTime, JSON
from sqlalchemy.sql import func
from app.core.database import Base


class DeviceLog(Base):
    __tablename__ = "device_logs"

    id = Column(BigInteger, primary_key=True, autoincrement=True)
    device_id = Column(String(100), index=True, nullable=True)
    log_level = Column(String(10), nullable=False)
    message = Column(Text, nullable=False)
    context = Column(JSON, nullable=True)
    app_version = Column(String(20), nullable=True)
    platform = Column(String(20), nullable=True)
    os_version = Column(String(50), nullable=True)
    api_endpoint = Column(String(200), nullable=True)
    error_code = Column(String(50), nullable=True)
    stack_trace = Column(Text, nullable=True)
    log_timestamp = Column(DateTime, nullable=True)
    received_at = Column(DateTime, server_default=func.now(), nullable=False)