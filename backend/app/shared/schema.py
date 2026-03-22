from typing import Optional, Any, Generic, TypeVar
from pydantic import BaseModel
from datetime import datetime

T = TypeVar("T")


class ResponseBase(BaseModel):
    code: int = 0
    message: str = "success"
    timestamp: int = int(datetime.utcnow().timestamp() * 1000)


class ResponseData(ResponseBase, Generic[T]):
    data: Optional[T] = None

    class Config:
        from_attributes = True


class PaginatedResponseData(ResponseData[T], Generic[T]):
    class Pagination(BaseModel):
        page: int
        page_size: int
        total: int
        total_pages: int

    data: Optional[Pagination] = None


class ErrorResponse(ResponseBase):
    code: int = 10001
    message: str = "参数错误"
    data: Optional[Any] = None


def success_response(data: Any = None, message: str = "success") -> dict:
    return {
        "code": 0,
        "message": message,
        "data": data,
        "timestamp": int(datetime.utcnow().timestamp() * 1000)
    }


def error_response(code: int, message: str, data: Any = None) -> dict:
    return {
        "code": code,
        "message": message,
        "data": data,
        "timestamp": int(datetime.utcnow().timestamp() * 1000)
    }


class AppException(Exception):
    def __init__(self, code: int, message: str, details: Optional[Any] = None):
        self.code = code
        self.message = message
        self.details = details
        super().__init__(self.message)
