from celery import shared_task
import httpx
from datetime import datetime
from sqlalchemy.orm import Session
from app.core.config import settings
from app.core.database import SessionLocal


@shared_task
def crawl_products_task():
    return {"status": "not_implemented", "message": "Taobao API not configured"}


@shared_task
def sync_product_prices_task():
    return {"status": "not_implemented", "message": "Taobao API not configured"}


@shared_task
def cleanup_expired_tasks_task():
    return {"status": "success", "cleaned": 0}
