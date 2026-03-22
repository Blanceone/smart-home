from fastapi import FastAPI, Request
from fastapi.responses import JSONResponse
from app.shared.schema import error_response, AppException


async def app_exception_handler(request: Request, exc: AppException):
    return JSONResponse(
        status_code=400 if exc.code < 20000 else 500,
        content=error_response(code=exc.code, message=exc.message, data=exc.details)
    )


async def generic_exception_handler(request: Request, exc: Exception):
    return JSONResponse(
        status_code=500,
        content=error_response(code=10002, message="系统繁忙，请稍后再试")
    )


def register_exception_handlers(app: FastAPI):
    app.add_exception_handler(AppException, app_exception_handler)
    app.add_exception_handler(Exception, generic_exception_handler)
