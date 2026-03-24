from pydantic import BaseModel, Field
from typing import Optional
from datetime import datetime


class UploadLogResponse(BaseModel):
    upload_id: str
    received_at: datetime
    log_count: int
    status: str = "received"

    class Config:
        from_attributes = True


class LogUploadData(BaseModel):
    max_size: int = Field(description="最大允许文件大小（字节）")
    actual_size: Optional[int] = Field(default=None, description="实际文件大小")