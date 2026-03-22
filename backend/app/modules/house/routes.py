from fastapi import APIRouter, Depends, HTTPException, status, UploadFile, File
from sqlalchemy.orm import Session
from sqlalchemy.exc import SQLAlchemyError
from typing import List, Optional
from decimal import Decimal
import logging
from app.core.database import get_db
from app.modules.house.models import House, Room
from app.modules.house.schemas import (
    HouseCreate, HouseUpdate, HouseResponse, HouseDetailResponse,
    HouseListResponse, RoomCreate, RoomUpdate
)
from app.shared.schema import success_response, error_response

router = APIRouter(prefix="/api/v1/houses", tags=["户型"])
logger = logging.getLogger(__name__)


@router.post("", response_model=dict)
def create_house(
    house_data: HouseCreate,
    db: Session = Depends(get_db)
):
    try:
        house = House(
            total_area=house_data.total_area
        )
        db.add(house)
        db.flush()

        for idx, room_data in enumerate(house_data.rooms):
            area = (room_data.length * room_data.width).quantize(Decimal("0.01"))
            room = Room(
                house_id=house.id,
                room_name=room_data.room_name,
                room_type=room_data.room_type,
                length=room_data.length,
                width=room_data.width,
                area=area,
                sort_order=idx
            )
            db.add(room)

        db.commit()
        db.refresh(house)

        return success_response(
            data={"house_id": house.id, "total_area": float(house.total_area)},
            message="户型创建成功"
        )
    except SQLAlchemyError as e:
        db.rollback()
        logger.error(f"Database error creating house: {str(e)}")
        return error_response(code=30001, message=f"数据库错误: {str(e)}")
    except Exception as e:
        db.rollback()
        logger.error(f"Error creating house: {str(e)}")
        return error_response(code=30002, message=f"创建户型失败: {str(e)}")


@router.get("", response_model=dict)
def list_houses(
    page: int = 1,
    page_size: int = 20,
    db: Session = Depends(get_db)
):
    query = db.query(House).filter(House.status == 1)
    total = query.count()
    houses = query.order_by(House.created_at.desc()).offset((page - 1) * page_size).limit(page_size).all()

    house_list = []
    for house in houses:
        room_count = db.query(Room).filter(Room.house_id == house.id).count()
        house_list.append({
            "id": house.id,
            "total_area": float(house.total_area) if house.total_area else 0,
            "image_url": house.image_url,
            "status": house.status,
            "created_at": house.created_at.isoformat() if house.created_at else None,
            "room_count": room_count
        })

    return success_response(
        data={
            "list": house_list,
            "pagination": {
                "page": page,
                "page_size": page_size,
                "total": total,
                "total_pages": (total + page_size - 1) // page_size if total > 0 else 0
            }
        }
    )


@router.get("/{house_id}", response_model=dict)
def get_house(
    house_id: int,
    db: Session = Depends(get_db)
):
    house = db.query(House).filter(House.id == house_id, House.status == 1).first()
    if not house:
        return error_response(code=30001, message="户型不存在")

    rooms = db.query(Room).filter(Room.house_id == house_id).order_by(Room.sort_order).all()

    return success_response(
        data={
            "id": house.id,
            "total_area": float(house.total_area) if house.total_area else 0,
            "image_url": house.image_url,
            "status": house.status,
            "created_at": house.created_at.isoformat() if house.created_at else None,
            "rooms": [
                {
                    "id": room.id,
                    "room_name": room.room_name,
                    "room_type": room.room_type,
                    "length": float(room.length) if room.length else 0,
                    "width": float(room.width) if room.width else 0,
                    "area": float(room.area) if room.area else 0,
                    "sort_order": room.sort_order
                }
                for room in rooms
            ]
        }
    )


@router.put("/{house_id}", response_model=dict)
def update_house(
    house_id: int,
    house_data: HouseUpdate,
    db: Session = Depends(get_db)
):
    try:
        house = db.query(House).filter(House.id == house_id, House.status == 1).first()
        if not house:
            return error_response(code=30001, message="户型不存在")

        if house_data.total_area is not None:
            house.total_area = house_data.total_area
        if house_data.image_url is not None:
            house.image_url = house_data.image_url

        if house_data.rooms is not None:
            db.query(Room).filter(Room.house_id == house_id).delete()
            for idx, room_data in enumerate(house_data.rooms):
                area = (room_data.length * room_data.width).quantize(Decimal("0.01"))
                room = Room(
                    house_id=house_id,
                    room_name=room_data.room_name,
                    room_type=room_data.room_type,
                    length=room_data.length,
                    width=room_data.width,
                    area=area,
                    sort_order=idx
                )
                db.add(room)

        db.commit()

        return success_response(
            data={"house_id": house_id},
            message="户型更新成功"
        )
    except SQLAlchemyError as e:
        db.rollback()
        logger.error(f"Database error updating house: {str(e)}")
        return error_response(code=30001, message=f"数据库错误: {str(e)}")
    except Exception as e:
        db.rollback()
        logger.error(f"Error updating house: {str(e)}")
        return error_response(code=30002, message=f"更新户型失败: {str(e)}")


@router.delete("/{house_id}", response_model=dict)
def delete_house(
    house_id: int,
    db: Session = Depends(get_db)
):
    try:
        house = db.query(House).filter(House.id == house_id, House.status == 1).first()
        if not house:
            return error_response(code=30001, message="户型不存在")

        house.status = 0
        db.commit()

        return success_response(
            data={"house_id": house_id},
            message="户型删除成功"
        )
    except SQLAlchemyError as e:
        db.rollback()
        logger.error(f"Database error deleting house: {str(e)}")
        return error_response(code=30001, message=f"数据库错误: {str(e)}")
    except Exception as e:
        db.rollback()
        logger.error(f"Error deleting house: {str(e)}")
        return error_response(code=30002, message=f"删除户型失败: {str(e)}")
