from pydantic import BaseModel, Field, field_validator
from typing import Optional, List
from decimal import Decimal
from datetime import datetime


class RoomBase(BaseModel):
    room_name: str = Field(..., min_length=1, max_length=50)
    room_type: str = Field(..., min_length=1, max_length=30)
    length: Decimal = Field(..., gt=0, decimal_places=2)
    width: Decimal = Field(..., gt=0, decimal_places=2)

    @field_validator("length", "width")
    @classmethod
    def validate_dimensions(cls, v):
        if v <= 0:
            raise ValueError("尺寸必须大于0")
        return v


class RoomCreate(RoomBase):
    pass


class RoomUpdate(BaseModel):
    room_name: Optional[str] = Field(None, min_length=1, max_length=50)
    room_type: Optional[str] = Field(None, min_length=1, max_length=30)
    length: Optional[Decimal] = Field(None, gt=0)
    width: Optional[Decimal] = Field(None, gt=0)
    sort_order: Optional[int] = None


class RoomResponse(RoomBase):
    id: int
    house_id: int
    area: Decimal
    sort_order: Optional[int] = 0

    class Config:
        from_attributes = True


class HouseBase(BaseModel):
    total_area: Decimal = Field(..., gt=0, decimal_places=2)


class HouseCreate(HouseBase):
    rooms: List[RoomCreate] = Field(..., min_length=1)


class HouseUpdate(BaseModel):
    total_area: Optional[Decimal] = Field(None, gt=0)
    image_url: Optional[str] = None


class HouseResponse(BaseModel):
    id: int
    user_id: int
    total_area: Decimal
    image_url: Optional[str]
    status: int
    created_at: datetime
    updated_at: datetime

    class Config:
        from_attributes = True


class HouseDetailResponse(HouseResponse):
    rooms: List[RoomResponse] = []

    class Config:
        from_attributes = True


class HouseListResponse(BaseModel):
    id: int
    total_area: Decimal
    image_url: Optional[str]
    status: int
    created_at: datetime
    room_count: int = 0

    class Config:
        from_attributes = True
