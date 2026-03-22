from fastapi import APIRouter, Depends, HTTPException, Query, BackgroundTasks
from sqlalchemy.orm import Session
from sqlalchemy import desc
from typing import Optional, List
from decimal import Decimal
from celery.result import AsyncResult
from app.core.database import get_db
from app.modules.scheme.schemas import (
    SchemeGenerateRequest, SchemeResponse, SchemeDetailResponse,
    TaskResponse, AISchemeResponse, AIDeviceItem, ProductMatchRequest, ProductMatchResponse
)
from app.shared.schema import success_response, error_response
from celery_tasks.generation import generate_scheme_task

router = APIRouter(prefix="/api/v1/schemes", tags=["方案"])


@router.post("/generate", response_model=dict)
def generate_scheme(
    request: SchemeGenerateRequest,
    background_tasks: BackgroundTasks,
    db: Session = Depends(get_db)
):
    if not request.house_layout or not request.house_layout.rooms:
        return error_response(code=10001, message="户型数据不完整")

    task_params = {
        "total_area": float(request.house_layout.total_area or 0),
        "rooms": [
            {
                "room_name": room.room_name,
                "room_type": room.room_type,
                "length": float(room.length or 0),
                "width": float(room.width or 0),
                "area": float(room.area or 0)
            }
            for room in request.house_layout.rooms
        ],
        "living_status": request.questionnaire.living_status,
        "resident_count": request.questionnaire.resident_count,
        "has_elderly": request.questionnaire.has_elderly,
        "has_children": request.questionnaire.has_children,
        "has_pets": request.questionnaire.has_pets,
        "preferred_scenarios": request.questionnaire.preferred_scenarios or [],
        "sleep_pattern": request.questionnaire.sleep_pattern,
        "knowledge_level": request.questionnaire.knowledge_level,
        "budget_min": float(request.preferences.budget_min or 0),
        "budget_max": float(request.preferences.budget_max or 100000),
        "preferred_brands": request.preferences.preferred_brands or [],
        "excluded_brands": request.preferences.excluded_brands or []
    }

    celery_task = generate_scheme_task.delay(task_params)

    return success_response(
        data={
            "task_id": celery_task.id,
        },
        message="方案生成任务已创建"
    )


@router.get("/tasks/{task_id}", response_model=dict)
def get_task_status(
    task_id: str,
    db: Session = Depends(get_db)
):
    from app.modules.scheme.models import Task
    task = db.query(Task).filter(Task.task_id == task_id).first()

    celery_result = AsyncResult(task_id)

    result_data = None
    task_status = celery_result.state.lower() if celery_result.state != "PENDING" else "pending"

    if task:
        task_status = task.status if task.status != "processing" else celery_result.state.lower()
        if task.result:
            result_data = task.result
        elif task.error:
            result_data = {"error": task.error}
    elif celery_result.ready():
        try:
            result_data = celery_result.get(timeout=1)
        except Exception as e:
            result_data = {"error": str(e)}

    return success_response(
        data={
            "task_id": task_id,
            "status": task_status,
            "result": result_data,
        }
    )
