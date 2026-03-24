from fastapi import APIRouter, Depends, Header, UploadFile, File, Form, HTTPException
from typing import Optional
from datetime import datetime
from app.core.dependencies import get_db
from app.modules.log.schemas import UploadLogResponse
from app.modules.log.service import LogUploadService
from app.shared.schema import success_response, error_response

router = APIRouter(prefix="/api/v1/logs", tags=["日志服务"])


@router.post("/upload", response_model=dict)
async def upload_logs(
    device_id: Optional[str] = Header(None, alias="X-Device-ID"),
    file: UploadFile = File(...),
    app_version: str = Form(..., alias="appVersion"),
    platform: str = Form(...),
    os_version: str = Form(..., alias="osVersion"),
    log_start_date: str = Form(..., alias="logStartDate"),
    log_end_date: str = Form(..., alias="logEndDate"),
    db=Depends(get_db),
):
    if not LogUploadService.validate_file_extension(file.filename or ""):
        return error_response(
            code=40002,
            message="不支持的文件格式，仅支持 .txt/.log/.zip"
        )

    contents = await file.read()
    file_size = len(contents)

    if not LogUploadService.validate_file_size(file_size):
        return error_response(
            code=40001,
            message="日志文件过大，最大支持5MB",
            data={"maxSize": LogUploadService.MAX_FILE_SIZE, "actualSize": file_size}
        )

    try:
        content_str = contents.decode('utf-8')
        logs = LogUploadService.parse_log_content(content_str)
    except Exception:
        return error_response(code=40002, message="日志格式错误，无法解析")

    if not logs:
        return error_response(code=40002, message="日志文件为空或格式不正确")

    saved_count = LogUploadService.save_logs(logs, device_id, db)

    upload_id = f"upload_{datetime.now().strftime('%Y%m%d%H%M%S')}"

    return success_response(data={
        "uploadId": upload_id,
        "receivedAt": datetime.now().isoformat(),
        "logCount": saved_count,
        "status": "received"
    })